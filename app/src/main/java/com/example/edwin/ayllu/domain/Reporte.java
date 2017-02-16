package com.example.edwin.ayllu.domain;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;

public class Reporte {
    @SerializedName(JsonKeys.CODIGO_PAF) int cod_paf;
    @SerializedName(JsonKeys.VARIABLE) String variable;
    @SerializedName(JsonKeys.AREA) int area;
    @SerializedName(JsonKeys.LATITUD_COO) String latitud;
    @SerializedName(JsonKeys.LONGITUD_COO) String longitud;
    @SerializedName(JsonKeys.FECHA_MON) String fecha_mon;
    @SerializedName(JsonKeys.NOMBRE_USU) String usuario;
    @SerializedName(JsonKeys.REPERCUSIONES) String repercusiones;
    @SerializedName(JsonKeys.ORIGEN) String origen;
    @SerializedName(JsonKeys.PORCENTAJE_APA) int porcentaje;
    @SerializedName(JsonKeys.FRECUENCIA_APA) int frecuencia;
    @SerializedName(JsonKeys.FECHA_RES) String fecha_res;
    @SerializedName(JsonKeys.EVALUACION) String evaluacion;
    @SerializedName(JsonKeys.PERSONAL) String personal;
    @SerializedName(JsonKeys.TIEMPO) String tiempo;
    @SerializedName(JsonKeys.PRESUPUESTO) String presupuesto;
    @SerializedName(JsonKeys.RECURSOS) String recursos;
    @SerializedName(JsonKeys.CONOCIMIENTO) String conocimiento;
    @SerializedName(JsonKeys.MONITOR_RES) int monitor_res;

    public Reporte(int cod_paf, int monitor_res, String conocimiento, String recursos, String variable, String latitud, int area, String fecha_mon, String repercusiones, String presupuesto, String tiempo, String personal, String evaluacion, String fecha_res, int frecuencia, int porcentaje, String origen, String usuario, String longitud) {
        this.cod_paf = cod_paf;
        this.monitor_res = monitor_res;
        this.conocimiento = conocimiento;
        this.recursos = recursos;
        this.variable = variable;
        this.latitud = latitud;
        this.area = area;
        this.fecha_mon = fecha_mon;
        this.repercusiones = repercusiones;
        this.presupuesto = presupuesto;
        this.tiempo = tiempo;
        this.personal = personal;
        this.evaluacion = evaluacion;
        this.fecha_res = fecha_res;
        this.frecuencia = frecuencia;
        this.porcentaje = porcentaje;
        this.origen = origen;
        this.usuario = usuario;
        this.longitud = longitud;
    }

    public Reporte() {
    }

    public int getMonitor_res() {
        return monitor_res;
    }

    public void setMonitor_res(int monitor_res) {
        this.monitor_res = monitor_res;
    }

    public String getConocimiento() {
        return conocimiento;
    }

    public void setConocimiento(String conocimiento) {
        this.conocimiento = conocimiento;
    }

    public String getRecursos() {
        return recursos;
    }

    public void setRecursos(String recursos) {
        this.recursos = recursos;
    }

    public String getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(String presupuesto) {
        this.presupuesto = presupuesto;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getPersonal() {
        return personal;
    }

    public void setPersonal(String personal) {
        this.personal = personal;
    }

    public String getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(String evaluacion) {
        this.evaluacion = evaluacion;
    }

    public String getFecha_res() {
        return fecha_res;
    }

    public void setFecha_res(String fecha_res) {
        this.fecha_res = fecha_res;
    }

    public int getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(int frecuencia) {
        this.frecuencia = frecuencia;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getRepercusiones() {
        return repercusiones;
    }

    public void setRepercusiones(String repercusiones) {
        this.repercusiones = repercusiones;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFecha_mon() {
        return fecha_mon;
    }

    public void setFecha_mon(String fecha_mon) {
        this.fecha_mon = fecha_mon;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public int getCod_paf() {
        return cod_paf;
    }

    public void setCod_paf(int cod_paf) {
        this.cod_paf = cod_paf;
    }
}
