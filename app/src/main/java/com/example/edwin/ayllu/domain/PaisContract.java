package com.example.edwin.ayllu.domain;

import android.provider.BaseColumns;

public class PaisContract {
    public static abstract class PaisEntry implements BaseColumns {
        public static final String TABLE_NAME = "paises";
        public static final String CODIGO = "codigo_pais";
        public static final String NOMBRE = "nombre_pais";
    }
}
