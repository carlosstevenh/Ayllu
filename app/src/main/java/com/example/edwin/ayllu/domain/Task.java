package com.example.edwin.ayllu.domain;

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
}
