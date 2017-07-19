package com.example.edwin.ayllu;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.Promedio;
import com.example.edwin.ayllu.io.RestClient;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraficaAnalisisVariableTiempo extends AppCompatActivity {
    private BarChart mChart;
    private ArrayList<Promedio> promedios;
    private String tramo, variable, monitor;
    private FloatingActionButton fabDowload;
    // Códigos de petición
    private static final int MY_WRITE_EXTERNAL_STORAGE = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica_analisis_variable_tiempo);

        Bundle bundle = getIntent().getExtras();
        tramo = bundle.getString("tramo");
        variable = bundle.getString("variable");

        //Obtenemos el codigo del monitor y el pais del usuario en sesión
        AdminSQLite admin = new AdminSQLite(getApplicationContext(), "login", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        //Prepara la sentencia SQL para la consulta en la Tabla de usuarios
        Cursor cursor = bd.rawQuery("SELECT codigo FROM login LIMIT 1", null);
        cursor.moveToFirst();
        monitor = cursor.getString(0);
        cursor.close();

        final ProgressDialog loading = ProgressDialog.show(this, getResources().getString(R.string.statistical_graph_variable_process_message_analysis),getResources().getString(R.string.statistical_graph_variable_process_message),false,false);
        //peticion al servidor de los datos necesarios para realizar la grafica estadistica.
        RestClient service = RestClient.retrofit.create(RestClient.class);
        final Call<ArrayList<Promedio>> requestUser = service.promedios(tramo,variable,monitor);
        requestUser.enqueue(new Callback<ArrayList<Promedio>>() {
            @Override
            public void onResponse(Call<ArrayList<Promedio>> call, Response<ArrayList<Promedio>> response) {
                loading.dismiss();
                if(response.isSuccessful()){
                    if(response.body().size()>0){
                        mChart = (BarChart) findViewById(R.id.barchart);
                        promedios = response.body();

                        BarData data= new BarData(valoresX(), valoresY());
                        //data.setGroupSpace(0);

                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.animateX(2000);
                        mChart.animateY(2000);
                        mChart.animateXY(2000, 2000);

                        Legend l = mChart.getLegend();
                        l.setEnabled(false);

                        fabDowload = (FloatingActionButton) findViewById(R.id.fab_dowload);
                        fabDowload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkPermission();
                            }
                        });

                    }
                    else{
                        Toast.makeText(
                                GraficaAnalisisVariableTiempo.this,
                                getResources().getString(R.string.statistical_graph_variable_process_message_negative),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                else{
                    Toast.makeText(
                            GraficaAnalisisVariableTiempo.this,
                            getResources().getString(R.string.statistical_graph_variable_process_message_server),
                            Toast.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<Promedio>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(
                        GraficaAnalisisVariableTiempo.this,
                        getResources().getString(R.string.statistical_graph_variable_process_message_server),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }

    //metodo encargado de obtener las etiquetas de la grafica
    private ArrayList<String> valoresX(){
        ArrayList<String> xVals = new ArrayList<String>();

        for(int i = 0; i < promedios.size(); i++){
            if(!promedios.get(i).getPromedio().equals("0.0")){
                String label = "";
                if(promedios.get(i).getSemestre().equals("1"))label = promedios.get(i).getAnio()+"-A";
                if(promedios.get(i).getSemestre().equals("2"))label = promedios.get(i).getAnio()+"-B";
                xVals.add(label);
            }
        }
        return xVals;
    }

    //Metodo que obtiene los valores del eje y dependiendo de la opcion seleccionada por el usuario
    private BarDataSet valoresY(){
        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        int aux = 0;
        for(int i = 0; i < promedios.size(); i++){

            if(!promedios.get(i).getPromedio().equals("0.0")){
                float promedio = Float.parseFloat(promedios.get(i).getPromedio());
                vals.add(new BarEntry(promedio,aux));
                aux++;
            }
        }

        BarDataSet set = new BarDataSet(vals,"");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        return set;
    }
    //Metodo que descarga la grafica estadistica
    void dowload(){
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());
        String grafica = getResources().getString(R.string.graficaVariableTramo)+format;;
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
