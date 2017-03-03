package com.example.edwin.ayllu.domain;

import android.provider.BaseColumns;

public class FactorContract {
    public static abstract class FactorEntry implements BaseColumns {
        public static final String TABLE_NAME = "factores";
        public static final String CODIGO = "codigo_fac";
        public static final String NOMBRE = "nombre_fac";
    }
}
