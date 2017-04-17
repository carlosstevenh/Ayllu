package com.example.edwin.ayllu.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steven on 17/04/17.
 */

public class RespuestaInstitucional {

    @SerializedName("fecha")
    private String fecha;
    @SerializedName("evaluacion")
    private String evaluacion;
    @SerializedName("personal")
    private String personal;
    @SerializedName("tiempo")
    private String tiempo;
    @SerializedName("presupuesto")
    private String presupuesto;
    @SerializedName("recursos")
    private String recursos;
    @SerializedName("conocimiento")
    private String conocimiento;

    public RespuestaInstitucional(String fecha, String evaluacion, String personal, String tiempo, String presupuesto, String recursos, String conocimiento) {
        this.fecha = fecha;
        this.evaluacion = evaluacion;
        this.personal = personal;
        this.tiempo = tiempo;
        this.presupuesto = presupuesto;
        this.recursos = recursos;
        this.conocimiento = conocimiento;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(String evaluacion) {
        this.evaluacion = evaluacion;
    }

    public String getPersonal() {
        return personal;
    }

    public void setPersonal(String personal) {
        this.personal = personal;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(String presupuesto) {
        this.presupuesto = presupuesto;
    }

    public String getRecursos() {
        return recursos;
    }

    public void setRecursos(String recursos) {
        this.recursos = recursos;
    }

    public String getConocimiento() {
        return conocimiento;
    }

    public void setConocimiento(String conocimiento) {
        this.conocimiento = conocimiento;
    }
}
