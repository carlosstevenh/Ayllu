package com.example.edwin.ayllu.domain;

import android.content.ContentValues;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;
import static com.example.edwin.ayllu.domain.FactorContract.FactorEntry;

public class Factor {
    //----------------------------------------------------------------------------------------------
    //Atributos de la clase Factor
    @SerializedName(JsonKeys.CODIGO_FACTOR)
    String codigo_fac;
    @SerializedName(JsonKeys.NOMBRE_FACTOR)
    String nombre_fac;

    //----------------------------------------------------------------------------------------------
    //Constuctor de la clase Factor
    public Factor(String codigo, String nombre) {
        this.codigo_fac = codigo;
        this.nombre_fac = nombre;
    }

    //----------------------------------------------------------------------------------------------
    //Constructor vacio
    public Factor() {
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesar datos para la tabla Factores
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(FactorEntry.CODIGO, codigo_fac);
        values.put(FactorEntry.NOMBRE, nombre_fac);
        return values;
    }

    //----------------------------------------------------------------------------------------------
    //Getter y Setter de la clase Factor
    public String getCodigo_fac() {
        return codigo_fac;
    }

    public void setCodigo_fac(String codigo_fac) {
        this.codigo_fac = codigo_fac;
    }

    public String getNombre_fac() {
        return nombre_fac;
    }

    public void setNombre_fac(String nombre_fac) {
        this.nombre_fac = nombre_fac;
    }
}
