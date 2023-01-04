package pers.warren.ioc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializeUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static String toJson(Object object){
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getOriginalMessage());
        }
    }

    public static  <T> T fromJson(String json,Class<T> clz){
        try {
            return objectMapper.readValue(json,clz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getOriginalMessage());
        }
    }

    public static  <T> T fromJson(String json , TypeReference<T> typeReference){
        try {
            return objectMapper.readValue(json,typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
