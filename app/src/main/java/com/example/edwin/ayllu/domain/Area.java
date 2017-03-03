package com.example.edwin.ayllu.domain;

import android.content.ContentValues;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;
import static com.example.edwin.ayllu.domain.AreaContract.AreaEntry;

public class Area {
    //----------------------------------------------------------------------------------------------
    //Atributos de la clase Area
    @SerializedName(JsonKeys.CODIGO_AREA)
    int codigo_area;
    @SerializedName(JsonKeys.TIPO_AREA)
    String tipo_area;
    @SerializedName(JsonKeys.PROPIEDAD_NOMINADA)
    String propiedad_nominada;
    @SerializedName(JsonKeys.SECCION)
    int seccion_area;

    //----------------------------------------------------------------------------------------------
    //Constructor de la clase Area
    public Area(int codigo_area, String tipo_area, String propiedad_nominada, int seccion_area) {
        this.codigo_area = codigo_area;
        this.tipo_area = tipo_area;
        this.propiedad_nominada = propiedad_nominada;
        this.seccion_area = seccion_area;
    }

    //----------------------------------------------------------------------------------------------
    //Constructor vacio
    public Area() {
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesar datos para la tabla Areas
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(AreaEntry.CODIGO, codigo_area);
        values.put(AreaEntry.TIPO, tipo_area);
        values.put(AreaEntry.PROPIEDAD_NOMINADA, propiedad_nominada);
        values.put(AreaEntry.SECCION, seccion_area);
        return values;
    }

    //----------------------------------------------------------------------------------------------
    //Getter y Setter de la clase Area
    public int getCodigo_area() {
        return codigo_area;
    }

    public void setCodigo_area(int codigo_area) {
        this.codigo_area = codigo_area;
    }

    public String getTipo_area() {
        return tipo_area;
    }

    public void setTipo_area(String tipo_area) {
        this.tipo_area = tipo_area;
    }

    public String getPropiedad_nominada() {
        return propiedad_nominada;
    }

    public void setPropiedad_nominada(String propiedad_nominada) {
        this.propiedad_nominada = propiedad_nominada;
    }

    public int getSeccion_area() {
        return seccion_area;
    }

    public void setSeccion_area(int seccion_area) {
        this.seccion_area = seccion_area;
    }
}
