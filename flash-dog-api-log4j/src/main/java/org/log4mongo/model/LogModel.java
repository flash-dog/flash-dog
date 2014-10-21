package org.log4mongo.model;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.log4mongo.enums.FieldEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhengbo.zb on 2014/10/10.
 */
public class LogModel {
    private String _id;
    private String logName;
    private String logModelName;
    private String matchStr;
    private Map<Integer,String> relation;

    public LogModel(DBObject dbObject){
        _id = dbObject.get("_id").toString();
        logName = dbObject.get(FieldEnum.KEY_LOGGER_NAME.getName()).toString();
        matchStr = dbObject.get(FieldEnum.KEY_MATCH_STR.getName()).toString();
        logModelName = dbObject.get(FieldEnum.KEY_RELATION.getName()).toString();
        relation = (Map)dbObject.get("relation");
    }

    public Map formatMessage(String logname,String message){
        //日志名称完全匹配
        if(this.logName.equals(logname)){
            Pattern pattern = Pattern.compile(matchStr);
            Matcher matcher = pattern.matcher(message);
            if(matcher.find()) {
                Map map = new HashMap();
                map.put(FieldEnum.KEY_LOGMODEL_ID.getName(),this.get_id());
                map.put(FieldEnum.KEY_LOGGER_MODEL_NAME.getName(),this.getLogModelName());
                for (int i =0;i<=matcher.groupCount();i++) {
                    if(relation.containsKey("field"+i)){
                        map.put(relation.get("field"+i),matcher.group(i));
                    }
                }
                return map;
            }
        }
        return null;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getLogModelName() {
        return logModelName;
    }

    public void setLogModelName(String logModelName) {
        this.logModelName = logModelName;
    }

    public String getMatchStr() {
        return matchStr;
    }

    public void setMatchStr(String matchStr) {
        this.matchStr = matchStr;
    }

    public Map<Integer, String> getRelation() {
        return relation;
    }

    public void setRelation(Map<Integer, String> relation) {
        this.relation = relation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogModel logModel = (LogModel) o;

        if (_id != null ? !_id.equals(logModel._id) : logModel._id != null) return false;
        if (logModelName != null ? !logModelName.equals(logModel.logModelName) : logModel.logModelName != null)
            return false;
        if (logName != null ? !logName.equals(logModel.logName) : logModel.logName != null) return false;
        if (matchStr != null ? !matchStr.equals(logModel.matchStr) : logModel.matchStr != null) return false;
        if (relation != null ? !relation.equals(logModel.relation) : logModel.relation != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (logName != null ? logName.hashCode() : 0);
        result = 31 * result + (logModelName != null ? logModelName.hashCode() : 0);
        result = 31 * result + (matchStr != null ? matchStr.hashCode() : 0);
        result = 31 * result + (relation != null ? relation.hashCode() : 0);
        return result;
    }
}
