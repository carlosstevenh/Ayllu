package com.qhapaq.nan.ayllu.domain.variable;

import android.content.ContentValues;

import com.qhapaq.nan.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;

public class Variable {
    //----------------------------------------------------------------------------------------------
    //Atributos de la clase Variable
    @SerializedName(JsonKeys.CODIGO_VARIABLE)
    String cod_variable;
    @SerializedName(JsonKeys.NOMBRE_VARIABLE)
    String nombre_var;
    @SerializedName(JsonKeys.FACTOR)
    String factor_var;

    //----------------------------------------------------------------------------------------------
    //Constructor de la clase Variable
    public Variable(String cod_variable, String nombre_var, String factor_var) {
        this.cod_variable = cod_variable;
        this.nombre_var = nombre_var;
        this.factor_var = factor_var;
    }

    //----------------------------------------------------------------------------------------------
    //Constructor vacio
    public Variable() {

    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesar datos para la tabla Variables
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(VariableContract.VariableEntry.CODIGO, cod_variable);
        values.put(VariableContract.VariableEntry.NOMBRE, nombre_var);
        values.put(VariableContract.VariableEntry.FACTOR, factor_var);
        return values;
    }

    //----------------------------------------------------------------------------------------------
    //Getter y Setter de la clase Variable
    public String getNombre_var() {
        return nombre_var;
    }

    public void setNombre_var(String nombre_var) {
        this.nombre_var = nombre_var;
    }

    public String getFactor_var() {
        return factor_var;
    }

    public String getCod_variable() {
        return cod_variable;
    }

    public void setCod_variable(String cod_variable) {
        this.cod_variable = cod_variable;
    }

    public void setFactor_var(String factor_var) {
        this.factor_var = factor_var;
    }
}
