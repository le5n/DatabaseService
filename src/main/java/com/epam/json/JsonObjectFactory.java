package com.epam.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class JsonObjectFactory {
    private JsonObjectFactory() {}

    private static ObjectMapper mapper = new ObjectMapper();

    public static String getJsonString(String command, String...params) throws JsonProcessingException {
        JsonObject jsonObject = new JsonObject(command, params);
        return mapper.writeValueAsString(jsonObject);
    }

    public static <T> String getJsonString(T object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    public static <T> T getObjectFromJson(String json, Class<T> tClass) throws IOException {
        return mapper.readValue(json, tClass);
    }
}
