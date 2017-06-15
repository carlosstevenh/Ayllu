package com.example.edwin.ayllu.domain;

import android.provider.BaseColumns;

public class ImagenContract {
    public static abstract class ImagenEntry implements BaseColumns {
        public static final String TABLE_NAME = "imagenes";
        public static final String FOTOGRAFIA1 = "fotografia1";
        public static final String FOTOGRAFIA2 = "fotografia2";
        public static final String FOTOGRAFIA3 = "fotografia3";
    }
}
