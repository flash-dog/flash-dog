package com.skymobi.monitor.util;

import com.google.common.collect.Lists;
import com.skymobi.monitor.model.MetricValue;
import com.skymobi.monitor.model.Project;

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

        List<MetricValue> max = metricList.get(0);
        for (List list : metricList) {
            if (list.size() > max.size())
                max = list;
        }
        ArrayList<List> rows = Lists.newArrayList();

        rows.add(getColumnNames(metricList));
        for (MetricValue metricValue : max) {
            List row = Lists.newArrayList(sdf.format(new Date(metricValue.getTimeStamp())));
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
            String metricName = list.get(0).getName();
            columns.add(metricName);
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

    /**
     * @deprecated
     * 计算合并所需的点
     * @param metrics
     * @param varData
     * @param project
     */
    private void caculatePoints(String[] metrics,List<List> varData,Project project){
        List<MetricValue>[] datalist = new ArrayList[metrics.length];
        int[] cucors = new int[metrics.length];
        for(int i=0;i<metrics.length;i++){
            cucors[i]=0;
            datalist[i]=project.findMetricData(metrics[i]);
            //将坐标最多的放在数组的第一位，作为主坐标
            if(i!=0&&datalist[i].size()>datalist[0].size()){
                List<MetricValue> templ =datalist[i];
                datalist[i]=datalist[0];
                datalist[0]=templ;
                String temp = metrics[i];
                metrics[i] = metrics[0];
                metrics[0] =temp;
            }
            varData.add(datalist[i]);
        }
        List<MetricValue> mainMetric=datalist[0];
        for(MetricValue metric:mainMetric){
            List formatResult = new ArrayList();
            formatResult.add(sdf.format(new Date(metric.getTimeStamp())));
            formatResult.add(metric.getValue());
            for(int j=1;j<datalist.length;j++){
                List<MetricValue> AdditionalMetric=datalist[j];
                double v1 =0;
                double v2 =0;
                for(int i=cucors[j];i<AdditionalMetric.size()-1;i++){
                    if(AdditionalMetric.get(i).getTimeStamp()>metric.getTimeStamp()) break;
                    if(AdditionalMetric.get(i).getTimeStamp()<=metric.getTimeStamp()
                            &&AdditionalMetric.get(i+1).getTimeStamp()>metric.getTimeStamp()){
                        cucors[j]=i;
                        v1 = AdditionalMetric.get(cucors[j]).getValue();
                        v2 = AdditionalMetric.get(cucors[j]+1).getValue();
                        break;
                    }
                }
                Double result =null;
                if(cucors[j]<AdditionalMetric.size()-1)
                {
                    //算出某个时间点的值
                    double v3 = v2-v1;
                    long s1 = AdditionalMetric.get(cucors[j]+1).getTimeStamp()-metric.getTimeStamp();
                    long s2 = AdditionalMetric.get(cucors[j]+1).getTimeStamp()-AdditionalMetric.get(cucors[j]).getTimeStamp();
                    result =v2-((double)s1/s2)*v3;
                }
                formatResult.add(result);
            }
            varData.add(formatResult);

        }

    }
}
