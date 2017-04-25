package com.example.edwin.ayllu;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    private String pa,fac,var;
    private ArrayList<RespuestaInstitucional> rp;
    private Boolean ban = false;
    private RadioButton eva,per,tie,pre,rec,con;
    private RadioGroup grup;
    private BarChart mChart;
    private TextView facView,varView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_grafica_respuesta_institucional);
        grup = (RadioGroup) findViewById(R.id.grup);
        eva = (RadioButton) findViewById(R.id.radio_eval);
        per = (RadioButton) findViewById(R.id.radio_per);
        tie = (RadioButton) findViewById(R.id.radio_tiempo);
        pre = (RadioButton) findViewById(R.id.radio_presu);
        rec = (RadioButton) findViewById(R.id.radio_rec);
        con = (RadioButton) findViewById(R.id.radio_con);
        mChart = (BarChart) findViewById(R.id.linechart);

        Bundle bundle = getIntent().getExtras();
        pa = bundle.getString("pa");
        fac = bundle.getString("fac");
        var = bundle.getString("var");

        facView = (TextView) findViewById(R.id.txtFac);
        varView = (TextView) findViewById(R.id.txtVar);
        facView.setText(fac);
        varView.setText(var);

        //Se hace la peticion al servidor para obtener la calificacion de las respuestas intitucionales de un punto de afectacion en especifico
        final ProgressDialog loading = ProgressDialog.show(this, getResources().getString(R.string.procesando),getResources().getString(R.string.esperar),false,false);

        RestClient service = RestClient.retrofit.create(RestClient.class);
        final Call<ArrayList<RespuestaInstitucional>> requestUser = service.getRespuestaInstitucional(pa);

        requestUser.enqueue(new Callback<ArrayList<RespuestaInstitucional>>() {
            @Override
            public void onResponse(Call<ArrayList<RespuestaInstitucional>> call, Response<ArrayList<RespuestaInstitucional>> response) {
                loading.dismiss();
                if(response.isSuccessful()){
                    Log.e("Error",response.body().size()+"");
                    rp = response.body();
                    ban = true;

                    ArrayList<BarDataSet> aux = valoresYq(evaluacion());
                    BarData data= new BarData(valoresX(), aux);
                    data.setGroupSpace(0);

                    mChart.setData(data);
                    mChart.invalidate();
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

    //metodo que grafica los resultados dependoendo de la opcion elegida por el usuario
    public void onRadioButtonClicked(View view) {
        boolean marcado = ((RadioButton) view).isChecked();

        switch (view.getId()) {

            //grafica la evaluacion
            case R.id.radio_eval:
                if (marcado) {
                    if(ban){
                        ArrayList<BarDataSet> aux = valoresYq(evaluacion());
                        BarData data= new BarData(valoresX(), aux);
                        data.setGroupSpace(0);

                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.animateX(2000);
                        mChart.animateY(2000);
                        mChart.animateXY(2000, 2000);
                    }


                }

                break;
            //grafica el personal
            case R.id.radio_per:
                if (marcado) {
                    if(ban){
                        ArrayList<BarDataSet> aux = valoresYq(personal());
                        BarData data= new BarData(valoresX(), aux);
                        data.setGroupSpace(0);

                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.animateX(2000);
                        mChart.animateY(2000);
                        mChart.animateXY(2000, 2000);
                    }


                }
                break;
            //grafica el tiempo
            case R.id.radio_tiempo:
                if (marcado) {
                    if(ban){
                        ArrayList<BarDataSet> aux = valoresYq(tiempo());
                        BarData data= new BarData(valoresX(), aux);
                        data.setGroupSpace(0);

                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.animateX(2000);
                        mChart.animateY(2000);
                        mChart.animateXY(2000, 2000);
                    }

                }
                break;
            //grafica el presupuesto
            case R.id.radio_presu:
                if (marcado) {
                    if(ban){
                        ArrayList<BarDataSet> aux = valoresYq(presupuesto());
                        BarData data= new BarData(valoresX(), aux);
                        data.setGroupSpace(0);

                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.animateX(2000);
                        mChart.animateY(2000);
                        mChart.animateXY(2000, 2000);
                    }

                }
                break;
            //grafuca los recursos
            case R.id.radio_rec:
                if (marcado) {
                    if(ban){
                        ArrayList<BarDataSet> aux = valoresYq(recursos());
                        BarData data= new BarData(valoresX(), aux);
                        data.setGroupSpace(0);

                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.animateX(2000);
                        mChart.animateY(2000);
                        mChart.animateXY(2000, 2000);
                    }

                }
                break;
            //Ggrafica el conocimiento
            case R.id.radio_con:
                if (marcado) {
                    if(ban){
                        ArrayList<BarDataSet> aux = valoresYq(conocimiento());
                        BarData data= new BarData(valoresX(), aux);
                        data.setGroupSpace(0);

                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.animateX(2000);
                        mChart.animateY(2000);
                        mChart.animateXY(2000, 2000);
                    }
                }
                break;
        }
    }

    //Metodo que obtiene los valores del eje x (Fechas de monitoreos)
    private ArrayList<String> valoresX(){
        ArrayList<String> valX = new ArrayList<String>();
        for(int i = 0; i<rp.size();i++)valX.add(rp.get(i).getFecha());
        return valX;
    }

    //Metodo que obtiene los valores del eje y dependiendo de la opcion seleccionada por el usuario
    private ArrayList<BarDataSet> valoresYq(BarDataSet aux){
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(aux);
        return dataSets;
    }

    //Metodo que obtiene los datos de la evaluaion
    private BarDataSet evaluacion(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getEvaluacion()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.evaluacion)));
        set.setColor(Color.rgb(255, 102, 102));

        return set;
    }

    //Metodo que obtiene los datos del personal
    private BarDataSet personal(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getPersonal()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.personal)));
        set.setColor(Color.rgb(102, 102, 255));

        return set;
    }

    //Metodo que obtiene los datos  del tiempo
    private BarDataSet tiempo(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getTiempo()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.tiempo)));
        set.setColor(Color.rgb(178, 102, 255));

        return set;
    }

    //Metodo que obiente los datos del presupuesto
    private BarDataSet presupuesto(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getPresupuesto()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.presupuesto)));
        set.setColor(Color.rgb(255, 178, 102));

        return set;
    }

    //Metodo que obtiene los datos de recursos
    private BarDataSet recursos(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getRecursos()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.recursos)));
        set.setColor(Color.rgb(255, 102, 255));


        return set;
    }

    //Metodos que obtiene los datos de conocimiento
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
