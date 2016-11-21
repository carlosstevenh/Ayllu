package com.example.edwin.ayllu.domain;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;

/**
 * Created by edwin on 17/11/16.
 */

public class Factor {
    @SerializedName(JsonKeys.CODIGO_FACTOR) String codigo_factor;
    @SerializedName(JsonKeys.NOMBRE_FACTOR) String nombre_factor;

    public Factor(String codigo, String nombre){
        this.codigo_factor = codigo;
        this.nombre_factor = nombre;
    }

    public String getCodigo_factor() {
        return codigo_factor;
    }

    public void setCodigo_factor(String codigo_factor) {
        this.codigo_factor = codigo_factor;
    }

    public String getNombre_factor() {
        return nombre_factor;
    }

    public void setNombre_factor(String nombre_factor) {
        this.nombre_factor = nombre_factor;
    }
}
