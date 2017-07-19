package com.example.edwin.ayllu;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.MonitoreoGeneral;
import com.example.edwin.ayllu.domain.Prueba;
import com.example.edwin.ayllu.io.ApiConstants;
import com.example.edwin.ayllu.io.RestClient;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformacionPuntoCritico extends AppCompatActivity implements View.OnClickListener{
    private String pa,fm;
    private TextView fec,nom,pais,tra,sub,sec,are,fac,var,lon,lat;
    private ArrayList<MonitoreoGeneral> mg;
    FloatingActionButton fab_res, fab_graficas;

    private String URL = ApiConstants.URL_IMG;

    private MonitoringImageSwipeAdapter adapter;
    private LinearLayout dotsLayout;
    private ViewPager vpMonitoring;
    String [] imgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_punto_critico);

        Bundle bundle = getIntent().getExtras();
        pa = bundle.getString("pa");
        fm = bundle.getString("fm");
        Log.i("TAG", "punto afecation: " + pa);

        //se define los elementos de la vista
        fec = (TextView) findViewById(R.id.fec);
        nom = (TextView) findViewById(R.id.user);
        pais = (TextView) findViewById(R.id.pais);
        tra = (TextView) findViewById(R.id.tramo);
        sub = (TextView) findViewById(R.id.subtramo);
        sec = (TextView) findViewById(R.id.seccion);
        are = (TextView) findViewById(R.id.area);
        fac = (TextView) findViewById(R.id.factor);
        var = (TextView) findViewById(R.id.variable);
        lon = (TextView) findViewById(R.id.longitud);
        lat = (TextView) findViewById(R.id.latitud);

        //se define un variable de tipo button el cual sera la encargada de manejar los eventos del boton analisis
        fab_graficas = (FloatingActionButton) findViewById(R.id.fab_graficas);
        fab_res = (FloatingActionButton) findViewById(R.id.fab_resp);

        fab_graficas.setOnClickListener(this);
        fab_res.setOnClickListener(this);

        fab_graficas.setEnabled(false);
        fab_res.setEnabled(false);

        vpMonitoring = (ViewPager) findViewById(R.id.vp_monitoring);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

        //se realiza la peticion al servidor del nombre de las pruebas del monitoreo
        RestClient service1 = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<Prueba>> getPrueba = service1.getPrueba(pa,fm);
        getPrueba.enqueue(new retrofit2.Callback<ArrayList<Prueba>>() {
            @Override
            public void onResponse(Call<ArrayList<Prueba>> call, Response<ArrayList<Prueba>> response) {
                if(response.isSuccessful()){
                    Prueba prueba = response.body().get(0);

                    int size = prueba.getSize();
                    imgs = new String[size];
                    for (int i = 0; i<size; i++) imgs[i] = ApiConstants.URL_IMG + prueba.getPruebas(i+1);

                    //Cargamos las Imagenes
                    adapter = new MonitoringImageSwipeAdapter(InformacionPuntoCritico.this, imgs);
                    vpMonitoring.setAdapter(adapter);
                    vpMonitoring.addOnPageChangeListener(viewPagerPageChangeListener);

                    addBottomDots(0);
                }
                else{
                    Log.e("URL:",URL);
                    Toast.makeText(
                            InformacionPuntoCritico.this,
                            getResources().getString(R.string.statistical_graph_variable_process_message_negative),
                            Toast.LENGTH_LONG)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Prueba>> call, Throwable t) {
                Toast.makeText(
                        InformacionPuntoCritico.this,
                        getResources().getString(R.string.statistical_graph_variable_process_message_server),
                        Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        });

        //se hace la peticion al servidor de la informacion general de un monitoreo
        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<MonitoreoGeneral>> mon = service.informacion(pa,fm);
        mon.enqueue(new Callback<ArrayList<MonitoreoGeneral>>() {
            @Override
            public void onResponse(Call<ArrayList<MonitoreoGeneral>> call, Response<ArrayList<MonitoreoGeneral>> response) {
                if (response.isSuccessful()) {
                    mg = response.body();

                    MonitoreoGeneral aux = mg.get(0);
                    fec.setText("  "+aux.getFecha());
                    nom.setText("  "+aux.getMonitor());
                    pais.setText("  "+aux.getPais());
                    tra.setText("  "+aux.getTramo());
                    sub.setText("  "+aux.getSubtramo());
                    sec.setText("  "+aux.getSeccion());
                    are.setText("  "+aux.getArea());
                    fac.setText("  "+aux.getFactor());
                    var.setText("  "+aux.getVariable());
                    lon.setText(reverseCoordinates(aux.getLongitud(),"LONGITUD"));
                    lat.setText(reverseCoordinates(aux.getLatitud(),"LATITUD"));

                    fab_graficas.setEnabled(true);
                    fab_res.setEnabled(true);

                }
                else{
                    Toast.makeText(
                            InformacionPuntoCritico.this,
                            getResources().getString(R.string.statistical_graph_variable_process_message_negative),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MonitoreoGeneral>> call, Throwable t) {
                Toast.makeText(
                        InformacionPuntoCritico.this,
                        getResources().getString(R.string.statistical_graph_variable_process_message_server),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    /**
     * =============================================================================================
     * METODO: Añade los puntos al LinearLayout encargado de mostrar información de el Slide Actual
     **/
    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[imgs.length];

        dotsLayout.removeAllViews();
        //Limpiamos y recargamos todos lo views para los puntos
        for (int i = 0; i < dots.length; i++) {
            //Creamos un nuevo view para el punto con el caracter (.)
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(60);
            dots[i].setTextColor(getResources().getColor(R.color.colorPrimary));
            dotsLayout.addView(dots[i]);
        }
        //Determinamos que punto esta activo segun el layout actual
        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.colorTextIcons));
    }

    /**
     * =============================================================================================
     * METODO: Agrega el escucha al view encargado de los Slides con sus respectivos metodos
     **/
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    //Metodo obtiene que graficas necesita observar el usuario
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_graficas:
                Bundle parametro = new Bundle();
                parametro.putString("pa",pa);
                parametro.putString("fac",mg.get(0).getFactor());
                parametro.putString("var",mg.get(0).getVariable());
                Intent intent = new Intent(InformacionPuntoCritico.this,ActividadEstadisticaPuntoAfactacion.class);
                intent.putExtras(parametro);
                startActivity(intent);
                break;
            case R.id.fab_resp:
                Bundle parametro1 = new Bundle();
                parametro1.putString("pa",pa);
                parametro1.putString("fac",mg.get(0).getFactor());
                parametro1.putString("var",mg.get(0).getVariable());
                Intent intent1 = new Intent(InformacionPuntoCritico.this,GraficaRespuestaInstitucional.class);
                intent1.putExtras(parametro1);
                startActivity(intent1);

                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private String reverseCoordinates(String cad, String opc){
        String reverseCad;
        if (opc.equals("LATITUD")){
            reverseCad =    cad.charAt(0) +  "-" + cad.charAt(1) + cad.charAt(2) + "°" +
                    cad.charAt(3) + cad.charAt(4) + "'" +
                    cad.charAt(5) + cad.charAt(6) + "''";
        }
        else {
            reverseCad =    cad.charAt(0) +  "-" + cad.charAt(1) + cad.charAt(2) + cad.charAt(3) + "°" +
                    cad.charAt(4) + cad.charAt(5) + "'" +
                    cad.charAt(6) + cad.charAt(7) + "''";
        }

        return reverseCad;
    }
}
