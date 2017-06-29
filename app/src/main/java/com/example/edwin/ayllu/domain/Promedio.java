package com.example.edwin.ayllu.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steven on 28/06/17.
 */

public class Promedio {

    @SerializedName("ano_promedio")
    private String anio ;

    @SerializedName("semestre_promedio")
    private String semestre ;

    @SerializedName("promedio")
    private String promedio ;

    public Promedio(String anio, String semestre, String promedio) {
        this.anio = anio;
        this.semestre = semestre;
        this.promedio = promedio;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public String getPromedio() {
        return promedio;
    }

    public void setPromedio(String promedio) {
        this.promedio = promedio;
    }
}
