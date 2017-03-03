package com.example.edwin.ayllu.domain;

import android.provider.BaseColumns;

public class VariableContract {
    public static abstract class VariableEntry implements BaseColumns {
        public static final String TABLE_NAME = "variables";
        public static final String CODIGO = "codigo_var";
        public static final String NOMBRE = "nombre_var";
        public static final String FACTOR = "factor";
    }
}
