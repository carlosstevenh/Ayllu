package com.example.edwin.ayllu.domain;

import android.content.ContentValues;

import com.example.edwin.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;

import static com.example.edwin.ayllu.domain.MonitoreoContract.MonitoreoEntry;

import java.util.ArrayList;

public class Reporte {
    //==============================================================================================
    //ATRIBUTOS DE LA CLASE REPORTE
    @SerializedName(JsonKeys.PUNTO_AFECTACION)
    int cod_paf;
    @SerializedName(JsonKeys.FECHA_MON)
    String fecha_mon;
    @SerializedName(JsonKeys.NOMBRE_USU)
    String usuario;
    @SerializedName(JsonKeys.PROPIEDAD_NOMINADA)
    String area;
    @SerializedName(JsonKeys.NOMBRE_VARIABLE)
    String variable;
    @SerializedName(JsonKeys.LATITUD_COO)
    String latitud;
    @SerializedName(JsonKeys.LONGITUD_COO)
    String longitud;
    @SerializedName(JsonKeys.REPERCUSIONES)
    String repercusiones;
    @SerializedName(JsonKeys.ORIGEN)
    String origen;
    @SerializedName(JsonKeys.PORCENTAJE_APA)
    int porcentaje;
    @SerializedName(JsonKeys.FRECUENCIA_APA)
    int frecuencia;
    @SerializedName(JsonKeys.FECHA_RES)
    String fecha_res;
    @SerializedName(JsonKeys.EVALUACION)
    String evaluacion;
    @SerializedName(JsonKeys.PERSONAL)
    String personal;
    @SerializedName(JsonKeys.TIEMPO)
    String tiempo;
    @SerializedName(JsonKeys.PRESUPUESTO)
    String presupuesto;
    @SerializedName(JsonKeys.RECURSOS)
    String recursos;
    @SerializedName(JsonKeys.CONOCIMIENTO)
    String conocimiento;
    @SerializedName(JsonKeys.MONITOR_RES)
    int monitor_res;
    @SerializedName(JsonKeys.PRUEBA1)
    String prueba1;
    @SerializedName(JsonKeys.PRUEBA2)
    String prueba2;
    @SerializedName(JsonKeys.PRUEBA3)
    String prueba3;
    String estado;

    //==============================================================================================
    //CONSTRUCTOR DE LA CLASE REPORTE
    public Reporte(int cod_paf, int monitor_res, String conocimiento, String recursos,
                   String variable, String latitud, String area, String fecha_mon,
                   String repercusiones, String presupuesto, String tiempo, String personal,
                   String evaluacion, String fecha_res, int frecuencia, int porcentaje,
                   String origen, String usuario, String longitud,
                   String prueba1, String prueba2, String prueba3) {
        this.cod_paf = cod_paf;
        this.monitor_res = monitor_res;
        this.conocimiento = conocimiento;
        this.recursos = recursos;
        this.variable = variable;
        this.latitud = latitud;
        this.area = area;
        this.fecha_mon = fecha_mon;
        this.repercusiones = repercusiones;
        this.presupuesto = presupuesto;
        this.tiempo = tiempo;
        this.personal = personal;
        this.evaluacion = evaluacion;
        this.fecha_res = fecha_res;
        this.frecuencia = frecuencia;
        this.porcentaje = porcentaje;
        this.origen = origen;
        this.usuario = usuario;
        this.longitud = longitud;
        this.prueba1 = prueba1;
        this.prueba2 = prueba2;
        this.prueba3 = prueba3;
        this.estado = "ONLINE";
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(MonitoreoEntry.PUNTO, cod_paf);
        values.put(MonitoreoEntry.FECHA, fecha_mon);
        values.put(MonitoreoEntry.MONITOR, usuario);
        values.put(MonitoreoEntry.PROPIEDAD, area);
        values.put(MonitoreoEntry.VARIABLE, variable);
        values.put(MonitoreoEntry.FECHA, fecha_mon);
        values.put(MonitoreoEntry.LATITUD, latitud);
        values.put(MonitoreoEntry.LONGITUD, longitud);
        values.put(MonitoreoEntry.REPERCUSIONES, repercusiones);
        values.put(MonitoreoEntry.ORIGEN, origen);
        values.put(MonitoreoEntry.PORCENTAJE, porcentaje);
        values.put(MonitoreoEntry.FRECUENCIA, frecuencia);
        values.put(MonitoreoEntry.NOMBRE, prueba1);
        values.put(MonitoreoEntry.NOMBRE2, prueba2);
        values.put(MonitoreoEntry.NOMBRE3, prueba3);
        values.put(MonitoreoEntry.ESTADO, estado);

        return values;
    }

    //==============================================================================================
    //CONSTRUCTOR VACIO
    public Reporte() {
        this.cod_paf = 0;
        this.monitor_res = 0;
        this.conocimiento = "";
        this.recursos = "";
        this.variable = "";
        this.latitud = "";
        this.area = "";
        this.fecha_mon = "";
        this.repercusiones = "";
        this.presupuesto = "";
        this.tiempo = "";
        this.personal = "";
        this.evaluacion = "";
        this.fecha_res = "";
        this.frecuencia = 0;
        this.porcentaje = 0;
        this.origen = "";
        this.usuario = "";
        this.longitud = "";
        this.prueba1 = "null";
        this.prueba2 = "null";
        this.prueba3 = "null";
        this.estado = "ONLINE";
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

    //==============================================================================================
    //METODO: GENERA INFORMACIÓN PARA LAS TRES PLANTILLAS DEL REPORTE
    public ArrayList<String> generarInfoPlantilla(int cod_plantilla) {
        ArrayList<String> info = new ArrayList<>();
        info.add(this.cod_paf + "");

        switch (cod_plantilla) {
            case 1:
                //Datos de la primera plantilla
                info.add(this.fecha_mon);
                info.add(this.usuario);
                info.add(this.area);
                info.add(this.variable);
                info.add(this.latitud);
                info.add(this.longitud);

                info.add(this.repercusiones.charAt(0) + "");
                info.add(this.repercusiones.charAt(1) + "");
                info.add(this.repercusiones.charAt(2) + "");
                info.add(this.repercusiones.charAt(3) + "");

                info.add(this.origen.charAt(0) + "");
                info.add(this.origen.charAt(1) + "");
                break;
            case 2:
                //Datos de la segunda plantilla
                info.add(this.porcentaje + "");
                info.add(this.frecuencia + "");
                break;
            case 3:
                //Datos de la tercera plantilla
                info.add(this.fecha_res);
                info.add(this.monitor_res + "");
                info.add(this.evaluacion);
                info.add(this.personal);
                info.add(this.tiempo);
                info.add(this.presupuesto);
                info.add(this.recursos);
                info.add(this.conocimiento);
        }

        return info;
    }

    //==============================================================================================
    //GETTER Y SETTER DE LOS ATRIBUTOS DE LA CLASE REPORTE
    public int getMonitor_res() {
        return monitor_res;
    }

    public void setMonitor_res(int monitor_res) {
        this.monitor_res = monitor_res;
    }

    public String getConocimiento() {
        return conocimiento;
    }

    public void setConocimiento(String conocimiento) {
        this.conocimiento = conocimiento;
    }

    public String getRecursos() {
        return recursos;
    }

    public void setRecursos(String recursos) {
        this.recursos = recursos;
    }

    public String getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(String presupuesto) {
        this.presupuesto = presupuesto;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getPersonal() {
        return personal;
    }

    public void setPersonal(String personal) {
        this.personal = personal;
    }

    public String getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(String evaluacion) {
        this.evaluacion = evaluacion;
    }

    public String getFecha_res() {
        return fecha_res;
    }

    public void setFecha_res(String fecha_res) {
        this.fecha_res = fecha_res;
    }

    public int getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(int frecuencia) {
        this.frecuencia = frecuencia;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getRepercusiones() {
        return repercusiones;
    }

    public void setRepercusiones(String repercusiones) {
        this.repercusiones = repercusiones;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFecha_mon() {
        return fecha_mon;
    }

    public void setFecha_mon(String fecha_mon) {
        this.fecha_mon = fecha_mon;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public int getCod_paf() {
        return cod_paf;
    }

    public void setCod_paf(int cod_paf) {
        this.cod_paf = cod_paf;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
