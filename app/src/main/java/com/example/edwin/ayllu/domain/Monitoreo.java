package com.example.edwin.ayllu.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steven on 27/11/16.
 */

public class Monitoreo {
    @SerializedName("fecha_mon")
    private String date ;
    @SerializedName("nombre_var")
    private String variable;
    @SerializedName("codigo_paf")
    private String codigo;
    @SerializedName("latitud_coo")
    private String latitud;
    @SerializedName("longitud_coo")
    private String longitud;
    @SerializedName("nombre_pru1")
    private String prueba;
    @SerializedName("propiedad_nominada")
    private  String propiedad;

    public Monitoreo(String date, String variable, String codigo, String latitud, String longitud, String prueba, String propiedad) {
        this.date = date;
        this.variable = variable;
        this.codigo = codigo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.prueba = prueba;
        this.propiedad = propiedad;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    public String getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(String propiedad) {
        this.propiedad = propiedad;
    }
}
