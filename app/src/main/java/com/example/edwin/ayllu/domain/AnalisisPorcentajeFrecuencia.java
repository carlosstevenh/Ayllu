package com.example.edwin.ayllu.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steven on 4/03/17.
 */

public class AnalisisPorcentajeFrecuencia {
    @SerializedName("fecha")
    private String fecha;
    @SerializedName("porcentaje")
    private String procentaje;
    @SerializedName("frecuancia")
    private String frecuencia;

    public AnalisisPorcentajeFrecuencia(String fecha, String procentaje, String frecuencia) {
        this.fecha = fecha;
        this.procentaje = procentaje;
        this.frecuencia = frecuencia;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getProcentaje() {
        return procentaje;
    }

    public void setProcentaje(String procentaje) {
        this.procentaje = procentaje;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }
}
