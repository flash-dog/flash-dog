package com.skymobi.monitor.service;

import com.skymobi.monitor.model.Alert;
import junit.framework.TestCase;

/**
 * @author Hill.Hu
 */
public class AlertServiceTest extends TestCase {
    AlertService alertService = new AlertService();
    private Alert alert;

    @Override
    public void setUp() throws Exception {
        alertService.init();
        alert = new Alert();
        alert.setTitle("alert1");
        alert.setProjectName("project1");
    }

    public void test_is_need_notify() throws Exception {
        assertFalse(alertService.isNeedNotify(null));
        assertTrue(alertService.isNeedNotify(alert));

        alert.setTitle("alert2");
        assertTrue(alertService.isNeedNotify(alert));
        alert.setTitle("alert1");
        alert.setProjectName("project2");
        assertTrue(alertService.isNeedNotify(alert));
    }
}
