package com.example.edwin.ayllu.domain;

import android.provider.BaseColumns;

public class SubtramoContract {
    public static abstract class SubtramoEntry implements BaseColumns {
        public static final String TABLE_NAME = "subtramos";
        public static final String CODIGO = "codigo_subtramo";
        public static final String DESCRIPCION = "descripcion_subtramo";
        public static final String TRAMO = "tramo";
    }
}
