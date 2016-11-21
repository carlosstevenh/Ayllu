package com.example.edwin.ayllu.domain;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;

/**
 * Created by edwin on 1/11/16.
 */

public class Subtramo {
    @SerializedName(JsonKeys.CODIGO_SUBTRAMO) String codigo_subtramo;
    @SerializedName(JsonKeys.DESCRIPCION_SUBTRAMO) String descripcion_subtramo;
    @SerializedName(JsonKeys.CODIGO_TRAMO_SUBTRAMO) String tramo;

    public Subtramo(String codigo, String descripcion, String tramo){
        this.codigo_subtramo = codigo;
        this.descripcion_subtramo = descripcion;
        this.tramo = tramo;
    }

    public  Subtramo(){}

    public String getCodigo_subtramo() {
        return codigo_subtramo;
    }

    public void setCodigo_subtramo(String codigo_subtramo) {
        this.codigo_subtramo = codigo_subtramo;
    }

    public String getDescripcion_subtramo() {
        return descripcion_subtramo;
    }

    public void setDescripcion_subtramo(String descripcion_subtramo) {
        this.descripcion_subtramo = descripcion_subtramo;
    }

    public String getTramo() {
        return tramo;
    }

    public void setTramo(String tramo) {
        this.tramo = tramo;
    }
}
