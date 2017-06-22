package com.example.edwin.ayllu.domain;

import android.content.ContentValues;

import static com.example.edwin.ayllu.domain.TaskContract.TaskEntry;

public class Task {
    private String monitor;
    private String area;
    private String variable;
    private String latitud;
    private String longitud;

    private String fecha;
    private String repercusiones;
    private String origen;

    private int porcentaje;
    private int frecuencia;

    private String nombre;
    private String nombre2;
    private String nombre3;

    private String tipo;

    public Task(String monitor, String variable, String area, String latitud, String longitud,
                String fecha, String repercusiones, String origen, int porcentaje,
                int frecuencia, String nombre, String nombre2, String nombre3, String tipo) {
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
        this.nombre2 = nombre2;
        this.nombre3 = nombre3;
        this.tipo = tipo;
    }

    public Task() {
        this.monitor = "";
        this.variable = "";
        this.area = "";
        this.latitud = "0";
        this.longitud = "0";
        this.fecha = "";
        this.repercusiones = "1001";
        this.origen = "10";
        this.porcentaje = 1;
        this.frecuencia  = 1;
        this.nombre = "null";
        this.nombre2 = "null";
        this.nombre3 = "null";
        this.tipo = "N";
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
        values.put(TaskEntry.NOMBRE2, nombre2);
        values.put(TaskEntry.NOMBRE3, nombre3);
        values.put(TaskEntry.TIPO, tipo);

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

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
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

    public String getNombre2() {
        return nombre2;
    }

    public void setNombre2(String nombre2) {
        this.nombre2 = nombre2;
    }

    public String getNombre3() {
        return nombre3;
    }

    public void setNombre3(String nombre3) {
        this.nombre3 = nombre3;
    }

    public String getTipo() {return tipo;}

    public void setTipo(String tipo) {this.tipo = tipo;}
}
