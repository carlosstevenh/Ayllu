package com.example.edwin.ayllu.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steven on 16/02/17.
 */

public class PuntoCritico {
    @SerializedName("codigo_paf")
    private String codigo_paf;
    @SerializedName("fecha_mon")
    private String fecha;
    @SerializedName("nombre_pais")
    private String pais;
    @SerializedName("propiedad_nominada")
    private String area;
    @SerializedName("nombre_fac")
    private String factor;
    @SerializedName("nombre_var")
    private String variable;
    @SerializedName("porcentaje")
    private String porcentaje;
    @SerializedName("frecuencia")
    private String frecuencia;
    @SerializedName("longitud_coo")
    private String longitud;
    @SerializedName("latitud_coo")
    private String latitud;

    public PuntoCritico(String codigo_paf, String fecha, String pais, String area, String factor, String variable, String porcentaje, String frecuencia, String longitud, String latitud) {
        this.codigo_paf = codigo_paf;
        this.fecha = fecha;
        this.pais = pais;
        this.area = area;
        this.factor = factor;
        this.variable = variable;
        this.porcentaje = porcentaje;
        this.frecuencia = frecuencia;
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getCodigo_paf() {
        return codigo_paf;
    }

    public void setCodigo_paf(String codigo_paf) {
        this.codigo_paf = codigo_paf;
    }
}
