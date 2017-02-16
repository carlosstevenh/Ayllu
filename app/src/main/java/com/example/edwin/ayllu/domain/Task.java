package com.example.edwin.ayllu.domain;

import android.content.ContentValues;
import static com.example.edwin.ayllu.domain.TaskContract.TaskEntry;

public class Task {
    final String monitor;
    final String area;
    final String variable;
    final int latitud;
    final int longitud;

    final String fecha;
    final String repercusiones;
    final String origen;

    final int porcentaje;
    final int frecuencia;
    final String nombre;

    public String getMonitor() {
        return monitor;
    }

    public String getArea() {
        return area;
    }

    public int getFrecuencia() {
        return frecuencia;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public String getOrigen() {
        return origen;
    }

    public String getRepercusiones() {
        return repercusiones;
    }

    public String getFecha() {
        return fecha;
    }

    public int getLongitud() {
        return longitud;
    }

    public int getLatitud() {
        return latitud;
    }

    public String getVariable() {
        return variable;
    }

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
}
