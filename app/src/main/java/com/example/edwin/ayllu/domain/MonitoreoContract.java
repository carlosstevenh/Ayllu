package com.example.edwin.ayllu.domain;

import android.provider.BaseColumns;

public class MonitoreoContract {
    public static abstract class MonitoreoEntry implements BaseColumns {
        public static final String TABLE_NAME = "monitoreos";
        public static final String PUNTO = "codigo";
        public static final String FECHA = "fecha";
        public static final String MONITOR = "monitor";
        public static final String PROPIEDAD = "propiedad";
        public static final String VARIABLE = "variable";
        public static final String LATITUD = "latitud";
        public static final String LONGITUD = "longitud";
        public static final String REPERCUSIONES = "repercusiones";
        public static final String ORIGEN = "origen";
        public static final String PORCENTAJE = "porcentaje";
        public static final String FRECUENCIA = "frecuencia";
        public static final String NOMBRE = "nombre";
        public static final String NOMBRE2 = "nombre2";
        public static final String NOMBRE3 = "nombre3";
        public static final String ESTADO = "estado";
    }
}
