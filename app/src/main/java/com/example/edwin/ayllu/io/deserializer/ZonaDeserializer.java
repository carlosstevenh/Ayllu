package com.example.edwin.ayllu.io.deserializer;

import com.example.edwin.ayllu.domain.Area;
import com.example.edwin.ayllu.domain.Pais;
import com.example.edwin.ayllu.domain.Seccion;
import com.example.edwin.ayllu.domain.Subtramo;
import com.example.edwin.ayllu.domain.Tramo;
import com.example.edwin.ayllu.domain.Zona;
import com.example.edwin.ayllu.io.model.JsonKeys;
import com.example.edwin.ayllu.io.model.ZonaResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ZonaDeserializer implements JsonDeserializer<ZonaResponse> {
    @Override
    public ZonaResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        ZonaResponse response = gson.fromJson(json, ZonaResponse.class);

        //obtener el objeto Zona
        JsonObject zonaResponseData = json.getAsJsonObject().getAsJsonObject(JsonKeys.ZONAS_RESULTS);

        //obtener el array Paises
        JsonArray paisArray = zonaResponseData.getAsJsonArray(JsonKeys.PAIS_ARRAY);

        //convertir el json array a objetos de la clase zona
        response.setZonas(extracZonasFromJsonArray(paisArray));
        return response;
    }

    private ArrayList<Zona> extracZonasFromJsonArray(JsonArray array) {

        ArrayList<Zona> zonas = new ArrayList<>();
        ArrayList<Pais> paises = new ArrayList<>();
        ArrayList<Tramo> tramos = new ArrayList<>();
        ArrayList<Subtramo> subtramos = new ArrayList<>();
        ArrayList<Seccion> secciones = new ArrayList<>();
        ArrayList<Area> areas = new ArrayList<>();


        for (int i = 0; i < array.size(); i++) {
            JsonObject paisData = array.get(i).getAsJsonObject();
            Pais currentPais = new Pais(paisData.get(JsonKeys.CODIGO_PAIS).getAsString(),
                    paisData.get(JsonKeys.NOMBRE_PAIS).getAsString());

            paises.add(currentPais);
            JsonArray tramoArray = paisData.getAsJsonArray(JsonKeys.TRAMOS_ARRAY);

            for (int j = 0; j < tramoArray.size(); j++) {
                JsonObject tramoData = tramoArray.get(j).getAsJsonObject();
                Tramo currentTramo = new Tramo(
                        tramoData.get(JsonKeys.CODIGO_TRAMO).getAsInt(),
                        tramoData.get(JsonKeys.DESCRIPCION_TRAMO).getAsString(),
                        tramoData.get(JsonKeys.PAIS).getAsString());

                tramos.add(currentTramo);
                JsonArray subtramoArray = tramoData.getAsJsonArray(JsonKeys.SUBTRAMOS_ARRAY);

                for (int k = 0; k < subtramoArray.size(); k++) {
                    JsonObject subtramoData = subtramoArray.get(k).getAsJsonObject();
                    Subtramo currentSubtramo = new Subtramo(
                            subtramoData.get(JsonKeys.CODIGO_SUBTRAMO).getAsInt(),
                            subtramoData.get(JsonKeys.DESCRIPCION_SUBTRAMO).getAsString(),
                            subtramoData.get(JsonKeys.TRAMO).getAsInt());

                    subtramos.add(currentSubtramo);
                    JsonArray seccionArray = subtramoData.getAsJsonArray(JsonKeys.SECCIONES_ARRAY);

                    for (int l = 0; l < seccionArray.size(); l++) {
                        JsonObject seccionData = seccionArray.get(l).getAsJsonObject();
                        Seccion currentSeccion = new Seccion(
                                seccionData.get(JsonKeys.CODIGO_SECCION).getAsInt(),
                                seccionData.get(JsonKeys.DESCRIPCION_SECCION).getAsString(),
                                seccionData.get(JsonKeys.SUBTRAMO).getAsInt());

                        secciones.add(currentSeccion);
                        JsonArray areaArray = seccionData.getAsJsonArray(JsonKeys.AREAS_ARRAY);

                        for (int m = 0; m < areaArray.size(); m++) {
                            JsonObject areaData = areaArray.get(m).getAsJsonObject();
                            Area currentArea = new Area(
                                    areaData.get(JsonKeys.CODIGO_AREA).getAsInt(),
                                    areaData.get(JsonKeys.TIPO_AREA).getAsString(),
                                    areaData.get(JsonKeys.PROPIEDAD_NOMINADA).getAsString(),
                                    areaData.get(JsonKeys.SECCION).getAsInt()
                            );

                            areas.add(currentArea);
                        }
                    }
                }
            }

        }

        Zona zona = new Zona(paises, tramos, subtramos, secciones, areas);
        zonas.add(zona);
        return zonas;
    }
}
