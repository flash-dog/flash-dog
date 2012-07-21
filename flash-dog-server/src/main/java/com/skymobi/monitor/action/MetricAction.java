/**
 * Copyright (C) 2012 skymobi LTD
 *
 * Licensed under GNU GENERAL PUBLIC LICENSE  Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.skymobi.monitor.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.skymobi.monitor.model.MetricValue;
import com.skymobi.monitor.model.Project;
import com.skymobi.monitor.model.TimeRange;
import com.skymobi.monitor.service.ProjectService;

/**
 * Author: Hill.Hu,steven.zheng
 */
@Controller
public class MetricAction {
    private static Logger logger = LoggerFactory.getLogger(MetricAction.class);

    @Resource
    private ProjectService projectService;
    private SimpleDateFormat sdf = new SimpleDateFormat("M月dd HH:mm");


    @RequestMapping(value = "/projects/{projectName}/metrics", method = RequestMethod.GET)
    public @ResponseBody ModelMap  renderVar(ModelMap map, @PathVariable String projectName, String metricName,String newChartTitle) {
    	Project project = projectService.findProject(projectName);
    	if(StringUtils.isNotEmpty(newChartTitle)){
	    		project.getViews().put(newChartTitle, metricName);
	    		projectService.saveProject(project);
    	}
    	List<List> varData = Lists.newArrayList();
        String[] metrics = metricName.split(",");
        caculatePoints(metrics,varData,project);
        map.put("metricNames",metrics);
        map.put("data", varData);
        return  map;
    }
    
    @RequestMapping(value = "/projects/{projectName}/metricsDelete", method = RequestMethod.GET)
    public @ResponseBody ModelMap  metricsDelete(ModelMap map, @PathVariable String projectName, String title) {
    	Project project = projectService.findProject(projectName);
    	if(StringUtils.isNotEmpty(title)){
    		if(project.getViews().containsKey(title)){
	    		project.getViews().remove(title);
	    		projectService.saveProject(project);
    		}
    	}
    	return  map;
    }

    
    /**
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
    

    @RequestMapping(value = "/projects/{projectName}/metrics/timeRange", method = RequestMethod.POST)
    public String save(@PathVariable String projectName, TimeRange timeRange) {
        Project project = projectService.findProject(projectName);

        project.setTimeRange(timeRange);

        projectService.saveProject(project);
        return "redirect:/projects/" + projectName;
    }

}
