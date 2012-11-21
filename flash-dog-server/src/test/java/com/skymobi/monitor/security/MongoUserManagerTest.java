package com.skymobi.monitor.security;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * author: Hill.Hu
 */
@ContextConfiguration(locations = {"classpath:spring/env-config.xml", "classpath:spring/services-config.xml", "classpath:/spring/email-notice.xml"})
public class MongoUserManagerTest extends AbstractJUnit4SpringContextTests {
    @Resource
    MongoUserManager mongoUserManager;

    @Test
    public void test_save() throws Exception {
        Collection<? extends GrantedAuthority> authorities=new ArrayList<GrantedAuthority>();
        String username = "test";
        mongoUserManager.removeUser(username);
        assertNull(mongoUserManager.loadUserByUsername(username));
        mongoUserManager.registerUser(new User(username,"123",authorities));
        assertEquals(username, mongoUserManager.loadUserByUsername(username).getUsername());
        assertTrue(mongoUserManager.listUsers().size() > 0);

    }
}
