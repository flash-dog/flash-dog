package com.skymobi.monitor.model;

import java.util.Calendar;
import java.util.Date;

/**
 * Author: Hill.Hu
 * Email:  hill.hu@sky-mobi.com
 * Date: 11-12-2 下午5:22
 * 时间范围
 */
public class TimeRange {

    private transient Date now=new Date();
    private int last;
    /**
     * 时间单位，参见Calendar
     * hour =10
     * MINUTE=12
     * DATE = 5
     */
    private int unit;
    private transient Date start,end;

    public TimeRange(int last, int unit) {
        this.last = last;
        this.unit = unit;
    }

    public TimeRange() {
    }

    public static TimeRange lastDay(){
        return new TimeRange(1,5);
    }
    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public Date getStart() {
        if(last>0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(unit,-last);
            return calendar.getTime();
        }
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getNow() {
        return now;
    }

    public void setNow(Date now) {
        this.now = now;
    }
}
