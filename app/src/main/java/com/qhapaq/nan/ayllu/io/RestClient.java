package com.qhapaq.nan.ayllu.io;

import com.qhapaq.nan.ayllu.domain.AnalisisPorcentajeFrecuencia;
import com.qhapaq.nan.ayllu.domain.ConteoFactoresTramo;
import com.qhapaq.nan.ayllu.domain.ConteoVariableFactorTramo;
import com.qhapaq.nan.ayllu.domain.monitoreo.Monitoreo;
import com.qhapaq.nan.ayllu.domain.MonitoreoGeneral;
import com.qhapaq.nan.ayllu.domain.Promedio;
import com.qhapaq.nan.ayllu.domain.Prueba;
import com.qhapaq.nan.ayllu.domain.PuntoCritico;
import com.qhapaq.nan.ayllu.domain.RespuestaInstitucional;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RestClient {

    //peticion para obtener los promedios dependiendo el tramo y variable
    @GET("promedioTramoVariable/{tramo}/{variable}/{monitor}")
    Call<ArrayList<Promedio>> promedios(@Path("tramo") String tramo,
                                        @Path("variable") String variable,
                                        @Path("monitor") String monitor);

    //Peticion que obtiene el conteo de las variables dependiendo del factor y tramo
    @GET("countVariableTramo/{tramo}/{factor}")
    Call<ArrayList<ConteoVariableFactorTramo>> conteoVariableFactortramo(@Path("tramo") String tramo,
                                                                         @Path("factor") String factor);

    //peticion que trae el conteo de factores por tramo
    @GET("countFactorTramo/{tramo}")
    Call<ArrayList<ConteoFactoresTramo>> conteoFactorTramo(@Path("tramo") String tramo);

    //peticion que trae el nombre de la prueba de un monitoreo
    @GET("getRespuestas/{paf}")
    Call<ArrayList<RespuestaInstitucional>> getRespuestaInstitucional(@Path("paf") String paf);

    //peticion que trae el nombre de la prueba de un monitoreo
    @GET("nombrePrueba/{paf}/{fec}")
    Call<ArrayList<Prueba>> getPrueba(@Path("paf") String paf,
                                      @Path("fec") String fec);

    //peticion que trae todos los monitores de un punto de afectacion
    @GET("puntosAfectacion/{paf}")
    Call<ArrayList<AnalisisPorcentajeFrecuencia>> proFre (@Path("paf") String paf);


    //peticion que consulta los ultimos monitores de cada punto de afectacion de acuerdo al filtro que el usuario realice
    @GET("filtrarMonitoreosConFecha/{p1}/{p2}/{p3}/{p4}/{p5}/{p6}/{fi}/{ff}/{fac}/{var}")
    Call<ArrayList<PuntoCritico>> getFiltro(@Path("p1") String p1,
                                            @Path("p2") String p2,
                                            @Path("p3") String p3,
                                            @Path("p4") String p4,
                                            @Path("p5") String p5,
                                            @Path("p6") String p6,
                                            @Path("fi") String fi,
                                            @Path("ff") String ff,
                                            @Path("fac") String fac,
                                            @Path("var") String var);

    //peticion que trae la informacion completa de un monitoreo
    @GET ("descripcionPC/{paf}/{fecha}")
    Call<ArrayList<MonitoreoGeneral>> informacion (@Path("paf") String paf,
                                                   @Path("fecha") String fec);

    //peticion que trae todos los monitoreos de un area especifica
    @GET("monitoreos/{tramo}/{subtramo}/{seccion}/{area}")
    Call <ArrayList<Monitoreo>> monitoreos ( @Path("tramo") String tramo,
                                             @Path("subtramo") String subtramo,
                                             @Path("seccion") String seccion,
                                             @Path("area") String area);

    //peticion que registra una respuesta institucional
    @GET ("addRespuesta/{pa}/{fm}/{eva}/{per}/{tie}/{pre}/{rec}/{con}/{mon}")
    Call<ArrayList<String>> addRespuesta (@Path("pa") String pa,
                                          @Path("fm") String fm,
                                          @Path("eva") String eva,
                                          @Path("per") String per,
                                          @Path("tie") String tie,
                                          @Path("pre") String pre,
                                          @Path("rec") String rec,
                                          @Path("con") String con,
                                          @Path("mon") String mon);

    Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiConstants.URL_WEBSERVICE)
            .addConverterFactory(GsonConverterFactory.create()).build();
}
