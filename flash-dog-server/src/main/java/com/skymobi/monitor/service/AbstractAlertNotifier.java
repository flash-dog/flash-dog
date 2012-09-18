package com.skymobi.monitor.service;

import com.google.common.collect.Lists;
import com.skymobi.monitor.model.Alert;

import java.util.List;

/**
 * @author Hill.Hu
 */
public abstract class AbstractAlertNotifier implements AlertListener {
    private String level = "WARN";
    private transient List<String> levels = Lists.newArrayList("WARN", "ERROR");

    @Override
    public void notify(Alert alert) {
        if (isNeedNotify(alert)) {
            _notify(alert);
        }
    }

    abstract void _notify(Alert alert);

    protected boolean isNeedNotify(Alert alert) {
        return levels.indexOf(alert.getLevel()) >= levels.indexOf(this.level);

    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
