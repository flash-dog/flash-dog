package com.skymobi.monitor.security;

import junit.framework.TestCase;

/**
 * @author Hill.Hu
 */
public class SimpleAuthzTest extends TestCase {
    SimpleAuthz authz=new SimpleAuthz();

    @Override
    public void setUp() throws Exception {

    }

    public void test_grant() throws Exception {
        assertTrue(authz.noneGranted("ROLE_ADMIN"));
    }
}
