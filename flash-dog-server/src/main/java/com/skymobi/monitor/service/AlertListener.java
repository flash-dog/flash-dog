package com.skymobi.monitor.service;

import com.skymobi.monitor.model.Alert;

/**
 * author: Hill.Hu
 */
public interface AlertListener {

    public void notify(Alert alert);
}
