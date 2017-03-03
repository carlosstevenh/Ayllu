package com.example.edwin.ayllu.domain;

import android.content.ContentValues;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;
import static com.example.edwin.ayllu.domain.SeccionContract.SeccionEntry;

public class Seccion {
    //----------------------------------------------------------------------------------------------
    //Atributos de la clase Seccion
    @SerializedName(JsonKeys.CODIGO_SECCION)
    int codigo_sec;
    @SerializedName(JsonKeys.DESCRIPCION_SECCION)
    String descripcion_sec;
    @SerializedName(JsonKeys.SUBTRAMO)
    int subtramo_sec;

    //----------------------------------------------------------------------------------------------
    //Constructor de la clase Seccion
    public Seccion(int codigo, String descripcion, int subtramo) {
        this.codigo_sec = codigo;
        this.descripcion_sec = descripcion;
        this.subtramo_sec = subtramo;
    }

    //----------------------------------------------------------------------------------------------
    //Constructor vacio
    public Seccion() {
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesar datos para la tabla Secciones
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(SeccionEntry.CODIGO, codigo_sec);
        values.put(SeccionEntry.DESCRIPCION, descripcion_sec);
        values.put(SeccionEntry.SUBTRAMO, subtramo_sec);
        return values;
    }

    //----------------------------------------------------------------------------------------------
    //Getter y Setter de la clase Seccion
    public int getCodigo_sec() {
        return codigo_sec;
    }

    public void setCodigo_sec(int codigo_sec) {
        this.codigo_sec = codigo_sec;
    }

    public String getDescripcion_sec() {
        return descripcion_sec;
    }

    public void setDescripcion_sec(String descripcion_sec) {
        this.descripcion_sec = descripcion_sec;
    }

    public int getSubtramo_sec() {
        return subtramo_sec;
    }

    public void setSubtramo_sec(int subtramo_sec) {
        this.subtramo_sec = subtramo_sec;
    }
}
