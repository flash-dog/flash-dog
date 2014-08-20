/**
 * Copyright (C) 2012 skymobi LTD
 *
 * Licensed under GNU GENERAL PUBLIC LICENSE  Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.skymobi.monitor.security;

import com.google.common.collect.Lists;
import com.skymobi.monitor.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

/**
 * Author: Hill.Hu
 * keep user data in mongodb
 */
public class MongoUserManager implements UserManager, UserDetailsService {
    private static Logger logger = LoggerFactory.getLogger(MongoUserManager.class);
    public static final String COLLECTION_NAME_USER = "system_user";
    public static final String LIST_SEPARATOR = ",";

    private MongoTemplate mongoTemplate;
    /**
     * admin user ,separated by comma ,
     */
    private String systemAdmins;

    private String systemAdminInitPassword = "123456";

    @Resource
    private EmailService emailService;

    public void init() {
        for (String username : loadAdmins()) {
            User user = loadUserByUsername(username);
            if (user == null) {
                logger.info("create a  system user [{}] ", username);
                user = new User(username, systemAdminInitPassword, true);
                try {
                    registerUser(user);
                } catch (RegisterException e) {
                    logger.error("init system user err", e);
                }
            }
        }

    }

    @Override
    public List<User> listUsers() {

        return mongoTemplate.find(new Query(), User.class, COLLECTION_NAME_USER);

    }

    public void registerUser(User user) throws RegisterException {
        Assert.hasLength(user.getUsername(), "username can't  be empty");
        Assert.hasLength(user.getPassword(), "password can't  be empty");
        if (loadUserByUsername(user.getUsername()) != null) {
            throw new RegisterException("err.username_has_exist");
        }
        logger.info("register a user = {} ", user);
        saveUser(user);

    }

    public void removeUser(String username) {
        logger.info("remove user by username ={}", username);
        User user = new User();
        user.setUsername(username);
        mongoTemplate.remove(user, COLLECTION_NAME_USER);

    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("try load user by username={}", username);

        User user = mongoTemplate.findOne(new Query(Criteria.where("username").is(username)),
                User.class, COLLECTION_NAME_USER);
        if(user!=null && isSystemAdmin(username))    {
            user.setAuthorities( AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        }
        return user;
    }

    @Override
    public boolean isSystemAdmin(String username) {
        return loadAdmins().contains(username);
    }

    /**
     * monitor user by system administrator
     *
     * @param user
     */
    @Override
    public void monitorUser(User user) {
        Assert.isTrue(!isSystemAdmin(user.getUsername()),
                "system user can't be monitor, please do that by change system properties config ,username=" + user.getUsername());
        User dbUser = loadUserByUsername(user.getUsername());
        Assert.notNull(dbUser);
        if (dbUser.isEnabled() != user.isEnabled()) {
            logger.info("change user enabled  {}", user);
            dbUser.setEnabled(user.isEnabled());
            dbUser.setPassword(user.getPassword());
            dbUser.setEmail(user.getEmail());
            dbUser.setPhone(user.getPhone());

            saveUser(dbUser);

        }
    }

    private void saveUser(User user) {
        Assert.hasLength(user.getUsername(), "username can't  be empty");
        Assert.hasLength(user.getPassword(), "password can't  be empty");
        mongoTemplate.save(user, COLLECTION_NAME_USER);
    }

    @Override
    public List<String> loadAdmins() {
        return Lists.newArrayList(systemAdmins.split(LIST_SEPARATOR));
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void setSystemAdmins(String systemAdmins) {
        this.systemAdmins = systemAdmins;
    }

    public void setSystemAdminInitPassword(String systemAdminInitPassword) {
        this.systemAdminInitPassword = systemAdminInitPassword;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
