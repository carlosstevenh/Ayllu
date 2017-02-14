package com.example.edwin.ayllu.io;

import com.example.edwin.ayllu.domain.Task;
import com.example.edwin.ayllu.io.model.ReporteResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface AylluApiService {
    @POST("monitoreos/registrar/")
    Call<Task> registrarPunto(@Body Task datos);

    @POST("monitoreos/consultar/")
    Call<ReporteResponse> getReporte();
}
