package com.example.edwin.ayllu.domain;

import android.provider.BaseColumns;

public class AreaContract {
    public static abstract class AreaEntry implements BaseColumns {
        public static final String TABLE_NAME = "areas";
        public static final String CODIGO = "codigo_area";
        public static final String TIPO = "tipo_area";
        public static final String PROPIEDAD_NOMINADA = "propiedad_nominada";
        public static final String SECCION = "seccion";
    }
}
