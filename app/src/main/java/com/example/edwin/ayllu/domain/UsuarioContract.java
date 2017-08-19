package com.example.edwin.ayllu.domain;

import android.provider.BaseColumns;

public class UsuarioContract {
    public static abstract class UsuarioEntry implements BaseColumns {
        public static final String TABLE_NAME = "usuarios";
        public static final String CODIGO = "codigo_usu";
        public static final String IDENTIFICACION = "identificacion_usu";
        public static final String NOMBRE = "nombre_usu";
        public static final String APELLIDO = "apellido_usu";
        public static final String TIPO = "tipo_usu";
        public static final String ESTADO = "estado_usu";
        public static final String PAIS = "pais_usu";
        public static final String CLAVE_API = "claveapi";
    }
}
