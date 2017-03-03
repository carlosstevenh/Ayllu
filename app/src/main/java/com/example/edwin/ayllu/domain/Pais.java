package com.example.edwin.ayllu.domain;

import android.content.ContentValues;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;

import static com.example.edwin.ayllu.domain.PaisContract.PaisEntry;

public class Pais {
    //----------------------------------------------------------------------------------------------
    //Atributos de la clase Pais
    @SerializedName(JsonKeys.CODIGO_PAIS)
    String codigo_p;
    @SerializedName(JsonKeys.NOMBRE_PAIS)
    String nombre_p;

    //----------------------------------------------------------------------------------------------
    //Constructor de la clase Pais
    public Pais(String codigo_p, String nombre_p) {
        this.codigo_p = codigo_p;
        this.nombre_p = nombre_p;
    }

    //----------------------------------------------------------------------------------------------
    //Constructor Vacio
    public Pais() {
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesar datos para la tabla Paises
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(PaisEntry.CODIGO, codigo_p);
        values.put(PaisEntry.NOMBRE, nombre_p);
        return values;
    }

    //----------------------------------------------------------------------------------------------
    //Getter y Setter de la clase Tramo
    public String getCodigo_p() {
        return codigo_p;
    }

    public void setCodigo_p(String codigo_p) {
        this.codigo_p = codigo_p;
    }

    public String getNombre_p() {
        return nombre_p;
    }

    public void setNombre_p(String nombre_p) {
        this.nombre_p = nombre_p;
    }
}
