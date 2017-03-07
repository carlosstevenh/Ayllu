package com.example.edwin.ayllu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button graficas;
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

        //se hace la peticion al servidor de la informacion general de un monitoreo
        final ProgressDialog loading = ProgressDialog.show(this, getResources().getString(R.string.procesando),getResources().getString(R.string.esperar),false,false);
        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<MonitoreoGeneral>> mon = service.informacion(pa,fm);
        mon.enqueue(new Callback<ArrayList<MonitoreoGeneral>>() {
            @Override
            public void onResponse(Call<ArrayList<MonitoreoGeneral>> call, Response<ArrayList<MonitoreoGeneral>> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
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
                else{
                    Toast.makeText(
                            InformacionPuntoCritico.this,
                            getResources().getString(R.string.noSeEncontraronDatos),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MonitoreoGeneral>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(
                        InformacionPuntoCritico.this,
                        getResources().getString(R.string.noSePudoConectarServidor),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        //se define un variable de tipo button el cual sera la encargada de manejar los eventos del boton analisis
        graficas = (Button) findViewById(R.id.btnGraficas);

        //metodo encargado de llamar a la actividad encargada de realizar la grafica estadistica
        graficas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle parametro = new Bundle();
                parametro.putString("pa",pa);
                Intent intent = new Intent(InformacionPuntoCritico.this,ActividadEstadisticaPuntoAfactacion.class);
                intent.putExtras(parametro);
                startActivity(intent);
            }
        });
    }
}
