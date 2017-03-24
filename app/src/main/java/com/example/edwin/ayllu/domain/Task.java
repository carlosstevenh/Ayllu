package com.example.edwin.ayllu.domain;

import android.content.ContentValues;

import static com.example.edwin.ayllu.domain.TaskContract.TaskEntry;

public class Task {
    String monitor;
    String area;
    String variable;
    int latitud;
    int longitud;

    String fecha;
    String repercusiones;
    String origen;

    int porcentaje;
    int frecuencia;
    String nombre;

    public Task(String monitor, String variable, String area, int latitud, int longitud, String fecha, String repercusiones, String origen, int porcentaje, int frecuencia, String nombre) {
        this.monitor = monitor;
        this.variable = variable;
        this.area = area;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha = fecha;
        this.repercusiones = repercusiones;
        this.origen = origen;
        this.porcentaje = porcentaje;
        this.frecuencia = frecuencia;
        this.nombre = nombre;
    }

    public Task() {
        this.monitor = "";
        this.variable = "";
        this.area = "";
        this.latitud = 0;
        this.longitud = 0;
        this.fecha = "";
        this.repercusiones = "1001";
        this.origen = "10";
        this.porcentaje = 1;
        this.frecuencia  = 1;
        this.nombre = "";
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.MONITOR, monitor);
        values.put(TaskEntry.VARIABLE, variable);
        values.put(TaskEntry.AREA, area);
        values.put(TaskEntry.LATITUD, latitud);
        values.put(TaskEntry.LONGITUD, longitud);
        values.put(TaskEntry.FECHA, fecha);
        values.put(TaskEntry.REPERCUSIONES, repercusiones);
        values.put(TaskEntry.ORIGEN, origen);
        values.put(TaskEntry.PORCENTAJE, porcentaje);
        values.put(TaskEntry.FRECUENCIA, frecuencia);
        values.put(TaskEntry.NOMBRE, nombre);

        return values;
    }

    public String getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public int getLatitud() {
        return latitud;
    }

    public void setLatitud(int latitud) {
        this.latitud = latitud;
    }

    public int getLongitud() {
        return longitud;
    }

    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getRepercusiones() {
        return repercusiones;
    }

    public void setRepercusiones(String repercusiones) {
        this.repercusiones = repercusiones;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public int getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(int frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
