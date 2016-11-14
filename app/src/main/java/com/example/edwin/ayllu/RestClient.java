package com.example.edwin.ayllu;

import com.example.edwin.ayllu.Domain.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by steven on 5/11/16.
 */

public interface RestClient {

    String BASE_URL="http://192.168.56.1/webservice/";
    //String BASE_URL="http://192.168.1.5/webservice/";

    @GET("login/{ide_usu}/{pw_usu}")
    Call<ArrayList<Usuario>> getUsuario(@Path("ide_usu") String identificacion_usu,
                                        @Path("pw_usu") String contrasena_usu);

    @GET("add/{ide}/{nom}/{ape}/{tipo}/{con}/{pais}")
    Call<ArrayList<String>> addUsuario(@Path("ide") String ide,
                                       @Path("nom") String nom,
                                       @Path("ape") String ape,
                                       @Path("tipo") String tipo,
                                       @Path("con") String con,
                                       @Path("pais") String pais);

    @GET("edit/{ide}/{nom}/{ape}/{con}/{codigo}")
    Call<ArrayList<String>> editUsuario(@Path("ide") String ide,
                                       @Path("nom") String nom,
                                       @Path("ape") String ape,
                                       @Path("con") String con,
                                       @Path("codigo") String pais);
    @GET("getMonitores/{pais}")
    Call<ArrayList<Usuario>> getUsuarios(@Path("pais") String pais);

    public static final Retrofit retrofit = new Retrofit.Builder().baseUrl(RestClient.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build();
}
