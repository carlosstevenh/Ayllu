package com.example.edwin.ayllu.domain;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;

public class Mensaje {
    @SerializedName(JsonKeys.ESTADO) String estado;
    @SerializedName(JsonKeys.MENSAJE) String descripcion;

    public Mensaje(String estado, String descripcion) {
        this.estado = estado;
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
