package com.sso.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sso.Entity.User;

import java.io.IOException;

/**
 * Created by ql on 2019/7/3.
 */
public class JsonUtil {

    private static final ObjectMapper MAPPER=new ObjectMapper();

    public static String objectToJson(Object obj){
        try {
            String str=MAPPER.writeValueAsString(obj);
            return str;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T>T JsonToEntity(String json, Class<User> beanType) {
        try {
            T t=MAPPER.readValue(json, (Class<T>) beanType);
            return t;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
