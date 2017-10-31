package com.dave.project.request.volley;

import android.text.TextUtils;

import com.dave.project.base.Constants;
import com.dave.project.util.SPUtil;
import com.dave.project.util.debug.BaseLog;
import com.google.gson.annotations.Expose;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 这个类封装了请求url+参数
 * Created by skindhu on 2015/05/13
 */
public abstract class BaseInput<T> {
    private static final String TAG = BaseInput.class.getName();

    @Expose(serialize = false, deserialize = false)
    public String url;

    @Expose(serialize = false, deserialize = false)
    public final int method;

    @Expose(serialize = false, deserialize = false)
    public Map<String, String> cookies;

    @Expose(serialize = false, deserialize = false)
    public final Class<T> clazz;

    protected BaseInput(String url, int method, Class<T> clazz) {
        this.url = Constants.URL_BASE_HEADER + url;
        BaseLog.d("main", "BaseInput.url=" + this.url);
        this.method = method;
        this.clazz = clazz;
        this.cookies = getCookies();
    }

    protected BaseInput(String url, int method, Class<T> clazz, String urlBase) {
        this.url = urlBase + url;
        BaseLog.d("main", "BaseInput.url=" + this.url);
        this.method = method;
        this.clazz = clazz;
        this.cookies = getCookies();
    }

    /**
     * 在这里会用反射的方式，通过在子类字段的注解@name()来判断该字段是否是参数字段
     * 参数字段会根据不同的类型进行存入Map集合的Value，key是注解@name()提供的字符串
     *
     * @return Map集合
     */
    public Map<String, Object> getParams() {
        // 获得当前对象的Class
        Class<? extends BaseInput> clazz = this.getClass();
        // 获得当前对象类的声明字段 public private protected
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Object> params = new HashMap<>();

        // 存入手机的IMEI号
//        params.put("u", DeviceUtil.getIMEI());
        for (Field field : fields) {
            // 从字段中获得注解注解符号为@name()
            InputKey prop = field.getAnnotation(InputKey.class);
            if (null == prop) {
                continue;
            }
            try {
                // 获得字段的类型并可以访问private权限的字段
                Class<?> type = field.getType();
                field.setAccessible(true);
                // 根据对应的字段类型存入信息
                if (String.class == type || URL.class == type || URI.class == type) {
                    // 存入这三个类的toString返回的信息
                    params.put(prop.name(), field.get(this).toString());
                } else if (double.class == type || Double.class == type) {
                    params.put(prop.name(), field.getDouble(this));
                } else if (float.class == type || Float.class == type) {
                    params.put(prop.name(), field.getFloat(this));
                } else if (int.class == type || Integer.class == type) {
                    params.put(prop.name(), field.getInt(this));
                } else if (long.class == type || Long.class == type) {
                    params.put(prop.name(), field.getLong(this));
                } else if (short.class == type || Short.class == type) {
                    params.put(prop.name(), field.getShort(this));
                } else if (byte.class == type || Byte.class == type) {
                    params.put(prop.name(), field.getByte(this));
                } else if (boolean.class == type || Boolean.class == type) {
                    params.put(prop.name(), field.getBoolean(this) ? 1 : 0);
                } else if (Date.class == type) {
                    params.put(prop.name(), ((Date) field.get(this)).getTime());
                } else if (byte[].class == type) {
                    params.put(prop.name(), field.get(this));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        BaseLog.d("main", "BaseInput.params=" + params.toString());
        return params;
    }

    /**
     * 用户相关的cookie
     *
     * @return 将cookie以键值对的形式添加到Map中
     */
    private Map<String, String> getCookies() {
        Map<String, String> cookies = new HashMap<>();
//        cookies.put(Constants.USER_ID, SPUtil.getString(Constants.USER_ID));
//        cookies.put(Constants.USER_ACCOUNT, SPUtil.getString(Constants.USER_ACCOUNT));
        String sessioned = SPUtil.getString(Constants.SESSION_ID);
        if (!TextUtils.isEmpty(sessioned)) {
            cookies.put("JSESSIONID", sessioned);
            BaseLog.d("log_s", "请求url=" + url + ",附带的JSESSIONID=" + sessioned);
        } else {
            BaseLog.d("log_s", "请求url=" + url + ",附带的JSESSIONID=空");
        }
//        cookies.put(Constants.CLIENT_TYPE, "android");
//        cookies.put(Constants.APP_VERSON, AppUtil.getVersionCode(BaseApplication.getContext()) + "");
        return cookies;
    }

}
