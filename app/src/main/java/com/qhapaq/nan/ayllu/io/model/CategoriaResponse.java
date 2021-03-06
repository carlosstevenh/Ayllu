package com.qhapaq.nan.ayllu.io.model;

import com.qhapaq.nan.ayllu.domain.Categoria;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CategoriaResponse {
    @SerializedName(JsonKeys.CATEGORIAS_RESULTS)
    CategoriaResponse.CategoriaResult result;

    public void setCategorias(ArrayList<Categoria> categorias){
        this.result.categorias = categorias;
    }

    public ArrayList<Categoria> getCategorias(){
        return result.categorias;
    }

    private class CategoriaResult {
        ArrayList<Categoria> categorias;
    }
}
