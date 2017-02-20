package com.example.edwin.ayllu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.Area;
import com.example.edwin.ayllu.domain.Pais;
import com.example.edwin.ayllu.domain.PuntoCritico;
import com.example.edwin.ayllu.domain.Seccion;
import com.example.edwin.ayllu.domain.Subtramo;
import com.example.edwin.ayllu.domain.Tramo;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FiltrarActividad extends AppCompatActivity {

    private CheckBox pais,tramo,subtramo,seccion,area,factor,variable;
    private CharSequence[] items_paises, items_tramos, items_subtramos, items_secciones, items_areas;
    private ArrayList<String> list_paises, list_tramos, list_subtramos, list_secciones, list_areas;
    private ArrayList<Pais> pa;
    private ArrayList<Tramo> ta;
    private ArrayList<Subtramo> sb;
    private ArrayList<Seccion> sc;
    private ArrayList<Area> ar;
    int[] opciones = {0,0,0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtrar_actividad);

        pais = (CheckBox) findViewById(R.id.pais);
        tramo = (CheckBox) findViewById(R.id.tramo);
        subtramo = (CheckBox) findViewById(R.id.subtramo);
        seccion = (CheckBox) findViewById(R.id.seccion);
        area = (CheckBox) findViewById(R.id.area);
        factor = (CheckBox) findViewById(R.id.factor);
        variable = (CheckBox) findViewById(R.id.variable);

        tramo.setEnabled(false);
        subtramo.setEnabled(false);
        seccion.setEnabled(false);
        area.setEnabled(false);
        variable.setEnabled(false);

        //seleccion pais
        pais.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    RestClient service = RestClient.retrofit.create(RestClient.class);
                    Call<ArrayList<Pais>> p = service.getPais();
                    p.enqueue(new Callback<ArrayList<Pais>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Pais>> call, Response<ArrayList<Pais>> response) {
                            if(response.isSuccessful()){
                                pa = response.body();

                                items_paises = new CharSequence[pa.size()];
                                for (int i=0; i<pa.size(); i++) items_tramos[i] = pa.get(i).getNombre();
                                //createRadioListDialog(items_tramos, "Seleccione un Tramo", 1).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<Pais>> call, Throwable t) {

                        }
                    });
                    tramo.setEnabled(true);
                }
                else{
                    tramo.setChecked(false);
                    subtramo.setChecked(false);
                    seccion.setChecked(false);
                    area.setChecked(false);
                    tramo.setEnabled(false);
                    subtramo.setEnabled(false);
                    seccion.setEnabled(false);
                    area.setEnabled(false);
                }
            }
        });
        //seleccion tramo
        tramo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    subtramo.setEnabled(true);
                }
                else{
                    subtramo.setChecked(false);
                    seccion.setChecked(false);
                    area.setChecked(false);
                    subtramo.setEnabled(false);
                    seccion.setEnabled(false);
                    area.setEnabled(false);
                }
            }
        });
        //seleccion subtramo
        subtramo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    seccion.setEnabled(true);
                }
                else{
                    seccion.setChecked(false);
                    area.setChecked(false);
                    seccion.setEnabled(false);
                    area.setEnabled(false);
                }
            }
        });
        //seleccion seccion
        seccion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    area.setEnabled(true);
                }
                else{
                    area.setChecked(false);
                    area.setEnabled(false);
                }
            }
        });

        factor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    variable.setEnabled(true);
                }
                else{
                    variable.setChecked(false);
                    variable.setEnabled(false);
                }
            }
        });
    }

}
