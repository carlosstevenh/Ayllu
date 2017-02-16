package com.example.edwin.ayllu.io.model;

import com.example.edwin.ayllu.domain.Reporte;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReporteResponse {
    @SerializedName(JsonKeys.REPORTE_RESULTS)
    ReporteResult result;

    public void setReportes(ArrayList<Reporte> reportes){
        this.result.reportes = reportes;
    }

    public ArrayList<Reporte> getReportes(){
        return result.reportes;
    }

    private class ReporteResult {
        ArrayList<Reporte> reportes;
    }
}
