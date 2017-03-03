package com.example.edwin.ayllu.domain;

import android.content.ContentValues;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;
import static com.example.edwin.ayllu.domain.SubtramoContract.SubtramoEntry;

public class Subtramo {
    //----------------------------------------------------------------------------------------------
    //Atributos de la clase Subtramo
    @SerializedName(JsonKeys.CODIGO_SUBTRAMO)
    int codigo_sub;
    @SerializedName(JsonKeys.DESCRIPCION_SUBTRAMO)
    String descripcion_sub;
    @SerializedName(JsonKeys.TRAMO)
    int tramo_sub;

    //----------------------------------------------------------------------------------------------
    //Constructor de la clase Subtramo
    public Subtramo(int codigo, String descripcion, int tramo) {
        this.codigo_sub = codigo;
        this.descripcion_sub = descripcion;
        this.tramo_sub = tramo;
    }

    //----------------------------------------------------------------------------------------------
    //Constructor vacio
    public Subtramo() {
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesar datos para la tabla Subtramos
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(SubtramoEntry.CODIGO, codigo_sub);
        values.put(SubtramoEntry.DESCRIPCION, descripcion_sub);
        values.put(SubtramoEntry.TRAMO, tramo_sub);
        return values;
    }

    //----------------------------------------------------------------------------------------------
    //Getter y Setter de la clase Subtramo
    public int getCodigo_sub() {
        return codigo_sub;
    }

    public void setCodigo_sub(int codigo_sub) {
        this.codigo_sub = codigo_sub;
    }

    public String getDescripcion_sub() {
        return descripcion_sub;
    }

    public void setDescripcion_sub(String descripcion_sub) {
        this.descripcion_sub = descripcion_sub;
    }

    public int getTramo_sub() {
        return tramo_sub;
    }

    public void setTramo_sub(int tramo_sub) {
        this.tramo_sub = tramo_sub;
    }
}
