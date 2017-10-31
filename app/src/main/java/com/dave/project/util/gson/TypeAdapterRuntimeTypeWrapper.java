package com.dave.project.util.gson;


import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.Adapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * 此类本身是非public,使用需要拷贝到本地
 * Created by Dave on 2016/12/28.
 */
class TypeAdapterRuntimeTypeWrapper<T> extends TypeAdapter<T> {
    private final Gson context;
    private final TypeAdapter<T> delegate;
    private final Type type;

    TypeAdapterRuntimeTypeWrapper(Gson context, TypeAdapter<T> delegate, Type type) {
        this.context = context;
        this.delegate = delegate;
        this.type = type;
    }

    public T read(JsonReader in) throws IOException {
        return this.delegate.read(in);
    }

    public void write(JsonWriter out, T value) throws IOException {
        TypeAdapter chosen = this.delegate;
        Type runtimeType = this.getRuntimeTypeIfMoreSpecific(this.type, value);
        if (runtimeType != this.type) {
            TypeAdapter runtimeTypeAdapter = this.context.getAdapter(TypeToken.get(runtimeType));
            if (!(runtimeTypeAdapter instanceof Adapter)) {
                chosen = runtimeTypeAdapter;
            } else if (!(this.delegate instanceof Adapter)) {
                chosen = this.delegate;
            } else {
                chosen = runtimeTypeAdapter;
            }
        }
        chosen.write(out, value);
    }

    private Type getRuntimeTypeIfMoreSpecific(Type type, Object value) {
        if (value != null && (type == Object.class || type instanceof TypeVariable || type instanceof Class)) {
            type = value.getClass();
        }
        return (Type) type;
    }
}
