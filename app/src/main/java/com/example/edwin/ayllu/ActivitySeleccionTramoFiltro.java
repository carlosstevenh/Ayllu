package com.example.edwin.ayllu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.ConteoFactoresTramo;
import com.example.edwin.ayllu.domain.PaisDbHelper;
import com.example.edwin.ayllu.domain.TramoDbHelper;
import com.example.edwin.ayllu.io.RestClient;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
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

import static com.example.edwin.ayllu.domain.PaisContract.PaisEntry;
import static com.example.edwin.ayllu.domain.TramoContract.TramoEntry;

public class ActivitySeleccionTramoFiltro extends AppCompatActivity implements View.OnClickListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener{

    private FloatingActionButton fabPais, fabTramo, fabSearch, fabDowload;
    private FloatingActionsMenu fabMenu;
    Interpolator interpolador;

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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            interpolador = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);
        }

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
        fabMenu = (FloatingActionsMenu) findViewById(R.id.menu_fab);

        fabTramo.setEnabled(false);

        fabPais.setOnClickListener(this);
        fabTramo.setOnClickListener(this);
        fabSearch.setOnClickListener(this);
        fabDowload.setOnClickListener(this);
        fabMenu.setOnFloatingActionsMenuUpdateListener(this);

        fabDowload.setScaleX(0);
        fabDowload.setScaleY(0);
        fabSearch.setScaleX(0);
        fabSearch.setScaleY(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fabMenu.collapse();
    }

    //Metodo que se encarga de obtener los porcentajes de cada factor
    private ArrayList<Float> yDatas(){
        int cantidad = 0;
        ArrayList<Float> aux = new ArrayList<>();
        for(int i = 0; i < facTram.size(); i++) cantidad += Integer.parseInt(facTram.get(i).getCantidad());
        for(int i = 0; i < facTram.size(); i++){
            float por = (Integer.parseInt(facTram.get(i).getCantidad())*100)/cantidad;
            aux.add(por);
        }

        return aux;
    }

    //Metodo que se encarga de obtener las etiquetas del diagrama de torta (los diferentes factores presentados)
    private ArrayList<String> xDatas(){
        ArrayList<String> datos = new ArrayList<>();
        for(int i = 0; i < facTram.size(); i++) datos.add(facTram.get(i).getNombre());
        return datos;
    }

    //Metodo encargado de obtener tanto los porcentajes como las etiquetas para posteriormente ser representados
    //en el diagra de torta
    private void addData() {
        ArrayList<Entry> yVals1 = new ArrayList<>();
        ArrayList<String> xValores = xDatas();
        ArrayList<Float> yValores = yDatas();
        //tableRow.add
        for (int i = 0; i < yValores.size(); i++)
            yVals1.add(new Entry(yValores.get(i), i));

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        // add many colors
        ArrayList<Integer> colors = new ArrayList<>();

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
                createRadioListDialog(items_paises, getResources().getString(R.string.general_statistical_graph_dialog_title_countries), 1).show();
                break;
            case R.id.fab_tramo:
                createRadioListDialog(items_tramos, getResources().getString(R.string.general_statistical_graph_dialog_title_tramos), 2).show();
                break;
            case R.id.fab_search:
                mChart = (PieChart) findViewById(R.id.piechart);
                mChart.clear();
                //ID del boton que realiza la peticion al servidor de los datos para porsteriormente ser graficados
                if(opTramo != -1){
                    fabMenu.collapse();
                    final ProgressDialog loading = ProgressDialog.show(this,getResources().getString(R.string.general_statistical_graph_process_message_analysis),getResources().getString(R.string.general_statistical_graph_process_message),false,false);
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

                                }
                                else{
                                    Toast.makeText(
                                            ActivitySeleccionTramoFiltro.this,
                                            getResources().getString(R.string.general_statistical_graph_process_message_negative),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                            else{
                                Toast.makeText(
                                        ActivitySeleccionTramoFiltro.this,
                                        getResources().getString(R.string.general_statistical_graph_process_message_negative),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }

                        }

                        @Override
                        public void onFailure(Call<ArrayList<ConteoFactoresTramo>> call, Throwable t) {
                            loading.dismiss();
                            Toast.makeText(
                                    ActivitySeleccionTramoFiltro.this,
                                    getResources().getString(R.string.general_statistical_graph_process_message_server),
                                    Toast.LENGTH_SHORT)
                                    .show();

                        }
                    });


                }
                else createSimpleDialog(getResources().getString(R.string.general_statistical_graph_message_info), getResources().getString(R.string.titleListMonitoringDialog)).show();
                break;
            case R.id.fab_dowload:
                fabMenu.collapse();
                if (mChart != null) checkPermission();
                else createSimpleDialog(getResources().getString(R.string.general_statistical_graph_message_download), getResources().getString(R.string.titleListMonitoringDialogError)).show();
                break;
        }
    }

    /**
     * =============================================================================================
     * METODO: Presenta en Interfaz un mensaje Tipo Dialog
     **/
    public AlertDialog createSimpleDialog(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_monitoring_info, null);
        builder.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title_dialog);
        TextView tvDescription = (TextView) v.findViewById(R.id.tv_description_dialog);

        tvTitle.setText(titulo);
        tvDescription.setText(mensaje);

        builder.setPositiveButton(getResources().getString(R.string.opcionListMonitoringDialog),
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
                .setNegativeButton(getResources().getString(R.string.general_statistical_graph_dialog_option_ok),
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
        String grafica = getResources().getString(R.string.general_statistical_graph_file_name)+format;
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Ayllu/Graficos");
        imagesFolder.mkdirs();
        mChart.saveToPath(grafica,"/Ayllu/Graficos");

        Toast.makeText(this, getResources().getString(R.string.general_statistical_graph_alert_dowload) , Toast.LENGTH_LONG).show();
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
                Toast.makeText(this, getResources().getString(R.string.general_statistical_graph_message_permissions) + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onMenuExpanded() {
        animationButton(fabSearch, 1, 1);
        animationButton(fabDowload, 1, 1);
        fabSearch.setEnabled(true);
        fabDowload.setEnabled(true);
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onMenuCollapsed() {
        fabSearch.setEnabled(false);
        fabDowload.setEnabled(false);

        animationButton(fabSearch, 0, 0);
        animationButton(fabDowload, 0, 0);
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    public void animationButton(FloatingActionButton fb, float x, float y) {
        fb.animate()
                .scaleX(x)
                .scaleY(y)
                .setInterpolator(interpolador)
                .setDuration(10)
                .setStartDelay(100);
    }
}
