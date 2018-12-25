package com.qhapaq.nan.ayllu.io;

import android.util.Log;

import java.util.HashMap;

public class ApiConstants {
    //IPs Olds
    public static final String URL_API_AYLLU = "http://192.168.1.7/api.ayllu.com/";
    public static final String URL_IMG = "http://192.168.1.7/camara/imagenes/";

    public static final String URL_WEBSERVICE = "http://192.168.1.7/webservice/";
    public static final String URL_CAMERA = "http://192.168.1.7/camara/";

    //IPs Paises
    public static final String IP_ARGENTINA = "http://192.168.1.7/";
    public static final String IP_BOLIVIA = "http://192.168.1.7/";
    public static final String IP_CHILE = "http://192.168.1.7/";
    public static final String IP_COLOMBIA = "http://192.168.1.7/";
    public static final String IP_ECUADOR = "http://192.168.1.7/";
    public static final String IP_PERU = "http://192.168.1.7/";

    //URLs Servicios
    public static final String API_AYLLU = "api.ayllu.com/";
    public static final String WEBSERVICE = "webservice/";
    public static final String IMG = "camara/imagenes/";
    public static final String CAMERA = "camara/";

    public HashMap<String, String> urls = new HashMap<>();
    public HashMap<String, String> services = new HashMap<>();

    public ApiConstants() {
        this.urls.put("01",IP_ARGENTINA);
        this.urls.put("02",IP_BOLIVIA);
        this.urls.put("03",IP_CHILE);
        this.urls.put("04",IP_COLOMBIA);
        this.urls.put("05",IP_ECUADOR);
        this.urls.put("06",IP_PERU);

        this.services.put("API",API_AYLLU);
        this.services.put("WEBSERVICE",WEBSERVICE);
        this.services.put("IMG",IMG);
        this.services.put("CAMERA",CAMERA);
    }

    /**
     * Construye una URL dinamica dependiendo del pais y el servicio a solicitar
     * @param country : Codigo del pais a solicitar
     * @param service : Servicio a solicitar
     * @return
     */
    public String buildUrl(String country, String service) {
        String url = this.urls.get(country)+this.services.get(service);
        Log.i("URL-DI",url+"-"+country);
        return url;
    }
}




