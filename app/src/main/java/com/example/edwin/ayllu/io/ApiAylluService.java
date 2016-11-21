package com.example.edwin.ayllu.io;

import com.example.edwin.ayllu.domain.Task;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface ApiAylluService {
    @POST("puntos/registrar/")
    Call<Task> registrarPunto(@Body Task datos);
}
