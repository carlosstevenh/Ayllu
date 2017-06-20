package com.example.edwin.ayllu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.ConteoFactoresTramo;
import com.example.edwin.ayllu.domain.PaisDbHelper;
import com.example.edwin.ayllu.domain.TramoDbHelper;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.AEADBadTagException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.edwin.ayllu.domain.PaisContract.PaisEntry;
import static com.example.edwin.ayllu.domain.TramoContract.TramoEntry;

public class ActivitySeleccionTramoFiltro extends AppCompatActivity implements View.OnClickListener{

    private FloatingActionButton fabPais, fabTramo, fabSearch, fabDowload;

    // Códigos de petición
    private static final int MY_WRITE_EXTERNAL_STORAGE = 123;

    //VARIABLES DATOS TEMPORALES
    CharSequence[] items_paises, items_tramos;

    //VARIABLES CONTROL DE DATOS FIJOS
    PaisDbHelper paisDbHelper;
    TramoDbHelper tramoDbHelper;
    int[] pos = {-1, -1};
    int opTramo = -1;
    Cursor cursor;
    String item = "";
    int i = 0;

    //VARIABLE DE DATOS PARA GRAFICAR
    private ArrayList<ConteoFactoresTramo> facTram;
    private PieChart mChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_tramo_filtro);


        paisDbHelper = new PaisDbHelper(this);
        tramoDbHelper = new TramoDbHelper(this);

        //------------------------------------------------------------------------------------------
        //Obtenemos los tramos correspondientes al pais del usuario actual
        cursor = paisDbHelper.generateQuery("SELECT * FROM ");
        if (cursor.moveToFirst()) {
            items_paises = new CharSequence[cursor.getCount()];
            do {
                items_paises[i] = cursor.getString(2);
                i++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        fabPais = (FloatingActionButton) findViewById(R.id.fab_pais);
        fabTramo = (FloatingActionButton) findViewById(R.id.fab_tramo);
        fabSearch = (FloatingActionButton) findViewById(R.id.fab_search);
        fabDowload = (FloatingActionButton) findViewById(R.id.fab_dowload);

        fabTramo.setEnabled(false);
        fabDowload.setEnabled(false);

        fabPais.setOnClickListener(this);
        fabTramo.setOnClickListener(this);
        fabSearch.setOnClickListener(this);
    }

    //Metodo que se encarga de obtener los porcentajes de cada factor
    private ArrayList<Float> yDatas(){
        int cantidad = 0;
        ArrayList<Float> aux = new ArrayList<Float>();
        for(int i = 0; i < facTram.size(); i++) cantidad += Integer.parseInt(facTram.get(i).getCantidad());
        for(int i = 0; i < facTram.size(); i++){
            float por = (Integer.parseInt(facTram.get(i).getCantidad())*100)/cantidad;
            aux.add(por);
        }

        return aux;
    }

    //Metodo que se encarga de obtener las etiquetas del diagrama de torta (los diferentes factores presentados)
    private ArrayList<String> xDatas(){
        ArrayList<String> datos = new ArrayList<String>();
        for(int i = 0; i < facTram.size(); i++) datos.add(facTram.get(i).getNombre());
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

    //Metodo donde se programan los cliks de los botones
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_pais:
                Log.e("Boton pais:", "Esta entrando");
                createRadioListDialog(items_paises, "PAISES", 1).show();
                break;
            case R.id.fab_tramo:
                Log.e("Boton tramo:", "Esta entrando");
                createRadioListDialog(items_tramos, "TRAMOS", 2).show();
                break;
            case R.id.fab_search:
                //ID del boton que realiza la peticion al servidor de los datos para porsteriormente ser graficados
                if(opTramo != -1){
                    final ProgressDialog loading = ProgressDialog.show(this,getResources().getString(R.string.presupuesto),getResources().getString(R.string.esperar),false,false);
                    RestClient service = RestClient.retrofit.create(RestClient.class);
                    Call<ArrayList<ConteoFactoresTramo>> requestUser = service.conteoFactorTramo(""+opTramo);
                    requestUser.enqueue(new Callback<ArrayList<ConteoFactoresTramo>>() {
                        @Override
                        public void onResponse(Call<ArrayList<ConteoFactoresTramo>> call, Response<ArrayList<ConteoFactoresTramo>> response) {
                            loading.dismiss();
                            if(response.isSuccessful()){
                                facTram = response.body();
                                if(facTram.size()>0){

                                    mChart = (PieChart) findViewById(R.id.piechart);

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
                                            parametro.putString("tramo",""+opTramo);
                                            parametro.putString("factor",facTram.get(e.getXIndex()).getCodigo());
                                            Intent intent = new Intent(ActivitySeleccionTramoFiltro.this,GraficaTortaVariables.class);
                                            intent.putExtras(parametro);
                                            startActivity(intent);
                                            //Toast.makeText(ActivitySeleccionTramoFiltro.this,
                                            //        facTram.get(e.getXIndex()).getNombre(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onNothingSelected() {

                                        }
                                    });

                                    // add data
                                    addData();

                                    Legend l = mChart.getLegend();
                                    l.setEnabled(false);
                                    l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
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
                                            ActivitySeleccionTramoFiltro.this,
                                            getResources().getString(R.string.noSeEncontraronDatos),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                            else{
                                Toast.makeText(
                                        ActivitySeleccionTramoFiltro.this,
                                        getResources().getString(R.string.noSeEncontraronDatos),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }

                        }

                        @Override
                        public void onFailure(Call<ArrayList<ConteoFactoresTramo>> call, Throwable t) {
                            loading.dismiss();
                            Toast.makeText(
                                    ActivitySeleccionTramoFiltro.this,
                                    getResources().getString(R.string.noSePudoConectarServidor),
                                    Toast.LENGTH_SHORT)
                                    .show();

                        }
                    });


                }

                break;
        }
    }

    /**
     * =============================================================================================
     * METODO: Presenta en Interfaz un mensaje tipo dialog con los datos correspondientes a las
     * Paises y asi aplicar los filtros correspondientes para realizar los graficos estadisticos generales
     **/
    public AlertDialog createRadioListDialog(final CharSequence[] items, final String title, final int zn) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setSingleChoiceItems(items, pos[zn-1], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (zn){
                            case 1:
                                item = items_paises[which].toString();
                                cursor = paisDbHelper.generateConditionalQuery(new String[]{item}, PaisEntry.NOMBRE);
                                cursor.moveToFirst();
                                String codigo = cursor.getString(1);
                                pos[0] = which;
                                pos[1] = -1;
                                opTramo = -1;
                                cursor.close();

                                cursor = tramoDbHelper.generateConditionalQuery(new String[]{codigo}, TramoEntry.PAIS);
                                items_tramos = dataFilter(cursor, 2);
                                cursor.close();

                                fabTramo.setEnabled(true);
                                break;
                            case 2:
                                item = items_tramos[which].toString();
                                cursor = tramoDbHelper.generateConditionalQuery(new String[]{item}, TramoEntry.DESCRIPCION);
                                cursor.moveToFirst();
                                opTramo = cursor.getInt(1);
                                pos[1] = which;
                                cursor.close();
                                break;
                        }
                    }
                })
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        });
        return builder.create();
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    public CharSequence[] dataFilter(Cursor cur, int position) {
        i = 0;
        CharSequence[] items = new CharSequence[0];

        if (cur.moveToFirst()) {
            items = new CharSequence[cursor.getCount()];
            do {
                items[i] = cursor.getString(position);
                i++;
            } while (cursor.moveToNext());
        }
        return items;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void dowload(){
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());
        String grafica = getResources().getString(R.string.graficaPorTramo)+format + ".jpg";
        mChart.saveToGallery(grafica,100);

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
