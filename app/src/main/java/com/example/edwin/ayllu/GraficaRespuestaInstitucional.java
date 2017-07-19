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
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.RespuestaInstitucional;
import com.example.edwin.ayllu.io.RestClient;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    private FloatingActionButton fabDowload;
    private String save = "E";

    // Códigos de petición
    private static final int MY_WRITE_EXTERNAL_STORAGE = 123;
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
        mChart = (BarChart) findViewById(R.id.barchart);
        fabDowload = (FloatingActionButton) findViewById(R.id.fab_dowload);
        fabDowload.setEnabled(false);

        Bundle bundle = getIntent().getExtras();
        pa = bundle.getString("pa");
        fac = bundle.getString("fac");
        var = bundle.getString("var");

        facView = (TextView) findViewById(R.id.txtFac);
        varView = (TextView) findViewById(R.id.txtVar);
        facView.setText(fac);
        varView.setText(var);

        //Se hace la peticion al servidor para obtener la calificacion de las respuestas intitucionales de un punto de afectacion en especifico
        final ProgressDialog loading = ProgressDialog.show(this, getResources().getString(R.string.statistical_graph_variable_process_message_analysis),getResources().getString(R.string.statistical_graph_variable_process_message),false,false);

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
                    save = "E";
                    ArrayList<BarDataSet> aux = valoresYq(evaluacion());
                    BarData data= new BarData(valoresX(), aux);
                    data.setGroupSpace(0);

                    mChart.setData(data);
                    mChart.invalidate();
                    mChart.animateX(2000);
                    mChart.animateY(2000);
                    mChart.animateXY(2000, 2000);

                    fabDowload.setEnabled(true);
                    fabDowload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkPermission();
                        }
                    });
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
                        save = "E";
                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.animateX(2000);
                        mChart.animateY(2000);
                        mChart.animateXY(2000, 2000);

                        fabDowload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkPermission();
                            }
                        });
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
                        save = "P";
                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.animateX(2000);
                        mChart.animateY(2000);
                        mChart.animateXY(2000, 2000);

                        fabDowload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkPermission();
                            }
                        });
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
                        save = "T";
                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.animateX(2000);
                        mChart.animateY(2000);
                        mChart.animateXY(2000, 2000);

                        fabDowload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkPermission();
                            }
                        });
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
                        save = "PR";
                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.animateX(2000);
                        mChart.animateY(2000);
                        mChart.animateXY(2000, 2000);

                        fabDowload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkPermission();
                            }
                        });
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
                        save = "R";
                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.animateX(2000);
                        mChart.animateY(2000);
                        mChart.animateXY(2000, 2000);

                        fabDowload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkPermission();
                            }
                        });
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
                        save = "C";
                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.animateX(2000);
                        mChart.animateY(2000);
                        mChart.animateXY(2000, 2000);

                        fabDowload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkPermission();
                            }
                        });
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
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.graph_institutional_response_evaluation)));
        set.setColor(Color.rgb(255, 102, 102));

        return set;
    }

    //Metodo que obtiene los datos del personal
    private BarDataSet personal(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getPersonal()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.graph_institutional_response_personal)));
        set.setColor(Color.rgb(102, 102, 255));

        return set;
    }

    //Metodo que obtiene los datos  del tiempo
    private BarDataSet tiempo(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getTiempo()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.graph_institutional_response_time)));
        set.setColor(Color.rgb(178, 102, 255));

        return set;
    }

    //Metodo que obiente los datos del presupuesto
    private BarDataSet presupuesto(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getPresupuesto()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.graph_institutional_response_budget)));
        set.setColor(Color.rgb(255, 178, 102));

        return set;
    }

    //Metodo que obtiene los datos de recursos
    private BarDataSet recursos(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getRecursos()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.graph_institutional_response_means)));
        set.setColor(Color.rgb(255, 102, 255));


        return set;
    }

    //Metodos que obtiene los datos de conocimiento
    private BarDataSet conocimiento(){

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        for(int i = 0; i < rp.size(); i++){

            vals.add(new BarEntry(Integer.parseInt(rp.get(i).getConocimiento()), i));

        }
        BarDataSet set = new BarDataSet(vals, getResources().getString((R.string.graph_institutional_response_knowledge)));
        set.setColor(Color.rgb(104, 241, 175));

        return set;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void dowload(){
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());
        String grafica;
        if(save.equals("E")) grafica = getResources().getString(R.string.graph_institutional_response_file_name)+getResources().getString(R.string.graph_institutional_response_evaluation)+format;
        else if(save.equals("P")) grafica = getResources().getString(R.string.graph_institutional_response_file_name)+getResources().getString(R.string.graph_institutional_response_personal)+format;
        else if(save.equals("T")) grafica = getResources().getString(R.string.graph_institutional_response_file_name)+getResources().getString(R.string.graph_institutional_response_time)+format;
        else if(save.equals("PR")) grafica = getResources().getString(R.string.graph_institutional_response_file_name)+getResources().getString(R.string.graph_institutional_response_budget)+format;
        else if(save.equals("R")) grafica = getResources().getString(R.string.graph_institutional_response_file_name)+getResources().getString(R.string.graph_institutional_response_means)+format;
        else grafica = getResources().getString(R.string.graph_institutional_response_file_name)+getResources().getString(R.string.graph_institutional_response_knowledge)+format;

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
                Toast.makeText(this, getResources().getString(R.string.registration_message_permissions) + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
