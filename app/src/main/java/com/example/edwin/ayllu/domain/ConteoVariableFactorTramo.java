package com.example.edwin.ayllu.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steven on 2/05/17.
 */

public class ConteoVariableFactorTramo {

    @SerializedName("codigo_var")
    private String codigo;
    @SerializedName("nombre_var")
    private String nombre;
    @SerializedName("cantidad")
    private String cantidad;

    public ConteoVariableFactorTramo(String cantidad, String nombre, String codigo) {
        this.cantidad = cantidad;
        this.nombre = nombre;
        this.codigo = codigo;
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
