package com.example.edwin.ayllu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.ConteoVariableFactorTramo;
import com.example.edwin.ayllu.io.RestClient;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraficaTortaVariables extends AppCompatActivity {
    private ArrayList<ConteoVariableFactorTramo> VarTram;
    private PieChart mChart;
    private FloatingActionButton fabDowload;
    private String tramo;

    // Códigos de petición
    private static final int MY_WRITE_EXTERNAL_STORAGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica_torta_variables);
        mChart = (PieChart) findViewById(R.id.piechart);
        fabDowload = (FloatingActionButton) findViewById(R.id.fab_dowload);
        fabDowload.setEnabled(false);

        Bundle bundle = getIntent().getExtras();
        tramo = bundle.getString("tramo");
        String factor = bundle.getString("factor");

        final ProgressDialog loading = ProgressDialog.show(this,getResources().getString(R.string.statistical_graph_variable_process_message_analysis),getResources().getString(R.string.statistical_graph_variable_process_message),false,false);
        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<ConteoVariableFactorTramo>> requestUser = service.conteoVariableFactortramo(tramo,factor);
        requestUser.enqueue(new Callback<ArrayList<ConteoVariableFactorTramo>>() {
            @Override
            public void onResponse(Call<ArrayList<ConteoVariableFactorTramo>> call, Response<ArrayList<ConteoVariableFactorTramo>> response) {
                loading.dismiss();
                if(response.isSuccessful() && response.body().size()>0){
                    VarTram = response.body();

                    //setContentView(R.layout.activity_grafica_torta_variables);
                    //mChart = (PieChart) findViewById(R.id.piechart);
                    //fabDowload = (FloatingActionButton) findViewById(R.id.fab_dowload);
                    //LinearLayout chart = (LinearLayout)findViewById(R.id.layout1);

                    mChart.setUsePercentValues(true);
                    mChart.setDrawHoleEnabled(true);
                    mChart.setHoleColorTransparent(true);
                    mChart.setHoleRadius(7);
                    mChart.setTransparentCircleRadius(10);

                    mChart.setRotationAngle(0);
                    mChart.setRotationEnabled(true);

                    mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

                        //Metodo que se encarga de obtener el elemento que fue clickeado en la grafica estadistica
                        @Override
                        public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                            // display msg when value selected
                            if (e == null)
                                return;
                            Bundle parametro = new Bundle();
                            parametro.putString("tramo",tramo);
                            parametro.putString("variable",VarTram.get(e.getXIndex()).getCodigo());
                            //parametro.putString("factor",facTram.get(e.getXIndex()).getCodigo());
                            Intent intent = new Intent(GraficaTortaVariables.this,GraficaAnalisisVariableTiempo.class);
                            intent.putExtras(parametro);
                            startActivity(intent);

                        }

                        @Override
                        public void onNothingSelected() {

                        }
                    });

                    // add data
                    addData();

                    Legend l = mChart.getLegend();
                    l.setEnabled(false);
                    l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
                    l.setXEntrySpace(7);
                    l.setYEntrySpace(5);

                    fabDowload.setEnabled(true);
                    fabDowload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkPermission();
                        }
                    });
                }
                else{
                    Toast.makeText(
                            GraficaTortaVariables.this,
                            getResources().getString(R.string.statistical_graph_variable_process_message_negative),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ConteoVariableFactorTramo>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(
                        GraficaTortaVariables.this,
                        getResources().getString(R.string.statistical_graph_variable_process_message_server),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    //Metodo que se encarga de obtener los porcentajes de cada factor
    private ArrayList<Float> yDatas(){
        int cantidad = 0;
        ArrayList<Float> aux = new ArrayList<Float>();
        for(int i = 0; i < VarTram.size(); i++) cantidad += Integer.parseInt(VarTram.get(i).getCantidad());
        for(int i = 0; i < VarTram.size(); i++){
            float por = (Integer.parseInt(VarTram.get(i).getCantidad())*100)/cantidad;
            aux.add(por);
        }

        return aux;
    }

    //Metodo que se encarga de obtener las etiquetas del diagrama de torta (los diferentes factores presentados)
    private ArrayList<String> xDatas(){
        ArrayList<String> datos = new ArrayList<String>();
        for(int i = 0; i < VarTram.size(); i++) datos.add(VarTram.get(i).getNombre());
        return datos;
    }

    //Metodo encargado de obtener tanto los porcentajes como las etiquetas para posteriormente ser representados
    //en el diagra de torta
    private void addData() {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xValores = xDatas();
        ArrayList<Float> yValores = yDatas();

        for (int i = 0; i < yValores.size(); i++)
            yVals1.add(new Entry(yValores.get(i), i));

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(xValores, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        // update pie chart
        mChart.invalidate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void dowload(){
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());
        String grafica = getResources().getString(R.string.statistical_graph_variable_file_name)+format;
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Ayllu/Graficos");
        imagesFolder.mkdirs();
        mChart.saveToPath(grafica,"/Ayllu/Graficos");

        Toast.makeText(this, getResources().getString(R.string.statistical_graph_variable_alert_dowload) , Toast.LENGTH_LONG).show();
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
                Toast.makeText(this, getResources().getString(R.string.statistical_graph_variable_message_permissions) + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
