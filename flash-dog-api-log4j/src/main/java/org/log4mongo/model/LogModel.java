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
    private String logname;
    private String matchstr;
    private Map<Integer,String> relation;

    public LogModel(DBObject dbObject){
        _id = dbObject.get("_id").toString();
        logname = dbObject.get("logname").toString();
        matchstr = dbObject.get("matchstr").toString();
        relation = (Map)dbObject.get("relation");
    }

    public Map formatMessage(String logname,String message){
        System.out.println(" LogModel format message="+message);
        //日志名称完全匹配
        if(this.logname.equals(logname)){
            Pattern pattern = Pattern.compile(matchstr);
            Matcher matcher = pattern.matcher(message);
            if(matcher.find()) {
                Map map = new HashMap();
                map.put(FieldEnum.KEY_LOGMODEL_ID.getName(),this.get_id());
                for (int i =0;i<=matcher.groupCount();i++) {
                    if(relation.containsKey("field"+i)){
                        map.put(relation.get("field"+i),matcher.group(i));
                    }
                }
                System.out.println(" LogModel mapsize="+map.size());
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

    public String getLogname() {
        return logname;
    }

    public void setLogname(String logname) {
        this.logname = logname;
    }

    public String getMatchstr() {
        return matchstr;
    }

    public void setMatchstr(String matchstr) {
        this.matchstr = matchstr;
    }

    public Map getRelation() {
        return relation;
    }

    public void setRelation(Map relation) {
        this.relation = relation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogModel logModel = (LogModel) o;

        if (_id != null ? !_id.equals(logModel._id) : logModel._id != null) return false;
        if (logname != null ? !logname.equals(logModel.logname) : logModel.logname != null) return false;
        if (matchstr != null ? !matchstr.equals(logModel.matchstr) : logModel.matchstr != null) return false;
        if (relation != null ? !relation.equals(logModel.relation) : logModel.relation != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (logname != null ? logname.hashCode() : 0);
        result = 31 * result + (matchstr != null ? matchstr.hashCode() : 0);
        result = 31 * result + (relation != null ? relation.hashCode() : 0);
        return result;
    }
}
