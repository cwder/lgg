package com.example.lgg.tt;


import java.util.concurrent.ExecutorService;

import androidx.annotation.Nullable;

// 调度器
public final class Dispatcher {
    private int maxRequest = 64;
    //同时请求的最大数目不能超过 maxRequests ，同时对于每一个主机host（服务器），同时请求数目不能超过maxRequestsPerHost
    private int maxRequestPerHost = 5;
    //当所有请求都执行完毕的时候执行这个runnable
    private @Nullable Runnable idleCallback;

    //线程池，懒加载
    private @Nullable
    ExecutorService executorService;

}
