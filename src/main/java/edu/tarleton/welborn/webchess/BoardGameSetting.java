package edu.tarleton.welborn.webchess;

import java.util.List;

// This is barely going to interface with the Java side of this system, it's mostly a JSON container,
// so we'll forgoe any special Java features and just keep it a simple, Java bean.
public class BoardGameSetting {
    private String key;
    private String value;
    private String type;
    private List<String> options;
    
    public BoardGameSetting(String key, String value, String type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }
    
    public BoardGameSetting(String key, String value, String type, List<String> options) {
        this.key = key;
        this.value = value;
        this.type = type;
        this.options = options;
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}