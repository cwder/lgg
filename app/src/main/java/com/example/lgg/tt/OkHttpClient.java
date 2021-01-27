package com.example.lgg.tt;

import okhttp3.Dispatcher;

public class OkHttpClient {

    public OkHttpClient(){

    }

    OkHttpClient(Builder builder){

    }

    public static final class Builder {
        Dispatcher dispatcher;
    }
}
