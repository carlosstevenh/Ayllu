package com.example.edwin.ayllu.io.deserializer;

import com.example.edwin.ayllu.domain.Area;
import com.example.edwin.ayllu.domain.Factor;
import com.example.edwin.ayllu.domain.Pais;
import com.example.edwin.ayllu.domain.Seccion;
import com.example.edwin.ayllu.domain.Subtramo;
import com.example.edwin.ayllu.domain.Tramo;
import com.example.edwin.ayllu.domain.Categoria;
import com.example.edwin.ayllu.domain.Variable;
import com.example.edwin.ayllu.io.model.CategoriaResponse;
import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CategoriaDeserializer implements JsonDeserializer<CategoriaResponse> {
    @Override
    public CategoriaResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        CategoriaResponse response = gson.fromJson(json, CategoriaResponse.class);

        //obtener el objeto Categoria
        JsonObject categoriaResponseData = json.getAsJsonObject().getAsJsonObject(JsonKeys.CATEGORIAS_RESULTS);

        //obtener el array Factores
        JsonArray factorArray = categoriaResponseData.getAsJsonArray(JsonKeys.FACTORES_ARRAY);

        //convertir el json array a objetos de la clase Categoria
        response.setCategorias(extracCategoriasFromJsonArray(factorArray));
        return response;
    }

    private ArrayList<Categoria> extracCategoriasFromJsonArray(JsonArray array) {

        ArrayList<Categoria> categorias = new ArrayList<>();
        ArrayList<Factor> factores = new ArrayList<>();
        ArrayList<Variable> variables = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            JsonObject factorData = array.get(i).getAsJsonObject();
            Factor currentFactor = new Factor(factorData.get(JsonKeys.CODIGO_FACTOR).getAsString(),
                    factorData.get(JsonKeys.NOMBRE_FACTOR).getAsString());

            factores.add(currentFactor);
            JsonArray variableArray = factorData.getAsJsonArray(JsonKeys.VARIABLES_ARRAY);

            for (int j = 0; j < variableArray.size(); j++) {
                JsonObject variableData = variableArray.get(j).getAsJsonObject();
                Variable currentVariable = new Variable(
                        variableData.get(JsonKeys.CODIGO_VARIABLE).getAsString(),
                        variableData.get(JsonKeys.NOMBRE_VARIABLE).getAsString(),
                        variableData.get(JsonKeys.FACTOR).getAsString());

                variables.add(currentVariable);
            }

        }

        Categoria categoria = new Categoria(factores, variables);
        categorias.add(categoria);
        return categorias;
    }
}
