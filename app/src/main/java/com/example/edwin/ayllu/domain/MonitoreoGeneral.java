package com.example.edwin.ayllu.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steven on 18/02/17.
 */

public class MonitoreoGeneral {
    @SerializedName("codigo_paf")
    private String codigo_paf;
    @SerializedName("fecha_mon")
    private String fecha;
    @SerializedName("nombre_usu")
    private String monitor;
    @SerializedName("nombre_pais")
    private String pais;
    @SerializedName("descripcion_tramo")
    private String tramo;
    @SerializedName("descripcion_subtramo")
    private String subtramo;
    @SerializedName("descripcion_seccion")
    private String seccion;
    @SerializedName("propiedad_nominada")
    private String area;
    @SerializedName("nombre_fac")
    private String factor;
    @SerializedName("nombre_var")
    private String variable;
    @SerializedName("longitud_coo")
    private String longitud;
    @SerializedName("latitud_coo")
    private String latitud;

    public MonitoreoGeneral(String codigo_paf, String fecha, String monitor, String pais, String tramo, String subtramo, String seccion, String area, String factor, String variable, String longitud, String latitud) {
        this.codigo_paf = codigo_paf;
        this.fecha = fecha;
        this.monitor = monitor;
        this.pais = pais;
        this.tramo = tramo;
        this.subtramo = subtramo;
        this.seccion = seccion;
        this.area = area;
        this.factor = factor;
        this.variable = variable;
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public String getCodigo_paf() {
        return codigo_paf;
    }

    public void setCodigo_paf(String codigo_paf) {
        this.codigo_paf = codigo_paf;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTramo() {
        return tramo;
    }

    public void setTramo(String tramo) {
        this.tramo = tramo;
    }

    public String getSubtramo() {
        return subtramo;
    }

    public void setSubtramo(String subtramo) {
        this.subtramo = subtramo;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
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
}
