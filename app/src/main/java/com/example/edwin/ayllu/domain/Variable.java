package com.example.edwin.ayllu.domain;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;

/**
 * Created by edwin on 17/11/16.
 */

public class Variable {
    @SerializedName(JsonKeys.CODIGO_FACTOR_VARIABLE) String factor;
    @SerializedName(JsonKeys.NOMBRE_VARIABLE) String nombre_variable;
    @SerializedName(JsonKeys.CODIGO_VARIABLE) String codigo_variable;

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }

    public String getCodigo_variable() {
        return codigo_variable;
    }

    public void setCodigo_variable(String codigo_variable) {
        this.codigo_variable = codigo_variable;
    }

    public String getNombre_variable() {
        return nombre_variable;
    }

    public void setNombre_variable(String nombre_variable) {
        this.nombre_variable = nombre_variable;
    }

    public Variable(String codigo, String nombre, String cod_factor){
        this.codigo_variable = codigo;
        this.nombre_variable = nombre;
        this.factor = cod_factor;
    }
}
