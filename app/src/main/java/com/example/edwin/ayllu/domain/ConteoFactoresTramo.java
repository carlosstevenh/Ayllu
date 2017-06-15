package com.example.edwin.ayllu.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steven on 30/04/17.
 */

public class ConteoFactoresTramo {
    @SerializedName("codigo_fac")
    private String codigo;
    @SerializedName("nombre_fac")
    private String nombre;
    @SerializedName("cantidad")
    private String cantidad;

    public ConteoFactoresTramo(String codigo, String nombre, String cantidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }
}
