package com.qhapaq.nan.ayllu.io;

import com.qhapaq.nan.ayllu.domain.Mensaje;
import com.qhapaq.nan.ayllu.domain.task.Task;
import com.qhapaq.nan.ayllu.domain.usuario.Usuario;
import com.qhapaq.nan.ayllu.io.model.CategoriaResponse;
import com.qhapaq.nan.ayllu.io.model.ReporteResponse;
import com.qhapaq.nan.ayllu.io.model.UsuarioResponse;
import com.qhapaq.nan.ayllu.io.model.ZonaResponse;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


public interface AylluApiService {

    /**
     * =============================================================================================
     * METODO: Consultar Usuarios
     **/
    @GET("usuarios/consultar/{pais}/{estado}")
    Call<UsuarioResponse> getUsuarios(@Path("pais") String pais,
                                      @Path("estado") String estado);

    /**
     * =============================================================================================
     * METODO: Loguear un Usuario
     **/
    @POST("usuarios/login/")
    Call<UsuarioResponse> loginUsuario(@Body Usuario datos);

    /**
     * =============================================================================================
     * METODO: Registrar un Usuario
     **/
    @POST("usuarios/registrar/")
    Call<Mensaje> registrarUsuario(@Body Usuario datos);

    /**
     * =============================================================================================
     * METODO: Actualizar un Usuario
     **/
    @POST("usuarios/actualizar/")
    Call<Mensaje> actualizarUsuario(@Body Usuario datos);

    /**
     * =============================================================================================
     * METODO: Cambiar el estado de un Usuario
     **/
    @POST("usuarios/cambiar/")
    Call<Mensaje> estadoUsuario(@Body Usuario datos);

    /**
     * =============================================================================================
     * METODO: Registrar un nuevo punto de afectación con monitoreo
     **/
    @POST("monitoreos/registrar/")
    Call<Mensaje> registrarPunto(@Body Task datos);
    /**
     * =============================================================================================
     * METODO: Registra un monitoreo sobre un punto de afectación existente
     **/
    @POST("monitoreos/monitorear/")
    Call<Mensaje> monitorearPunto(@Body Task datos);
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


    @Multipart
    @POST("imagen/")
    Call<String> uploadAvatar (@Part MultipartBody.Part filePart);
}
