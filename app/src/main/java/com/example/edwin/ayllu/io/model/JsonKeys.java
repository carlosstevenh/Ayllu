package com.example.edwin.ayllu.io.model;

public class JsonKeys {
    //==============================================================================================
    //CONSTANTES PARA LA CONSULTA DE ZONAS (PAISES,TRAMOS,SUBTRAMOS,SECCIONES,AREAS)
    //----------------------------------------------------------------------------------------------
    //Llave general para la consulta de zonas
    public static final String ZONAS_RESULTS = "ZONAS";
    //----------------------------------------------------------------------------------------------
    //Llaves para paises
    public static final String PAIS_ARRAY = "Paises";
    public static final String CODIGO_PAIS = "codigo_pais";
    public static final String NOMBRE_PAIS = "nombre_pais";
    //----------------------------------------------------------------------------------------------
    //Llaves para Tramos
    public static final String TRAMOS_ARRAY = "Tramos";
    public static final String CODIGO_TRAMO = "codigo_tramo";
    public static final String DESCRIPCION_TRAMO = "descripcion_tramo";
    public static final String PAIS = "pais";
    //----------------------------------------------------------------------------------------------
    //Llaves para Subtramos
    public static final String SUBTRAMOS_ARRAY = "Subtramos";
    public static final String CODIGO_SUBTRAMO = "codigo_subtramo";
    public static final String DESCRIPCION_SUBTRAMO = "descripcion_subtramo";
    public static final String TRAMO = "tramo";
    //----------------------------------------------------------------------------------------------
    //Llaves para Secciones
    public static final String SECCIONES_ARRAY = "Secciones";
    public static final String CODIGO_SECCION = "codigo_seccion";
    public static final String DESCRIPCION_SECCION = "descripcion_seccion";
    public static final String SUBTRAMO = "subtramo";
    //----------------------------------------------------------------------------------------------
    //Llaves para Areas
    public static final String AREAS_ARRAY = "Areas";
    public static final String CODIGO_AREA = "codigo_area";
    public static final String TIPO_AREA = "tipo_area";
    public static final String AREA_ZONA_AMORTIGUAMIENTO ="area_zona_amortiguamiento";
    public static final String PROPIEDAD_AREA_NOMINADA = "propiedad_area_nominada";
    public static final String PROPIEDAD_NOMINADA = "propiedad_nominada";
    public static final String ANCHO_PROM_CAMINO = "ancho_prom_camino";
    public static final String LONGITUD_CAMINO = "longitud_camino";
    public static final String SECCION = "seccion";

    //==============================================================================================
    //CONSTANTES PARA LA CONSULTA DE CATEGORIAS (FACTORES Y VARIABLES)
    //----------------------------------------------------------------------------------------------
    //Llave general para la consulta de categorias
    public static final String CATEGORIAS_RESULTS = "CATEGORIAS";
    //----------------------------------------------------------------------------------------------
    //Llaves para factores
    public static final String FACTORES_ARRAY = "Factores";
    public static final String CODIGO_FACTOR = "codigo_fac";
    public static final String NOMBRE_FACTOR = "nombre_fac";
    //----------------------------------------------------------------------------------------------
    //Llaves para variables
    public static final String VARIABLES_ARRAY = "Variables";
    public static final String CODIGO_VARIABLE = "codigo_var";
    public static final String NOMBRE_VARIABLE = "nombre_var";
    public static final String FACTOR = "factor";

    //==============================================================================================
    //CONSTANTES PARA EL REPORTE
    public static final String REPORTE_RESULTS = "REPORTE";
    public static final String REPORTE_ARRAY = "Monitoreos";

    public static final String PUNTO_AFECTACION = "punto_afectacion";
    public static final String VARIABLE = "nombre_var";
    public static final String AREA = "area";
    public static final String LATITUD_COO = "latitud_coo";
    public static final String LONGITUD_COO = "longitud_coo";
    public static final String FECHA_MON = "fecha_mon";
    public static final String NOMBRE_USU = "nombre_usu";
    public static final String REPERCUSIONES = "repercusiones_mon";
    public static final String ORIGEN = "origen_mon";
    public static final String PORCENTAJE_APA = "porcentaje_aparicion_mon";
    public static final String FRECUENCIA_APA = "frecuencia_aparicion_mon";
    public static final String FECHA_RES = "fecha_res";
    public static final String EVALUACION = "evaluacion_res";
    public static final String PERSONAL = "personal_res";
    public static final String TIEMPO = "tiempo_res";
    public static final String PRESUPUESTO = "presupuesto_res";
    public static final String RECURSOS = "recursos_res";
    public static final String CONOCIMIENTO = "conocimiento_res";
    public static final String MONITOR_RES = "monitor_res";
    public static final String PRUEBA1 = "nombre_pru1";
    public static final String PRUEBA2 = "nombre_pru2";
    public static final String PRUEBA3 = "nombre_pru3";
}
