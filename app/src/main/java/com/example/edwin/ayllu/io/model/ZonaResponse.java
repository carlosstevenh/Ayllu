package com.example.edwin.ayllu.io.model;

import com.example.edwin.ayllu.domain.Zona;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ZonaResponse {
    @SerializedName(JsonKeys.ZONAS_RESULTS)
    ZonaResponse.ZonaResult result;

    public void setZonas(ArrayList<Zona> zonas){
        this.result.zonas = zonas;
    }

    public ArrayList<Zona> getZonas(){
        return result.zonas;
    }

    private class ZonaResult {
        ArrayList<Zona> zonas;
    }
}
