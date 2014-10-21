package org.log4mongo.enums;

/**
 * Created by zhengbo.zb on 2014/10/17.
 */
public enum  FieldEnum {
    KEY_TIMESTAMP("timestamp"),

    KEY_LOGGER_NAME("loggerName"),

    KEY_LOGGER_MODEL_NAME("logModelName"),

    KEY_RELATION("relation"),

    KEY_MATCH_STR("matchStr"),

    KEY_LOGMODEL_ID("logModelId");

    private String name;
    FieldEnum(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
