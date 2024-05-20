package com.artakbaghdasaryan.fungus.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSingleton {
    private static final Gson gson = new GsonBuilder().create();

    public static Gson getInstance() {
        return gson;
    }
}