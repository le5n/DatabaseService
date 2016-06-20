package com.epam.entity.relflection;

import com.epam.entity.json.JsonObject;

import java.lang.reflect.Method;

public final class ReflectionAPI {
    private ReflectionAPI() {}

    public static <T> Object getRequestedObjectFromJson(JsonObject jsonObject, T object) throws Exception {
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(jsonObject.getCommand())) {
                return method.invoke(object, jsonObject.getParams());
            }
        }
        return null;
    }
}
