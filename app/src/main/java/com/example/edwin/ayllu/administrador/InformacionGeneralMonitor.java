package com.example.edwin.ayllu.administrador;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.RestClient;
import com.example.edwin.ayllu.domain.ResumenMonitor;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformacionGeneralMonitor extends AppCompatActivity {
    private TextView ide,nom,ult,mon,res;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_general_monitor);

        //se reciben los parametros enviados por la actividad anterior
        Bundle bundle = getIntent().getExtras();

        //se instacion os elementos de la vista
        ide = (TextView) findViewById(R.id.txtide);
        nom = (TextView) findViewById(R.id.txtname);
        ult = (TextView) findViewById(R.id.ultMon);
        mon = (TextView) findViewById(R.id.mon);
        res = (TextView) findViewById(R.id.resAdm);

        //se llenan los algunso elementos de la vista con los parametros recividos
        ide.setText(bundle.getString("iden"));
        nom.setText(bundle.getString("nombre")+" "+bundle.getString("apellido"));

        //se realiza la peticion al servidor para obtener la informacion faltante del monitor
        final ProgressDialog loading = ProgressDialog.show(InformacionGeneralMonitor.this,getResources().getString(R.string.procesando),getResources().getString(R.string.esperar),false,false);
        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<ResumenMonitor>> requestAdd = service.resumenMonitor(bundle.getString("iden"));
        requestAdd.enqueue(new Callback<ArrayList<ResumenMonitor>>() {
            @Override
            public void onResponse(Call<ArrayList<ResumenMonitor>> call, Response<ArrayList<ResumenMonitor>> response) {
                loading.dismiss();
                if(response.isSuccessful()){
                    ArrayList<ResumenMonitor> aux = response.body();
                    if(aux.size()>1){
                        ult.setText(aux.get(0).getFecha());
                        mon.setText(aux.get(0).getMonitoreos());
                        res.setText(aux.get(0).getRespuestas());
                    }
                    else{

                        ult.setText("");
                        mon.setText("0");
                        res.setText("0");

                    }

                }
                else{
                    Toast login = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.noSeEncontraronDatos), Toast.LENGTH_LONG);
                    login.show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ResumenMonitor>> call, Throwable t) {
                loading.dismiss();
                Toast login = Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.noSePudoConectarServidor), Toast.LENGTH_LONG);
                login.show();
            }
        });

    }
}
