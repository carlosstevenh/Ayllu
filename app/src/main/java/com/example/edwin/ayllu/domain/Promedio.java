package com.example.edwin.ayllu.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steven on 28/06/17.
 */

public class Promedio {

    @SerializedName("procesar_datos")
    private String datos ;

    public Promedio(String datos) {
        this.datos = datos;
    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }
}
