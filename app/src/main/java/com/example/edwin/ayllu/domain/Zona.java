package com.example.edwin.ayllu.domain;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Zona {
    @SerializedName(JsonKeys.PAIS_ARRAY)
    ArrayList<Pais> paises;
    @SerializedName(JsonKeys.TRAMOS_ARRAY)
    ArrayList<Tramo> tramos;
    @SerializedName(JsonKeys.SUBTRAMOS_ARRAY)
    ArrayList<Subtramo> subtramos;
    @SerializedName(JsonKeys.SECCIONES_ARRAY)
    ArrayList<Seccion> secciones;
    @SerializedName(JsonKeys.AREAS_ARRAY)
    ArrayList<Area> areas;

    public Zona(ArrayList<Pais> paises, ArrayList<Tramo> tramos, ArrayList<Subtramo> subtramos, ArrayList<Seccion> secciones, ArrayList<Area> areas) {
        this.paises = paises;
        this.tramos = tramos;
        this.subtramos = subtramos;
        this.secciones = secciones;
        this.areas = areas;
    }

    public ArrayList<Pais> getPaises() {
        return paises;
    }

    public void setPaises(ArrayList<Pais> paises) {
        this.paises = paises;
    }

    public ArrayList<Tramo> getTramos() {
        return tramos;
    }

    public void setTramos(ArrayList<Tramo> tramos) {
        this.tramos = tramos;
    }

    public ArrayList<Subtramo> getSubtramos() {
        return subtramos;
    }

    public void setSubtramos(ArrayList<Subtramo> subtramos) {
        this.subtramos = subtramos;
    }

    public ArrayList<Seccion> getSecciones() {
        return secciones;
    }

    public void setSecciones(ArrayList<Seccion> secciones) {
        this.secciones = secciones;
    }

    public ArrayList<Area> getAreas() {
        return areas;
    }

    public void setAreas(ArrayList<Area> areas) {
        this.areas = areas;
    }
}
