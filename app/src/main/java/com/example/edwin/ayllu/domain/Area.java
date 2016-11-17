package com.example.edwin.ayllu.domain;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;

/**
 * Created by edwin on 1/11/16.
 */

public class Area {
    @SerializedName(JsonKeys.CODIGO_AREA) String codigo_area;
    @SerializedName(JsonKeys.TIPO_AREA) String tipo_area;
    @SerializedName(JsonKeys.PROPIEDAD_NOMINADA) String propiedad_nominada;
    @SerializedName(JsonKeys.CODIGO_SECCION_AREA) String seccion;

    public String getCodigo_area() {
        return codigo_area;
    }

    public void setCodigo_area(String codigo_area) {
        this.codigo_area = codigo_area;
    }

    public String getTipo_area() {
        return tipo_area;
    }

    public void setTipo_area(String tipo_area) {
        this.tipo_area = tipo_area;
    }

    public String getPropiedad_nominada() {
        return propiedad_nominada;
    }

    public void setPropiedad_nominada(String propiedad_nominada) {
        this.propiedad_nominada = propiedad_nominada;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public Area(String codigo, String tipo, String propiedad, String seccion){
        this.codigo_area = codigo;
        this.tipo_area = tipo;
        this.propiedad_nominada = propiedad;
        this.seccion = seccion;

    }

    public  Area(){}
}
