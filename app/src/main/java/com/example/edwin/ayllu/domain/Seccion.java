package com.example.edwin.ayllu.domain;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;

public class Seccion {
    @SerializedName(JsonKeys.CODIGO_SECCION) String codigo_seccion;
    @SerializedName(JsonKeys.DESCRIPCION_SECCION) String descripcion_seccion;
    @SerializedName(JsonKeys.CODIGO_SUBTRAMO_SECCION) String subtramo;

    public Seccion(String codigo, String descripcion, String subtramo){
        this.codigo_seccion = codigo;
        this.descripcion_seccion = descripcion;
        this.subtramo = subtramo;
    }

    public String getCodigo_seccion() {
        return codigo_seccion;
    }

    public void setCodigo_seccion(String codigo_seccion) {
        this.codigo_seccion = codigo_seccion;
    }

    public String getDescripcion_seccion() {
        return descripcion_seccion;
    }

    public void setDescripcion_seccion(String descripcion_seccion) {
        this.descripcion_seccion = descripcion_seccion;
    }

    public String getSubtramo() {
        return subtramo;
    }

    public void setSubtramo(String subtramo) {
        this.subtramo = subtramo;
    }

    public  Seccion(){}
}
