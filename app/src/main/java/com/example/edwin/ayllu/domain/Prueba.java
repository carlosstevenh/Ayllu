package com.example.edwin.ayllu.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steven on 8/03/17.
 */

public class Prueba {
    @SerializedName("nombre_pru1")
    private String prueba1;
    @SerializedName("nombre_pru2")
    private String prueba2;
    @SerializedName("nombre_pru3")
    private String prueba3;


    public Prueba(String prueba1, String prueba2, String prueba3) {
        this.prueba1 = prueba1;
        this.prueba2 = prueba2;
        this.prueba3 = prueba3;
    }

    //==============================================================================================
    //
    public String getPruebas(int i) {
        if (i == 1) return this.prueba1;
        else if (i == 2) return this.prueba2;
        else return this.prueba3;
    }

    public int getSize(){
        int size = 0;

        if(! this.prueba1.equals("null")) size++;
        if(! this.prueba2.equals("null")) size++;
        if(! this.prueba3.equals("null")) size++;

        return size;
    }

    public String getPrueba1() {
        return prueba1;
    }

    public void setPrueba1(String prueba1) {
        this.prueba1 = prueba1;
    }

    public String getPrueba2() {
        return prueba2;
    }

    public void setPrueba2(String prueba2) {
        this.prueba2 = prueba2;
    }

    public String getPrueba3() {
        return prueba3;
    }

    public void setPrueba3(String prueba3) {
        this.prueba3 = prueba3;
    }
}
