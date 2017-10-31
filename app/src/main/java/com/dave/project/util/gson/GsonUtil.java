package com.dave.project.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * google Gson的通用工具类,强化null字段解析
 * Created by skindhu on 2015/05/13
 * modify by Dave on2016/12/28
 */
public class GsonUtil {

    private static Gson gson;

    //注意:使用此方法时,必须要加上@Expose标注,不然会出现异常
    public static Gson getGson() {
        if (null != gson) {
            return gson;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Boolean.class, booleanAsIntAdapter);//注册解析Boolean和boolean类型的适配器,写的时候true转化为1,false转化为0,读的时候判断是否等于0
        gsonBuilder.registerTypeAdapter(boolean.class, booleanAsIntAdapter);
        gsonBuilder.registerTypeAdapter(String.class, nullAsStringAdapter);//注册解析String类型的适配器,读取到null自动转化为""
        //接下来处理数组为null的情况,返回空数据
        try {
            Class buildClass = (Class) gsonBuilder.getClass();
            Field f = buildClass.getDeclaredField("instanceCreators");//通过反射获取instanceCreators属性
            f.setAccessible(true);
            ConstructorConstructor constructorConstructor = new ConstructorConstructor((Map<Type, InstanceCreator<?>>) f.get(gsonBuilder));
            CollectionTypeAdapterFactory collectionTypeAdapterFactory = new CollectionTypeAdapterFactory(constructorConstructor);
            gsonBuilder.registerTypeAdapterFactory(collectionTypeAdapterFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();//忽略没有Expose的字段
        gson = gsonBuilder.create();
        return gson;
    }

    /**
     * 自定义TypeAdapter ,通过与int之间转换进行操作
     */
    private static final TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<Boolean>() {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            if (null == value) {
                out.nullValue();
            } else {
                out.value(value ? 1 : 0);
            }
        }

        @Override
        public Boolean read(JsonReader jsonReader) throws IOException {
            JsonToken peek = jsonReader.peek();
            switch (peek) {
                case BOOLEAN:
                    return jsonReader.nextBoolean();
                case NULL:
                    jsonReader.nextNull();
                    return null;
                case NUMBER:
                    return jsonReader.nextInt() != 0;
                case STRING:
                    return Boolean.parseBoolean(jsonReader.nextString());
                default:
                    throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
            }
        }
    };

    /**
     * 自定义TypeAdapter ,null对象将被解析成空字符串
     */
    public static final TypeAdapter<String> nullAsStringAdapter = new TypeAdapter<String>() {
        public void write(JsonWriter writer, String value) {
            try {
                if (value == null) {
                    writer.nullValue();
                } else {
                    writer.value(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String read(JsonReader reader) {
            try {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return "";//原先是返回Null，这里改为返回空字符串
                } else {
                    return reader.nextString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    };
}
