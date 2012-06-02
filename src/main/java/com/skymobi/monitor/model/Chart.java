package com.skymobi.monitor.model;

import com.mongodb.CommandResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Author: Hill.Hu
 * Email:  hill.hu@sky-mobi.com
 * Date: 11-12-7 下午1:43
 * 统计类
 */
public class Chart implements IdentifyObject {

    private static Logger logger = LoggerFactory.getLogger(Chart.class);

    private String name;
    /**
     * mongodb的查询脚本
     */
    private String query;

    public Chart(String name) {
        this.name = name;
    }

    public Chart() {
    }

    public List findData() {

        logger.debug("find stats  by {}", query);
        CommandResult result =null ;//mongoDb.runCmd(query);
        logger.debug("stats mongo result {}", result);
        Object data = result.get("retval");

        return (List) data;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chart)) return false;

        Chart that = (Chart) o;

        return StringUtils.equals(that.getName(), this.getName());
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

}
