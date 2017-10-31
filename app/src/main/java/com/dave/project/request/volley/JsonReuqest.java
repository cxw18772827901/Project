package com.dave.project.request.volley;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.dave.project.base.Constants;
import com.dave.project.util.SPUtil;
import com.dave.project.util.debug.BaseLog;
import com.dave.project.util.debug.MyDebugUtil;
import com.dave.project.util.gson.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * Volley json请求回调的解析类,传参类型:jsonValue
 * Created by skindhu on 15/5/12.
 */
public class JsonReuqest<T> extends Request<T> {
    private static final String TAG = JsonReuqest.class.getName();
    private Map<String, String> params = new HashMap<>();
    public Class<T> mClazz;
    private Response.Listener<T> mListener;
    private BaseInput<T> mInput;
    private boolean mShouldCache = false; // 是否缓存
    private long mCacheTime = 1000 * 60 * 60; // 缓存超时时间默认1小时，单位毫秒
    private boolean needHead;//是否需要手动添加key
    private String mJsonString;

    public JsonReuqest(boolean needHead, BaseInput<T> input, String jsonString, Response.Listener<T> successListener, Response.ErrorListener listener) {
        super(input.method, input.url, listener);
        this.needHead = needHead;
        this.mClazz = input.clazz;
        this.mListener = successListener;
        this.mInput = input;
        this.mJsonString = jsonString;
        setShouldCache(mShouldCache);
    }

    /**
     * 设置cookie到header中
     *
     * @return
     * @throws AuthFailureError
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("X-Wap-Proxy-Cookie", "none");
//      headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        headerMap.put("Content-Type", "application/json;charset=utf-8");
        headerMap.put("Cookie", getCookie());
        return headerMap;
    }

    /**
     * 获取请求参数:
     */
//    @Override
//    protected Map<String, String> getParams() {
////		params.put("i", mInput.getEncodedParams());源代码
//        Map<String, Object> args = mInput.getParams();
//        Set<String> keySet = args.keySet();
//        for (String key : keySet) {
//            if ("u".equals(key)) {
//                continue;
//            }
//            this.params.put(key, String.valueOf(args.get(key)));
//        }
//        return this.params;
//    }

    /**
     * 注意
     * gson解析回调:所有请求的数据都在这里进行解析,可以直接打断点查看所有的信息
     * 另外,服务端cookie也是从这里获取
     *
     * @param response
     * @return
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        Map<String, String> responseHeaders = response.headers;
        String rawCookies = responseHeaders.get("Set-Cookie");//-----------------------------从服务端获取的cookie
        if (!TextUtils.isEmpty(rawCookies)) {
            MyDebugUtil.logTest("cookie", rawCookies);
            if (rawCookies.contains("logout_status")) {
//                MyDebugUtil.logTest("logout_status","链接为="+mInput.url);
                BaseLog.d("log_s", "链接为=" + mInput.url + "sessid为=" + SPUtil.getString(Constants.SESSION_ID));
                jumpLogin();
                return Response.error(new NetworkError(response));
            }
        } else {
            MyDebugUtil.logTest("cookie", "服务端获取cookie为空");
        }
        try {
            String data = new String(response.data, HttpHeaderParser.parseCharset(responseHeaders));
            MyDebugUtil.logTest("base", "服务返回的数据是" + data);
            if (!TextUtils.isEmpty(data)) {
                T responseObject;
                Gson gson = GsonUtil.getGson();
                if (needHead) {
                    JsonKey jsonKey = new JsonKey(data);//赋值
                    String json = gson.toJson(jsonKey);//使用toString添加字段
                    responseObject = gson.fromJson(json, mClazz);
                } else {
                    responseObject = gson.fromJson(data, mClazz);
                }
                return Response.success(responseObject, cache(response, mCacheTime));
            } else {
                MyDebugUtil.logTest("base", "服务返回的数据data为空");
                return Response.error(new ParseError(response));
            }
        } catch (JsonParseException e) {
            Log.e("base", "json解析异常");
            e.printStackTrace();
            return Response.error(new ParseError(response));
        } catch (UnsupportedEncodingException e) {
            Log.e("base", "不支持的编码异常");
            e.printStackTrace();
            return Response.error(new ParseError(response));
        } catch (Exception e) {
            Log.e("base", "其他异常");
            e.printStackTrace();
            return Response.error(new ServerError(response));
        }
    }

    private void jumpLogin() {
        NetClient.cancleAll();
//        if (0 == BaseApplication.loginFlag307) {
//            BaseApplication.loginFlag307 = 1;
//            BaseApplication.getContext().sendBroadcast(new Intent(HeartService.FINISH_SERVICE));
//            //停止心跳
//            if (UserUtil.isAnchor()) {
//                ChangeAnchorStateUtil.changeAnchorState(SPUtil.getString(Constants.USER_ACCOUNT), ChangeAnchorStateUtil.OFF_LINE);
//            }
//            RongIMClient.getInstance().logout();//退出融云
//            LoginUtil.clearCacheInfo();//清除缓存当前用户的信息
//            Intent intent = new Intent(BaseApplication.getContext(), LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("logout_status", "logout_status");
//            BaseApplication.getContext().startActivity(intent);
//        }
    }

    /**
     * 回调
     *
     * @param response
     */
    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    /**
     * 使用url+参数+cookie的拼接的字符串作为缓存时使用到的的key
     *
     * @return
     */
    @Override
    public String getCacheKey() {
        StringBuilder sb = new StringBuilder();
        //添加url
        String url = getUrl();
        sb.append(url);
        //添加请求参数
//        Map<String, String> params = getParams();
//        StringBuilder p = new StringBuilder();
//        for (String key : params.keySet()) {
//            p.append("&").append(key).append("=").append(params.get(key));
//        }
//        sb.append(p);
        //添加cookie
        Map<String, String> cookies = mInput.cookies;
        StringBuilder c = new StringBuilder();
        if (cookies != null) {
            for (String key : cookies.keySet()) {
                c.append("&").append(key).append("=").append(cookies.get(key));
            }
        }
        sb.append(c);
        return sb.toString();
    }

    /**
     * 获取Cookie形式的通用参数
     *
     * @return 返回BaseInput中map拼接的参数
     */
    private String getCookie() {
        Map<String, String> commonParams = buildCommonParams();
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : commonParams.keySet()) {
            stringBuilder.append(key).append("=").append(commonParams.get(key));
            stringBuilder.append(";");
        }

        Map<String, String> cookies = mInput.cookies;
        if (cookies != null) {
            for (String key : cookies.keySet()) {
                stringBuilder.append(key).append("=").append(cookies.get(key));
                stringBuilder.append(";");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 设置缓存
     *
     * @param response
     * @param maxAge
     * @return 返回缓存实体
     */
    public static Cache.Entry cache(NetworkResponse response, long maxAge) {
        long now = System.currentTimeMillis();
//        if (maxAge == 0) maxAge = 60;
        Map<String, String> headers = response.headers;

        long serverDate = 0;
        long softExpire = 0;
        String serverEtag = null;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }
        softExpire = now + maxAge;
        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = entry.softTtl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;
        return entry;
    }

    //设置缓存时间,默认一个小时
    public void setCacheTime(long cacheTime) {
        this.mCacheTime = cacheTime;
    }

    /**
     * 构造通用参数列表
     */
    private static Map<String, String> buildCommonParams() {
        Map<String, String> params = new HashMap<String, String>();
        String deviceId;
        try {
            deviceId = URLEncoder.encode(DeviceUtil.getIMEI(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        params.put("TERMINAL", "android_" + deviceId);
        params.put("APP_TIME", System.currentTimeMillis() + "");
        return params;
    }

    /******************************************json方式请求*************************************************/
    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return mJsonString.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getBodyContentType() {
        return "application/json;charset=utf-8";
    }
}
