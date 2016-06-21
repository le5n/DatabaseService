package com.epam.json;

import java.util.Arrays;

public class JsonObject {
    private String command;
    private String[] params;

    public JsonObject() {
    }

    public JsonObject(String command, String... params) {
        this.command = command;
        this.params = params.clone();
    }

    public String getCommand() {
        return command;
    }

    public String[] getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "JsonObject{" +
                "command='" + command + '\'' +
                ", params=" + Arrays.toString(params) +
                '}';
    }
}
