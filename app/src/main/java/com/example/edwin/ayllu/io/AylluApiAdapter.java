package com.example.edwin.ayllu.io;

import android.util.Log;

import com.example.edwin.ayllu.domain.Zona;
import com.example.edwin.ayllu.io.deserializer.CategoriaDeserializer;
import com.example.edwin.ayllu.io.deserializer.ZonaDeserializer;
import com.example.edwin.ayllu.io.deserializer.ReporteDeserializer;
import com.example.edwin.ayllu.io.model.CategoriaResponse;
import com.example.edwin.ayllu.io.model.ReporteResponse;
import com.example.edwin.ayllu.io.model.ZonaResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AylluApiAdapter {
    private static AylluApiService API_SERVICE;
    //==============================================================================================
    public static AylluApiService getApiService(String op){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.interceptors().add(logging); // <-- this is the important line!

        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(ApiConstants.URL_API_AYLLU)
                .addConverterFactory(GsonConverterFactory.create(buildAylluApiGsonConverter(op)))
                .client(httpClient.build())
                .build();
        API_SERVICE = adapter.create(AylluApiService.class);

        return API_SERVICE;
    }
    //==============================================================================================
    private static Gson buildAylluApiGsonConverter(String op) {
        Gson gsonConf;
        switch (op) {
            case "REPORTE":
                Log.e("INFO","DESERIALIZER REPORTE");
                gsonConf = new GsonBuilder()
                        .registerTypeAdapter(ReporteResponse.class, new ReporteDeserializer())
                        .create();

                return gsonConf;
            case "ZONAS":
                gsonConf = new GsonBuilder()
                        .registerTypeAdapter(ZonaResponse.class, new ZonaDeserializer())
                        .create();

                return gsonConf;
            case "CATEGORIAS":
                gsonConf = new GsonBuilder()
                        .registerTypeAdapter(CategoriaResponse.class, new CategoriaDeserializer())
                        .create();

                return gsonConf;
            default:
                return null;
        }
    }
}
