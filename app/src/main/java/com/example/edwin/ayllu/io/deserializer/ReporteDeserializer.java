package com.example.edwin.ayllu.io.deserializer;

import android.util.Log;

import com.example.edwin.ayllu.domain.Reporte;
import com.example.edwin.ayllu.io.model.JsonKeys;
import com.example.edwin.ayllu.io.model.ReporteResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.jar.JarEntry;

public class ReporteDeserializer implements JsonDeserializer<ReporteResponse>{
    @Override
    public ReporteResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        ReporteResponse response = gson.fromJson(json, ReporteResponse.class);

        //obtener el objeto reporte
        JsonObject reporteResponseData = json.getAsJsonObject().getAsJsonObject(JsonKeys.REPORTE_RESULTS);

        //obtener el array reporte
        JsonArray reporteArray = reporteResponseData.getAsJsonArray(JsonKeys.REPORTE_ARRAY);

        //convertir el json array a objetos de la clase sintoma
        response.setReportes(extracReporteFromJsonArray(reporteArray));
        return response;
    }

    private ArrayList<Reporte> extracReporteFromJsonArray(JsonArray array) {
        ArrayList<Reporte> reportes = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject reporteData = array.get(i).getAsJsonObject();
            Reporte currentReporte = new Reporte();

            int codigo_paf = reporteData.get(JsonKeys.PUNTO_AFECTACION).getAsInt();
            String variable = reporteData.get(JsonKeys.NOMBRE_VARIABLE).getAsString();
            String area = reporteData.get(JsonKeys.PROPIEDAD_NOMINADA).getAsString();
            String latitud = reporteData.get(JsonKeys.LATITUD_COO).getAsString();
            String longitud = reporteData.get(JsonKeys.LONGITUD_COO).getAsString();
            String fecha_mon = reporteData.get(JsonKeys.FECHA_MON).getAsString();
            String usuario = reporteData.get(JsonKeys.NOMBRE_USU).getAsString();
            String repercusiones = reporteData.get(JsonKeys.REPERCUSIONES).getAsString();
            String origen = reporteData.get(JsonKeys.ORIGEN).getAsString();
            int porcentaje = reporteData.get(JsonKeys.PORCENTAJE_APA).getAsInt();
            int frecuencia = reporteData.get(JsonKeys.FRECUENCIA_APA).getAsInt();
            String fecha_res = reporteData.get(JsonKeys.FECHA_RES).getAsString();
            String evaluacion = reporteData.get(JsonKeys.EVALUACION).getAsString();
            String personal = reporteData.get(JsonKeys.PERSONAL).getAsString();
            String tiempo = reporteData.get(JsonKeys.TIEMPO).getAsString();
            String presupuesto = reporteData.get(JsonKeys.PRESUPUESTO).getAsString();
            String recursos = reporteData.get(JsonKeys.RECURSOS).getAsString();
            String conocimiento = reporteData.get(JsonKeys.CONOCIMIENTO).getAsString();
            int monitor_res = reporteData.get(JsonKeys.MONITOR_RES).getAsInt();
            String prueba1 = reporteData.get(JsonKeys.PRUEBA1).getAsString();
            String prueba2 = reporteData.get(JsonKeys.PRUEBA2).getAsString();
            String prueba3 = reporteData.get(JsonKeys.PRUEBA3).getAsString();

            currentReporte.setCod_paf(codigo_paf);
            currentReporte.setVariable(variable);
            currentReporte.setArea(area);
            currentReporte.setLatitud(latitud);
            currentReporte.setLongitud(longitud);
            currentReporte.setFecha_mon(fecha_mon);
            currentReporte.setUsuario(usuario);
            currentReporte.setRepercusiones(repercusiones);
            currentReporte.setOrigen(origen);
            currentReporte.setPorcentaje(porcentaje);
            currentReporte.setFrecuencia(frecuencia);
            currentReporte.setFecha_res(fecha_res);
            currentReporte.setEvaluacion(evaluacion);
            currentReporte.setPersonal(personal);
            currentReporte.setTiempo(tiempo);
            currentReporte.setPresupuesto(presupuesto);
            currentReporte.setRecursos(recursos);
            currentReporte.setConocimiento(conocimiento);
            currentReporte.setMonitor_res(monitor_res);
            currentReporte.setPrueba1(prueba1);
            currentReporte.setPrueba2(prueba2);
            currentReporte.setPrueba3(prueba3);

            reportes.add(currentReporte);

        }
        for (int i = 0; i < reportes.size(); i++){
            Log.e("Reporte",reportes.get(i).getFecha_mon());
        }
        return reportes;
    }
}
