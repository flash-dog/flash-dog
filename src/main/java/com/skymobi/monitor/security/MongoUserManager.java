package com.skymobi.monitor.security;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Author: Hill.Hu
 * Email:  hill.hu@sky-mobi.com
 * Date: 11-12-23 上午10:00
 */
@Component
public class MongoUserManager implements UserManager {
    private static Logger logger = LoggerFactory.getLogger(MongoUserManager.class);

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public List<User> listUsers() {
        try {

            List<User> userList = mongoTemplate.find( new Query(),User.class,"system_user");
            return (List<User>) userList;
        } catch (Exception e) {
            logger.error("get user list from oms fail", e);
        }
        return Lists.newArrayList();
    }

    public void registerUser(User user) {
        mongoTemplate.save(user,"system_user");

    }

}
