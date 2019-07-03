package com.sso.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
}
