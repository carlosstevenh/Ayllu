package com.example.edwin.ayllu.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steven on 26/02/17.
 */

public class ResumenMonitor {
    @SerializedName("fecha")
    private String fecha;
    @SerializedName("monitoreos")
    private String monitoreos;
    @SerializedName("respuestas")
    private String respuestas;

    public ResumenMonitor(String fecha, String monitoreos, String respuestas) {
        this.fecha = fecha;
        this.monitoreos = monitoreos;
        this.respuestas = respuestas;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMonitoreos() {
        return monitoreos;
    }

    public void setMonitoreos(String monitoreos) {
        this.monitoreos = monitoreos;
    }

    public String getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(String respuestas) {
        this.respuestas = respuestas;
    }
}