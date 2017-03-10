package com.example.edwin.ayllu;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.AnalisisPorcentajeFrecuencia;
import com.example.edwin.ayllu.domain.Usuario;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActividadEstadisticaPuntoAfactacion extends AppCompatActivity {
    private LineChart mChart;
    private ArrayList<AnalisisPorcentajeFrecuencia> datos;
    private String pa;
    private TextView fecha,puntaje;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_estadistica_punto_afactacion);

        //Parametro que llega cuando se inicia esta actividad
        Bundle bundle = getIntent().getExtras();
        pa = bundle.getString("pa");

        final ProgressDialog loading = ProgressDialog.show(this, getResources().getString(R.string.procesando),getResources().getString(R.string.esperar),false,false);

        //peticion al servidor de los datos necesarios para realizar la grafica estadistica.
        RestClient service = RestClient.retrofit.create(RestClient.class);
        final Call<ArrayList<AnalisisPorcentajeFrecuencia>> requestUser = service.proFre(pa);
        requestUser.enqueue(new Callback<ArrayList<AnalisisPorcentajeFrecuencia>>() {
            @Override
            public void onResponse(Call<ArrayList<AnalisisPorcentajeFrecuencia>> call, Response<ArrayList<AnalisisPorcentajeFrecuencia>> response) {
                setContentView(R.layout.activity_actividad_estadistica_punto_afactacion);

                mChart = (LineChart) findViewById(R.id.linechart);
                fecha = (TextView) findViewById(R.id.txtFec);
                puntaje = (TextView) findViewById(R.id.txtPun);

                loading.dismiss();
                if(response.isSuccessful()){

                    datos = response.body();
                    // add data
                    setData1(datos);

                    // get the legend (only possible after setting data)
                    Legend l = mChart.getLegend();

                    // modify the legend ...
                    // l.setPosition(LegendPosition.LEFT_OF_CHART);
                    l.setForm(Legend.LegendForm.LINE);


                    mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                        //metodo encargado de obtener los datos del punto seleccionado
                        @Override
                        public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

                            int aux = e.getXIndex();
                            if(datos.size()>=0) fecha.setText(datos.get(aux).getFecha());
                            else fecha.setText("");
                            puntaje.setText(""+e.getVal());

                        }

                        @Override
                        public void onNothingSelected() {

                        }
                    });

                }
                else {
                    Toast.makeText(
                            ActividadEstadisticaPuntoAfactacion.this,
                            getResources().getString(R.string.noSeEncontraronDatos),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AnalisisPorcentajeFrecuencia>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(
                        ActividadEstadisticaPuntoAfactacion.this,
                        getResources().getString(R.string.noSePudoConectarServidor),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }

    //metodo encargado de obtener las etiquetas de la grafica
    private ArrayList<String> valoresX(ArrayList<AnalisisPorcentajeFrecuencia> datos){
        ArrayList<String> xVals = new ArrayList<String>();
        for(int i = 0; i < datos.size(); i++){
            xVals.add(""+i);
        }
        return xVals;
    }

    //metodo encargado de obtener los datos que seran graficados
    private ArrayList<Entry> valoresY(ArrayList<AnalisisPorcentajeFrecuencia> datos){
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for(int i = 0; i < datos.size(); i++){
            int aux = (Integer.parseInt(datos.get(i).getFrecuencia()) + Integer.parseInt(datos.get(i).getProcentaje()))/2;
            yVals.add(new Entry(aux, i));
        }

        return yVals;

    }
    //metodo encargado de grafiacar en patalla los resultados
    private void setData1(ArrayList<AnalisisPorcentajeFrecuencia> datos) {
        ArrayList<String> xVals = valoresX(datos);

        ArrayList<Entry> yVals = valoresY(datos);

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, "Frecuencia y Porcentaje de Aparición");
        set1.setFillAlpha(110);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(5f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);

    }
}
