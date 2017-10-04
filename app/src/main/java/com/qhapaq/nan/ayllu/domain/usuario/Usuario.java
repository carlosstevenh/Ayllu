package com.qhapaq.nan.ayllu.domain.usuario;

import android.content.ContentValues;

import com.qhapaq.nan.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;

public class Usuario {
    //----------------------------------------------------------------------------------------------
    //Atributos de la clase Usuario
    @SerializedName(JsonKeys.COD_USU)
    private String codigo_usu;
    @SerializedName(JsonKeys.ID_USU)
    private String identificacion_usu;
    @SerializedName(JsonKeys.NOM_USU)
    private String nombre_usu;
    @SerializedName(JsonKeys.APE_USU)
    private String apellido_usu;
    @SerializedName(JsonKeys.TIPO_USU)
    private String tipo_usu;
    @SerializedName(JsonKeys.ESTADO_USU)
    private String estado_usu;
    @SerializedName(JsonKeys.API_USU)
    private String clave_api;
    @SerializedName(JsonKeys.PAIS_USU)
    private String pais_usu;

    /**
     * =============================================================================================
     * METODO: Constructor de la clase Usuario
     */
    public Usuario(String codigo_usu, String identificacion_usu, String nombre_usu, String apellido_usu, String tipo_usu, String estado_usu, String clave_api, String pais_usu) {
        this.codigo_usu = codigo_usu;
        this.identificacion_usu = identificacion_usu;
        this.nombre_usu = nombre_usu;
        this.apellido_usu = apellido_usu;
        this.tipo_usu = tipo_usu;
        this.estado_usu = estado_usu;
        this.clave_api = clave_api;
        this.pais_usu = pais_usu;
    }

    public Usuario (String identificacion_usu, String clave_api){
        this.codigo_usu = "";
        this.identificacion_usu = identificacion_usu;
        this.nombre_usu = "";
        this.apellido_usu = "";
        this.tipo_usu = "";
        this.estado_usu = "";
        this.clave_api = clave_api;
        this.pais_usu = "";
    }

    /**
     * =============================================================================================
     * METODO: Procesa datos para la tabla Usuarios
     */
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(UsuarioContract.UsuarioEntry.CODIGO, this.codigo_usu);
        values.put(UsuarioContract.UsuarioEntry.IDENTIFICACION, this.identificacion_usu);
        values.put(UsuarioContract.UsuarioEntry.NOMBRE, this.nombre_usu);
        values.put(UsuarioContract.UsuarioEntry.APELLIDO, this.apellido_usu);
        values.put(UsuarioContract.UsuarioEntry.TIPO, this.tipo_usu);
        values.put(UsuarioContract.UsuarioEntry.ESTADO, this.estado_usu);
        values.put(UsuarioContract.UsuarioEntry.PAIS, this.pais_usu);
        values.put(UsuarioContract.UsuarioEntry.CLAVE_API, this.clave_api);
        return values;
    }

    /**
     * =============================================================================================
     * METODOS GETTER Y SETTER DE LA CLASE USUARIO
     * =============================================================================================
     */

    public String getCodigo_usu() {
        return codigo_usu;
    }

    public void setCodigo_usu(String codigo_usu) {
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

    public String getClave_api() {
        return clave_api;
    }

    public void setClave_api(String clave_api) {
        this.clave_api = clave_api;
    }

    public String getPais_usu() {
        return pais_usu;
    }

    public void setPais_usu(String pais_usu) {
        this.pais_usu = pais_usu;
    }

    public String getEstado_usu() {
        return estado_usu;
    }

    public void setEstado_usu(String estado_usu) {
        this.estado_usu = estado_usu;
    }
}
