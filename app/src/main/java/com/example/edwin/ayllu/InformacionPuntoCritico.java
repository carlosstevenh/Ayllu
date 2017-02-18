package com.example.edwin.ayllu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.edwin.ayllu.domain.MonitoreoGeneral;
import com.example.edwin.ayllu.domain.PuntoCritico;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformacionPuntoCritico extends AppCompatActivity {
    private String pa,fm;
    private TextView paf,fec,nom,pais,tra,sub,sec,are,fac,var,lon,lat;
    private ArrayList<MonitoreoGeneral> mg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_punto_critico);

        Bundle bundle = getIntent().getExtras();
        pa = bundle.getString("pa");
        fm = bundle.getString("fm");
        Log.i("TAG", "punto afecation: " + pa);

        //paf = (TextView) findViewById(R.id.paf);
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
                    lon.setText("  "+aux.getLongitud());
                    lat.setText("  "+aux.getLatitud());

                }
                else{}
            }

            @Override
            public void onFailure(Call<ArrayList<MonitoreoGeneral>> call, Throwable t) {

            }
        });

    }
}
