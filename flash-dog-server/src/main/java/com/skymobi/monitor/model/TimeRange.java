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
package com.skymobi.monitor.model;

import java.util.Calendar;
import java.util.Date;

/**
* @author hill.hu
 *
 * 时间范围
 */
public class TimeRange {

    private transient Date now = new Date();
    private int last=1;
    /**
     * 时间单位，参见Calendar
     * hour =10
     * MINUTE=12
     * DATE = 5
     */
    private int unit=5;
    private transient Date start, end;

    public TimeRange(int last, int unit) {
        this.last = last;
        this.unit = unit;
    }

    public TimeRange() {
    }

    public static TimeRange lastDay() {
        return new TimeRange(1, 5);
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
        if (last > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(unit, -last);
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
