package com.skymobi.monitor.model;

import java.util.List;

/**
 * @author Hill.Hu
 */
public class View {
    private String name;
    private List<String> projectNames;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getProjectNames() {
        return projectNames;
    }

    public void setProjectNames(List<String> projectNames) {
        this.projectNames = projectNames;
    }

    @Override
    public String toString() {
        return "View{" +
                "name='" + name + '\'' +
                ", projectNames=" + projectNames +
                '}';
    }
}
