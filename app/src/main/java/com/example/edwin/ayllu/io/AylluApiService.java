package com.example.edwin.ayllu.io;

import com.example.edwin.ayllu.domain.Task;
import com.example.edwin.ayllu.io.model.CategoriaResponse;
import com.example.edwin.ayllu.io.model.ReporteResponse;
import com.example.edwin.ayllu.io.model.ZonaResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


public interface AylluApiService {
    /**
     * =============================================================================================
     * METODO: Registrar un nuevo punto de afectación con monitoreo
     **/
    @POST("monitoreos/registrar/")
    Call<Task> registrarPunto(@Body Task datos);
    /**
     * =============================================================================================
     * METODO: Registra un monitoreo sobre un punto de afectación existente
     **/
    @POST("monitoreos/monitorear/")
    Call<Task> monitorearPunto(@Body Task datos);
    /**
     * =============================================================================================
     * METODO: Consultar Monitoreos
     **/
    @GET("monitoreos/consultar/{tramo}/{subtramo}/{seccion}/{area}")
    Call<ReporteResponse> getReporte(@Path("tramo") int tramo,
                                     @Path("subtramo") int subtramo,
                                     @Path("seccion") int seccion,
                                     @Path("area") int area);

    /**
     * =============================================================================================
     * METODO: Consultar Zonas
     **/
    @GET("zonas/consultar/")
    Call<ZonaResponse> getZona();

    /**
     * =============================================================================================
     * METODO: Consultar Categorias
     **/
    @GET("categorias/consultar/")
    Call<CategoriaResponse> getCategoria();

    /**
     * =============================================================================================
     * METODO: Descargar Imagenes
     **/
    @Streaming
    @GET
    Call<ResponseBody> downloadImageByUrl(@Url String fileUrl);
}
