package com.wrs.gykjewm.baselibrary.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * description:
 * <p>
 * author: josh.lu
 * created: 17/7/18 下午3:38
 * email:  1113799552@qq.com
 * version: v1.0
 */
public class JsonUtils {
    private static Gson mGson;
    static{
        if (mGson == null) {
            mGson = new Gson();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> str2ListMap(String jsonArrStr) {
        System.out.println(jsonArrStr);
        List<Map<String, Object>> jsonObjList = new ArrayList<Map<String, Object>>();
        List<?> jsonList = jsonToList(jsonArrStr);
        for (Object object : jsonList) {
            String jsonStr = mGson.toJson(object);
            Map<?, ?> json = jsonToMap(jsonStr);
            System.out.println(json.toString());
            jsonObjList.add((Map<String, Object>) json);
        }
        return jsonObjList;
    }

    /**
     * 将传入的json字符串解析为List集合
     * @param jsonStr
     * @return
     */
    public static List<?> jsonToList(String jsonStr) {
        List<?> ObjectList = null;
        java.lang.reflect.Type type = new TypeToken<List<?>>() {
        }.getType();
        ObjectList = mGson.fromJson(jsonStr, type);
        return ObjectList;
    }

    /**
     * 将传入的json字符串解析为Map集合
     * @param jsonStr
     * @return
     */
    public static Map<?, ?> jsonToMap(String jsonStr) {
        Map<?, ?> ObjectMap = null;
        java.lang.reflect.Type type = new TypeToken<Map<?, ?>>() {
        }.getType();
        ObjectMap = mGson.fromJson(jsonStr, type);
        return ObjectMap;
    }

    /**
     * map转json字符串
     * @param m
     * @return
     */
    public static String map2json(Map<?,?> m){
        mGson.toJson(m).toString();
        return mGson.toJson(m).toString();
    }

    /**
     * JsonObject 转换为对象
     */
    public static Object jsonToObject(final Class<?> cls, final JsonDeserializer deserializer, final JsonObject json) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(cls, deserializer);
        Gson gson = builder.create();
        gson.fromJson(json, cls);
        return gson.fromJson(json, cls);
    }

    /**
     * JsonObject 转换为对象数组
     */
    public static <T> List<T> jsonToList(final Class<T> cls, final JsonDeserializer deserializer, final JsonArray json){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(cls, deserializer);
        Gson gson = builder.create();
        List<T> items = new ArrayList<>();
        for(JsonElement element: json){
            items.add(gson.fromJson(element, cls));
        }
        return items;
    }

    /**
     * 将JsonArray转换成List
     */
    public static <T> List<T> jsonArray2List(JsonArray jsonArray, T t){
        List<T> list = new ArrayList<>();
        for (JsonElement obj: jsonArray) {
            T tBean = (T) mGson.fromJson(obj, t.getClass());
            list.add(tBean);
        }
        //List<T> list = mGson.fromJson(jsonArray, new TypeToken<List<T>>() {}.getType());
        return list;
    }

    /**
     * string to json array
     * @param datas
     * @param <T>
     * @return
     */
    public static <T> JsonArray listToJson(List<T> datas){
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        String str = gson.toJson(datas);
        JsonArray aJson = parser.parse(str).getAsJsonArray();
        return aJson;
    }
}