package com.skymobi.monitor.util;

import com.google.common.collect.Lists;
import com.skymobi.monitor.model.MetricValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Hill.Hu
 */
@SuppressWarnings("unchecked")
public class ChartUtil {
    private static SimpleDateFormat sdf = new SimpleDateFormat("M月dd HH:mm");

    /**
     * 格式成google图表，参见单元测试
     * 如：
     * [[time, request, pay],
     * [7月01 00:00, 10.0, 0.0],
     * [8月01 00:00, 12.0, 2.0],
     * [9月01 00:00, 13.0, 2.0]]
     *
     * @param metricList
     * @return
     */
    public static List<List> format(List<List<MetricValue>> metricList) {
        return format(metricList,true);
    }
    public static List<List> format(List<List<MetricValue>> metricList,boolean timeAsStr) {
        ArrayList<List> rows = Lists.newArrayList();
        if (metricList == null || metricList.isEmpty())
            return rows;
        List<MetricValue> max = metricList.get(0);
        for (int i = 0; i < metricList.size(); i++) {
            List list = metricList.get(i);
            if (list.size() > max.size())
                max = list;
            if (list.isEmpty())
                metricList.remove(list);
        }

        rows.add(getColumnNames(metricList));
        for (MetricValue metricValue : max) {
            Object time = metricValue.getTimeStamp();
            if(timeAsStr){
                time = sdf.format(new Date(metricValue.getTimeStamp()));
            }

            List row = Lists.newArrayList(time);
            for (List<MetricValue> list : metricList) {
                row.add(findValue(metricValue.getTimeStamp(), list));
            }
            rows.add(row);
        }
        return rows;
    }
    private static List getColumnNames(List<List<MetricValue>> metricList) {
        List columns = Lists.newArrayList("time");
        for (List<MetricValue> list : metricList) {
            if(list.size()>0){
                String metricName = list.get(0).getName();
                columns.add(metricName);
            }

        }
        return columns;
    }

    /**
     * 寻找最接近指定时间点的值
     * 如果找不到则返回上一个时间点的值
     *
     * @param timeStamp
     * @param list
     * @return
     */
    private static double findValue(long timeStamp, List<MetricValue> list) {
        double value = 0;
        for (MetricValue mv : list) {
            if (mv.getTimeStamp() <= timeStamp) {
                value = mv.getValue();
            }
        }
        return value;
    }

}
