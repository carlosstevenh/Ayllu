package com.example.edwin.ayllu.Adiminstrador;

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

        Bundle bundle = getIntent().getExtras();

        ide = (TextView) findViewById(R.id.txtide);
        nom = (TextView) findViewById(R.id.txtname);
        ult = (TextView) findViewById(R.id.ultMon);
        mon = (TextView) findViewById(R.id.mon);
        res = (TextView) findViewById(R.id.resAdm);

        ide.setText(bundle.getString("iden"));
        nom.setText(bundle.getString("nombre")+" "+bundle.getString("apellido"));

        final ProgressDialog loading = ProgressDialog.show(InformacionGeneralMonitor.this,"Recuperando datos","Por favor espere...",false,false);
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
                            "Ha ocurrido un error", Toast.LENGTH_LONG);
                    login.show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ResumenMonitor>> call, Throwable t) {
                loading.dismiss();
                Toast login = Toast.makeText(getApplicationContext(),
                        "Ha ocurrido un error", Toast.LENGTH_LONG);
                login.show();
            }
        });

    }
}
