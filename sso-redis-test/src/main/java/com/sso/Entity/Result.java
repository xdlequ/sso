package com.sso.Entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 返回值同一模板
 * Created by ql on 2019/7/3.
 */
@Component
@Setter
@Getter
@NoArgsConstructor
public class Result {

    //响应状态码
    private Integer status;

    //具体信息
    private String msg;

    //响应中的数据
    private Object data;

    //定义json对象
    private static final ObjectMapper MAPPER=new ObjectMapper();


    public Result(Integer status, String msg, Object data) {
        this.status=status;
        this.msg=msg;
        this.data=data;
    }

    public Result(Object data){
        this.status=200;
        this.msg="OK";
        this.data=data;
    }

    public static Result ok(Object data){
        return new Result(data);
    }

    public static Result ok(){
        return new Result(null);
    }

    public static Result build(Integer status,String msg,Object data){
        return new Result(status,msg,data);
    }
    public static Result build(Integer status,String msg){
        return new Result(status,msg,null);
    }

    /**
     * 将json结果转化为Result对象
     * @param jsonData json数据
     * @param clazz Result中的object类型
     * @return
     */
    public static Result formatToEntity(String jsonData,Class<?>clazz){
        try {
            if (clazz==null){
                return MAPPER.readValue(jsonData,Result.class);
            }
            JsonNode jsonNode=MAPPER.readTree(jsonData);
            JsonNode data=jsonNode.get("data");
            Object obj =null;
            if (clazz!=null){
                if (data.isObject()){
                    obj=MAPPER.readValue(data.traverse(),clazz);
                }else if (data.isTextual()){
                    obj=MAPPER.readValue(data.asText(),clazz);
                }
            }
            return build(jsonNode.get("status").intValue(),jsonNode.get("msg").asText(),obj);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 没有object对象的转化
     * @param json
     * @return
     */
    public static Result format(String json){
        try {
            return MAPPER.readValue(json,Result.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json集合转化为List
     * @param jsonData
     * @param clazz
     * @return
     */
    public static Result formatToList(String jsonData,Class<?>clazz) {
        try {
            JsonNode jsonNode=MAPPER.readTree(jsonData);
            JsonNode data=jsonNode.get("data");
            Object obj=null;
            if (data.isArray()&&data.size()>0){
                obj=MAPPER.readValue(data.traverse(),
                        MAPPER.getTypeFactory().constructCollectionType(List.class,clazz));
            }
            return build(jsonNode.get("status").intValue(),jsonNode.get("msg").asText(),obj);
        } catch (Exception e) {
            return null;
        }
    }
}
