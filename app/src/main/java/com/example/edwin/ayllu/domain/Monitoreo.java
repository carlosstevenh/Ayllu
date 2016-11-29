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

    public Monitoreo(String date, String variable, String codigo) {
        this.date = date;
        this.variable = variable;
        this.codigo = codigo;
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
}
