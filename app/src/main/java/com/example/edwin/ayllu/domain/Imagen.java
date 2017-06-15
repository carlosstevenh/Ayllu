package com.example.edwin.ayllu.domain;

import android.content.ContentValues;
import static com.example.edwin.ayllu.domain.ImagenContract.ImagenEntry;

public class Imagen {
    private String fotografia1;
    private String fotografia2;
    private String fotografia3;

    public Imagen(String fotografia1, String fotografia2, String fotografia3) {
        this.fotografia1 = fotografia1;
        this.fotografia2 = fotografia2;
        this.fotografia3 = fotografia3;
    }

    public Imagen (){
        this.fotografia1 = "null";
        this.fotografia2 = "null";
        this.fotografia3 = "null";
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesar datos para la tabla Factores
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(ImagenEntry.FOTOGRAFIA1, fotografia1);
        values.put(ImagenEntry.FOTOGRAFIA2, fotografia2);
        values.put(ImagenEntry.FOTOGRAFIA3, fotografia3);
        return values;
    }

    public String getFotografia1() {
        return fotografia1;
    }

    public void setFotografia1(String fotografia1) {
        this.fotografia1 = fotografia1;
    }

    public String getFotografia2() {
        return fotografia2;
    }

    public void setFotografia2(String fotografia2) {
        this.fotografia2 = fotografia2;
    }

    public String getFotografia3() {
        return fotografia3;
    }

    public void setFotografia3(String fotografia3) {
        this.fotografia3 = fotografia3;
    }
}
