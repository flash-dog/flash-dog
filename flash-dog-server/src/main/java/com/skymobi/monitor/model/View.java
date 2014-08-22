package com.skymobi.monitor.model;

import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

/**
 * @author Hill.Hu
 */
public class View {
    @Id
    private String id;
    private String name;
    private List<String> projectNames;
    private Date createTime=new Date();
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "View{" +
                "name='" + name + '\'' +
                ", projectNames=" + projectNames +
                '}';
    }
}
