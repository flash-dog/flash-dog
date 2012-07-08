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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Hill.Hu
 */
public class LogQuery {
    private String start;
    private String end;
    private String level;
    private String keyWord;


    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public DBObject toQuery() throws ParseException {
        BasicDBObject query = new BasicDBObject();
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map gt = new HashMap();
        if (this.getStart() != null && !this.getStart().isEmpty()) {
            Date startDate = formate.parse(this.getStart());
            gt.put("$gt", startDate);
        }
        if (this.getEnd() != null && !this.getEnd().isEmpty()) {
            Date endDate = formate.parse(this.getEnd());
            gt.put("$lt", endDate);
        }
        if (StringUtils.isNotEmpty(getLevel())) {
            query.put("level", getLevel());
        }
        if (!gt.isEmpty())
            query.put("timestamp", gt);
        if (StringUtils.isNotEmpty(keyWord)) {
            Pattern pattern = Pattern.compile(keyWord);
            query.put("message", pattern);

        }
        return query;
    }
}
