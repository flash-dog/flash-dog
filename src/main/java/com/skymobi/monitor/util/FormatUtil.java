package com.skymobi.monitor.util;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * @author Hill.Hu
 */

public class FormatUtil {

    public String toJson(Object obj) {

        return new Gson().toJson(obj);
    }
    public String join(Iterator iterator){
       return StringUtils.join(iterator,",");
    }
    public String join(List list){
        return StringUtils.join(list.toArray(), ",");
    }
}
