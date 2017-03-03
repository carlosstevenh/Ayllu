package com.example.edwin.ayllu.domain;

import android.content.ContentValues;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;

import static com.example.edwin.ayllu.domain.TramoContract.TramoEntry;

public class Tramo {
    //----------------------------------------------------------------------------------------------
    //Atributos de la clase Tramo
    @SerializedName(JsonKeys.CODIGO_TRAMO)
    int codigo_t;
    @SerializedName(JsonKeys.DESCRIPCION_TRAMO)
    String descripcion_t;
    @SerializedName(JsonKeys.PAIS)
    String pais_t;

    //----------------------------------------------------------------------------------------------
    //Constructor de la clase Tramo
    public Tramo(int codigo, String descripcion, String pais) {
        this.codigo_t = codigo;
        this.descripcion_t = descripcion;
        this.pais_t = pais;
    }

    //----------------------------------------------------------------------------------------------
    //Constructor Vacio
    public Tramo() {
    }
    //----------------------------------------------------------------------------------------------
    //METODO: Procesar datos para la tabla Tramos
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(TramoEntry.CODIGO, codigo_t);
        values.put(TramoEntry.DESCRIPCION, descripcion_t);
        values.put(TramoEntry.PAIS, pais_t);
        return values;
    }

    //----------------------------------------------------------------------------------------------
    //Getter y Setter de la clase Tramo
    public int getCodigo_t() {
        return codigo_t;
    }

    public void setCodigo_t(int codigo_t) {
        this.codigo_t = codigo_t;
    }

    public String getDescripcion_t() {
        return descripcion_t;
    }

    public void setDescripcion_t(String descripcion_t) {
        this.descripcion_t = descripcion_t;
    }

    public String getPais_t() {
        return pais_t;
    }

    public void setPais_t(String pais_t) {
        this.pais_t = pais_t;
    }
}
