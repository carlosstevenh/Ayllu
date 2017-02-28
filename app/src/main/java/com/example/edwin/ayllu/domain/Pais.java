package com.example.edwin.ayllu.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steven on 19/02/17.
 */

public class Pais {
    @SerializedName("codigo_pais")
    private String codigo;
    @SerializedName("nombre_pais")
    private String nombre;

    public Pais(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
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
}
