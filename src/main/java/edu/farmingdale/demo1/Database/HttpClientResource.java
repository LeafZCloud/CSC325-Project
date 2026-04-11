package edu.farmingdale.demo1.Database;

import okhttp3.OkHttpClient;

public class HttpClientResource
{
    //Singleton design pattern
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

    public static OkHttpClient get()
    {
        return OK_HTTP_CLIENT;
    }

}
