package com.example.edwin.ayllu;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.edwin.ayllu.domain.RespuestaInstitucional;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraficaRespuestaInstitucional extends AppCompatActivity {
    String pa;
    ArrayList<RespuestaInstitucional> rp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica_respuesta_institucional);

        Bundle bundle = getIntent().getExtras();
        pa = bundle.getString("pa");
        final ProgressDialog loading = ProgressDialog.show(this, getResources().getString(R.string.procesando),getResources().getString(R.string.esperar),false,false);

        RestClient service = RestClient.retrofit.create(RestClient.class);
        final Call<ArrayList<RespuestaInstitucional>> requestUser = service.getRespuestaInstitucional(pa);

        requestUser.enqueue(new Callback<ArrayList<RespuestaInstitucional>>() {
            @Override
            public void onResponse(Call<ArrayList<RespuestaInstitucional>> call, Response<ArrayList<RespuestaInstitucional>> response) {
                loading.dismiss();
                if(response.isSuccessful()){
                    setContentView(R.layout.activity_grafica_respuesta_institucional);
                    BarChart mChart = (BarChart) findViewById(R.id.linechart);
                    Log.e("Error",response.body().size()+"");
                    rp = response.body();
                    graficar(mChart);

                    mChart.animateX(2000);
                    mChart.animateY(2000);
                    mChart.animateXY(2000, 2000);
                }
                else Log.e("Error","encontro pero nada");
            }

            @Override
            public void onFailure(Call<ArrayList<RespuestaInstitucional>> call, Throwable t) {
                loading.dismiss();
                Log.e("Error","nada");
            }
        });

    }

    private void graficar(BarChart mc){
        BarData data= new BarData(valoresX(), valoresY());
        //BarData data = new BarData(valoresX(), valoresY());
        data.setGroupSpace(0);

        mc.setData(data);
        mc.invalidate();
    }
    private ArrayList<String> valoresX(){
        ArrayList<String> valX = new ArrayList<String>();
        for(int i = 0; i<rp.size();i++)valX.add(rp.get(i).getFecha());
        return valX;
    }

    private ArrayList<BarDataSet> valoresY(){
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(evaluacion());
        dataSets.add(personal());
        dataSets.add(tiempo());
        dataSets.add(presupuesto());
        dataSets.add(recursos());
        dataSets.add(conocimiento());
        return dataSets;
    }

    private BarDataSet evaluacion(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getEvaluacion()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.evaluacion)));
        set.setColor(Color.rgb(255, 102, 102));

        return set;
    }

    private BarDataSet personal(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getPersonal()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.personal)));
        set.setColor(Color.rgb(102, 102, 255));

        return set;
    }

    private BarDataSet tiempo(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getTiempo()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.tiempo)));
        set.setColor(Color.rgb(178, 102, 255));

        return set;
    }

    private BarDataSet presupuesto(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getPresupuesto()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.presupuesto)));
        set.setColor(Color.rgb(255, 178, 102));

        return set;
    }

    private BarDataSet recursos(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getRecursos()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.recursos)));
        set.setColor(Color.rgb(255, 102, 255));


        return set;
    }

    private BarDataSet conocimiento(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getConocimiento()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.conocimiento)));
        set.setColor(Color.rgb(104, 241, 175));

        return set;
    }
}
