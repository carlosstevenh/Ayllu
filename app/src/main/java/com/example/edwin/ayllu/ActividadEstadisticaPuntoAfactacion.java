package com.example.edwin.ayllu;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.AnalisisPorcentajeFrecuencia;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActividadEstadisticaPuntoAfactacion extends AppCompatActivity {
    private LineChart mChart;
    private ArrayList<AnalisisPorcentajeFrecuencia> datos;
    private String pa,fac,var;
    private TextView fecha,puntaje,fact,vari,por,fre;
    private FloatingActionButton fabDowload;

    // Códigos de petición
    private static final int MY_WRITE_EXTERNAL_STORAGE = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_estadistica_punto_afactacion);

        //Parametro que llega cuando se inicia esta actividad
        Bundle bundle = getIntent().getExtras();
        pa = bundle.getString("pa");
        fac = bundle.getString("fac");
        var = bundle.getString("var");


        final ProgressDialog loading = ProgressDialog.show(this, getResources().getString(R.string.procesando),getResources().getString(R.string.esperar),false,false);

        //peticion al servidor de los datos necesarios para realizar la grafica estadistica.
        RestClient service = RestClient.retrofit.create(RestClient.class);
        final Call<ArrayList<AnalisisPorcentajeFrecuencia>> requestUser = service.proFre(pa);
        requestUser.enqueue(new Callback<ArrayList<AnalisisPorcentajeFrecuencia>>() {
            @Override
            public void onResponse(Call<ArrayList<AnalisisPorcentajeFrecuencia>> call, Response<ArrayList<AnalisisPorcentajeFrecuencia>> response) {
                setContentView(R.layout.activity_actividad_estadistica_punto_afactacion);

                mChart = (LineChart) findViewById(R.id.linechart);
                mChart.setDoubleTapToZoomEnabled(false);
                mChart.setPinchZoom(false);
                mChart.setScaleEnabled(false);

                mChart.animateX(2000);
                mChart.animateY(2000);
                mChart.animateXY(2000, 2000);
                
                fecha = (TextView) findViewById(R.id.txtFec);
                puntaje = (TextView) findViewById(R.id.txtPun);
                fact = (TextView) findViewById(R.id.txtFac);
                vari = (TextView) findViewById(R.id.txtVar);
                por = (TextView) findViewById(R.id.txtPorApa);
                fre = (TextView) findViewById(R.id.txtFreApa);
                fact.setText(fac);
                vari.setText(var);
                fabDowload = (FloatingActionButton) findViewById(R.id.fab_dowload);

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
                            if(datos.size()>=0) {
                                fecha.setText(datos.get(aux).getFecha());
                                por.setText(datos.get(aux).getProcentaje());
                                fre.setText(datos.get(aux).getFrecuencia());
                            }
                            else {
                                fecha.setText("");
                                por.setText("");
                                fre.setText("");
                            }
                            puntaje.setText(""+e.getVal());

                        }

                        @Override
                        public void onNothingSelected() {

                        }
                    });
                    fabDowload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkPermission();
                        }
                    });

                    YAxis leftAxis = mChart.getAxisLeft();

                    LimitLine ll = new LimitLine(140f, "Critical Blood Pressure");
                    ll.setLineColor(Color.RED);
                    ll.setLineWidth(4f);
                    ll.setTextColor(Color.BLACK);
                    ll.setTextSize(12f);
// .. and more styling options

                    leftAxis.addLimitLine(ll);


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
        for(int i = 1; i <= datos.size(); i++){
            xVals.add(""+i);
        }
        return xVals;
    }

    //metodo encargado de obtener los datos que seran graficados
    private ArrayList<Entry> valoresY(ArrayList<AnalisisPorcentajeFrecuencia> datos){
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for(int i = 0; i < datos.size(); i++){
            float aux = (float) (((float)Integer.parseInt(datos.get(i).getFrecuencia()) + (float)Integer.parseInt(datos.get(i).getProcentaje()))/2.0);
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
        set1 = new LineDataSet(yVals, getResources().getString(R.string.freYpor));
        set1.setFillAlpha(110);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(3f);

        set1.setColor(getResources().getColor(R.color.colorPrimaryDark));
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        //set1.setDrawFilled(true);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void dowload(){
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());
        String grafica = getResources().getString(R.string.graficaPorcentajeFrecuencia)+format;
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Ayllu/Graficos");
        imagesFolder.mkdirs();
        mChart.saveToPath(grafica,"/Ayllu/Graficos");

        Toast.makeText(this, getResources().getString(R.string.descargaGrafica) , Toast.LENGTH_LONG).show();
    }
    /**
     * =============================================================================================
     * METODO: CHEQUEA LOS PERMISOS DEL DISPOSITIVO
     **/
    private void checkPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) dowload();
        else {
            int hasWriteContactsPermission = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_WRITE_EXTERNAL_STORAGE);
            }
            else dowload();
        }
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(MY_WRITE_EXTERNAL_STORAGE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) dowload();
            else {
                Toast.makeText(this, "Permissions are not granted ! :-( " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
