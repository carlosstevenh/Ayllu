package com.example.edwin.ayllu.domain;

public class Task {
    final String variable;
    final String area;
    final int latitud;
    final int longitud;

    public Task(String variable, String area, int latitud, int longitud) {
        this.variable = variable;
        this.area = area;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
