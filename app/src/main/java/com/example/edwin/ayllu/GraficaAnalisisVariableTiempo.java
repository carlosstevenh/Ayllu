package com.example.edwin.ayllu;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
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
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.charts.BarChart;
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
    private String tramo, variable;
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



        final ProgressDialog loading = ProgressDialog.show(this, getResources().getString(R.string.procesando),getResources().getString(R.string.esperar),false,false);
        //peticion al servidor de los datos necesarios para realizar la grafica estadistica.
        RestClient service = RestClient.retrofit.create(RestClient.class);
        final Call<ArrayList<Promedio>> requestUser = service.promedios(tramo,variable);
        requestUser.enqueue(new Callback<ArrayList<Promedio>>() {
            @Override
            public void onResponse(Call<ArrayList<Promedio>> call, Response<ArrayList<Promedio>> response) {
                loading.dismiss();
                if(response.isSuccessful()){
                    if(response.body().size()>0){
                        setContentView(R.layout.activity_grafica_analisis_variable_tiempo);
                        mChart = (BarChart) findViewById(R.id.variableTiempo);
                        promedios = response.body();

                        BarData data= new BarData(valoresX(), valoresY());
                        //data.setGroupSpace(0);

                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.animateX(2000);
                        mChart.animateY(2000);
                        mChart.animateXY(2000, 2000);

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
                                getResources().getString(R.string.noSeEncontraronDatos),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                else{
                    Toast.makeText(
                            GraficaAnalisisVariableTiempo.this,
                            getResources().getString(R.string.noSeEncontraronDatos),
                            Toast.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<Promedio>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(
                        GraficaAnalisisVariableTiempo.this,
                        getResources().getString(R.string.noSePudoConectarServidor),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }

    //metodo encargado de obtener las etiquetas de la grafica
    private ArrayList<String> valoresX(){
        ArrayList<String> xVals = new ArrayList<String>();

        for(int i = 0; i < promedios.size(); i++){

            int raya = 0;
            int coma = 0;
            int cont = 0;
            int raya2 = 0;

            String s1 = "";
            String s2 = "";

            do{
                if(String.valueOf(promedios.get(i).getDatos().charAt(cont)).equals("-")) raya = cont;
                cont++;
            }while(raya == 0 && cont<=promedios.get(i).getDatos().length());

            cont = 0;

            do{
                if(String.valueOf(promedios.get(i).getDatos().charAt(cont)).equals(",")) coma = cont;
                cont++;
            }while(coma == 0 && cont<=promedios.get(i).getDatos().length());
            cont = coma;
            do{
                if(String.valueOf(promedios.get(i).getDatos().charAt(cont)).equals("-")) raya2 = cont;
                cont++;
            }while(raya2 == 0 && cont<=promedios.get(i).getDatos().length());

            for(int h = raya+1; h < coma; h++) {

                s1 += String.valueOf(promedios.get(i).getDatos().charAt(h));
            }

            for(int h = raya2+1; h < promedios.get(i).getDatos().length()-1; h++) {
                s2 += String.valueOf(promedios.get(i).getDatos().charAt(h));
            }

            if(!s1.equals("0")) xVals.add(promedios.get(i).getDatos().substring(1,5)+"-A");
            if(!s2.equals("0")){
                String annio = "";
                for(int h = coma+1; h < raya2; h++) {
                    annio += String.valueOf(promedios.get(i).getDatos().charAt(h));
                }
                //Log.i("Año 1:" ,""+annio);
                xVals.add(annio+"-B");
            }
        }
        return xVals;
    }

    //Metodo que obtiene los valores del eje y dependiendo de la opcion seleccionada por el usuario
    private BarDataSet valoresY(){
        int aux = 0;
        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < promedios.size(); i++){

            int raya = 0;
            int coma = 0;
            int cont = 0;
            int raya2 = 0;

            String s1 = "";
            String s2 = "";

            do{
                if(String.valueOf(promedios.get(i).getDatos().charAt(cont)).equals("-")) raya = cont;
                cont++;
            }while(raya == 0 && cont<=promedios.get(i).getDatos().length());

            cont = 0;

            do{
                if(String.valueOf(promedios.get(i).getDatos().charAt(cont)).equals(",")) coma = cont;
                cont++;
            }while(coma == 0 && cont<=promedios.get(i).getDatos().length());

            cont = coma;

            do{
                if(String.valueOf(promedios.get(i).getDatos().charAt(cont)).equals("-")) raya2 = cont;
                cont++;
            }while(raya2 == 0 && cont<=promedios.get(i).getDatos().length());

            for(int h = raya+1; h < coma; h++) {

                s1 += String.valueOf(promedios.get(i).getDatos().charAt(h));
            }

            for(int h = raya2+1; h < promedios.get(i).getDatos().length()-1; h++) {
                s2 += String.valueOf(promedios.get(i).getDatos().charAt(h));
            }

            if(!s1.equals("0")){
                vals.add(new BarEntry(Float.parseFloat(s1),aux));
                Log.i("Numero",""+Float.parseFloat(s1));
                aux++;
            }
            if(!s2.equals("0")){
                vals.add(new BarEntry(Float.parseFloat(s2),aux));
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
