package com.qhapaq.nan.ayllu.io;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClientAdapter {

    private static AylluApiService API_SERVICE;

    /**
     * Nuevo metodo para solicitar un servicio al API-REST con una URL dinamica, determinada por el
     * pais deL usuario
     * @param url : Url dinamica - depende del pais del usuario
     * @return
     */
    public static Retrofit getRetrofit(String url){


        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }
}
