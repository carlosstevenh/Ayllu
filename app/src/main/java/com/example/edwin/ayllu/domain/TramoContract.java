package com.example.edwin.ayllu.domain;

import android.provider.BaseColumns;

public class TramoContract {
    public static abstract class TramoEntry implements BaseColumns {
        public static final String TABLE_NAME = "tramos";
        public static final String CODIGO = "codigo_tramo";
        public static final String DESCRIPCION = "descripcion_tramo";
        public static final String PAIS = "pais";
    }
}
