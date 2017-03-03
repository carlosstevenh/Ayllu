package com.example.edwin.ayllu.domain;

import android.provider.BaseColumns;

public class SeccionContract {
    public static abstract class SeccionEntry implements BaseColumns {
        public static final String TABLE_NAME = "secciones";
        public static final String CODIGO = "codigo_seccion";
        public static final String DESCRIPCION = "descripcion_seccion";
        public static final String SUBTRAMO = "subtramo";
    }
}
