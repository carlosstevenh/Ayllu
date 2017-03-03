package com.example.edwin.ayllu.io;

import com.example.edwin.ayllu.domain.Task;
import com.example.edwin.ayllu.io.model.CategoriaResponse;
import com.example.edwin.ayllu.io.model.ReporteResponse;
import com.example.edwin.ayllu.io.model.ZonaResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface AylluApiService {
    //----------------------------------------------------------------------------------------------
    //METODO: REGISTRAR UN MONITOREO
    @POST("monitoreos/registrar/")
    Call<Task> registrarPunto(@Body Task datos);
    //----------------------------------------------------------------------------------------------
    //METODO: CONSULTAR MONITOREOS
    @GET("monitoreos/consultar/{tramo}/{subtramo}/{seccion}/{area}")
    Call<ReporteResponse> getReporte(@Path("tramo") int tramo,
                                     @Path("subtramo") int subtramo,
                                     @Path("seccion") int seccion,
                                     @Path("area") int area );
    //----------------------------------------------------------------------------------------------
    //METODO: CONSULTAR ZONAS
    @GET("zonas/consultar/")
    Call<ZonaResponse> getZona();
    //----------------------------------------------------------------------------------------------
    //METODO: CONSULTAR CATEGORIAS
    @GET("categorias/consultar/")
    Call<CategoriaResponse> getCategoria();
}
