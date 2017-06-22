package com.example.edwin.ayllu.domain;


import android.provider.BaseColumns;

public class TaskContract {
    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String MONITOR = "monitor";
        public static final String VARIABLE = "variable";
        public static final String AREA = "area";
        public static final String LATITUD = "latitud";
        public static final String LONGITUD = "longitud";
        public static final String FECHA = "fecha";
        public static final String REPERCUSIONES = "repercusiones";
        public static final String ORIGEN = "origen";
        public static final String PORCENTAJE = "porcentaje";
        public static final String FRECUENCIA = "frecuencia";
        public static final String NOMBRE = "nombre";
        public static final String NOMBRE2 = "nombre2";
        public static final String NOMBRE3 = "nombre3";
        public static final String TIPO = "tipo";
    }
}
