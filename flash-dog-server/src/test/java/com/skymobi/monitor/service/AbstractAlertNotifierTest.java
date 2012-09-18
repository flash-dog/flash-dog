package com.skymobi.monitor.service;

import com.skymobi.monitor.model.Alert;
import junit.framework.TestCase;

/**
 * @author Hill.Hu
 */
public class AbstractAlertNotifierTest extends TestCase {
    AbstractAlertNotifier alertNotifier = new HttpAlertNotifier();
    private Alert alert;

    @Override
    public void setUp() throws Exception {
        alert = new Alert();
    }

    public void test_is_need() throws Exception {
        alert.setLevel("WARN");
        alertNotifier.setLevel("ERROR");
        assertFalse(alertNotifier.isNeedNotify(alert));
        alertNotifier.setLevel("WARN");
        assertTrue(alertNotifier.isNeedNotify(alert));

        alert.setLevel("ERROR");
        alertNotifier.setLevel("WARN");
        assertTrue(alertNotifier.isNeedNotify(alert));
        alertNotifier.setLevel("ERROR");
        assertTrue(alertNotifier.isNeedNotify(alert));
    }
}
