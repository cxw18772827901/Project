package com.dave.project.request.volley;

import com.google.gson.annotations.Expose;

/**
 * 自定义类型,手动添加header
 * Created by Dave on 2017/2/9.
 */

public class JsonKey {

    public JsonKey(String jsonHeader) {
        this.jsonHeader = jsonHeader;
    }

    @Expose
    public String jsonHeader;
}
