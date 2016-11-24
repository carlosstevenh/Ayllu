package com.example.edwin.ayllu.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steven on 5/11/16.
 */

public class Usuario {
    @SerializedName("codigo_usu")
    private  int codigo_usu;
    @SerializedName("identificacion_usu")
    private String identificacion_usu;
    @SerializedName("nombre_usu")
    private String nombre_usu;
    @SerializedName("apellido_usu")
    private String apellido_usu;
    @SerializedName("tipo_usu")
    private String tipo_usu;
    @SerializedName("contrasena_usu")
    private String contrasena_usu;
    @SerializedName("claveapi")
    private String clave_api;
    @SerializedName("pais_usu")
    private String pais_usu;

    public int getCodigo_usu() {
        return codigo_usu;
    }

    public void setCodigo_usu(int codigo_usu) {
        this.codigo_usu = codigo_usu;
    }

    public String getIdentificacion_usu() {
        return identificacion_usu;
    }

    public void setIdentificacion_usu(String identificacion_usu) {
        this.identificacion_usu = identificacion_usu;
    }
    public String getNombre_usu() {
        return nombre_usu;
    }

    public void setNombre_usu(String nombre_usu) {
        this.nombre_usu = nombre_usu;
    }

    public String getApellido_usu() {
        return apellido_usu;
    }

    public void setApellido_usu(String apellido_usu) {
        this.apellido_usu = apellido_usu;
    }

    public String getTipo_usu() {
        return tipo_usu;
    }

    public void setTipo_usu(String tipo_usu) {
        this.tipo_usu = tipo_usu;
    }

    public String getContrasena_usu() {
        return contrasena_usu;
    }

    public String getClave_api() {
        return clave_api;
    }

    public void setClave_api(String clave_api) {
        this.clave_api = clave_api;
    }

    public void setContrasena_usu(String contrasena_usu) {
        this.contrasena_usu = contrasena_usu;
    }

    public String getPais_usu() {
        return pais_usu;
    }

    public void setPais_usu(String pais_usu) {
        this.pais_usu = pais_usu;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "codigo_usu=" + codigo_usu +
                ", identificacion_usu='" + identificacion_usu + '\'' +
                ", nombre_usu='" + nombre_usu + '\'' +
                ", apellido_usu='" + apellido_usu + '\'' +
                ", tipo_usu='" + tipo_usu + '\'' +
                ", contrasena_usu='" + contrasena_usu + '\'' +
                ", clave_api='" + clave_api + '\'' +
                ", pais_usu='" + pais_usu + '\'' +
                '}';
    }
}
