package com.example.edwin.ayllu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.Factor;
import com.example.edwin.ayllu.domain.Task;
import com.example.edwin.ayllu.domain.Variable;
import com.example.edwin.ayllu.io.ApiAylluService;
import com.example.edwin.ayllu.io.ApiConstants;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MonitoringRegistrationForm2Activity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Factor> factores = new ArrayList<>();
    ArrayList<Variable> variables = new ArrayList<>();
    int[] opciones = {0,0};
    String codArea = "";
    String monitor = "";

    CharSequence[] items_factores, items_variables;
    ArrayList<String> list_factores, list_variables;

    Button btn_factores, btn_variables;
    EditText et_longitud, et_latitud;
    FloatingActionButton fab_reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        factores.add( new Factor("01","edificios y desarrollo"));
        factores.add( new Factor("02","infraestructura de transportes"));
        factores.add( new Factor("03","infraestructura de servicos"));
        factores.add( new Factor("04","contaminacion"));
        factores.add( new Factor("05","utilizacion/modificacion recursos biologicos"));
        factores.add( new Factor("06","extraccion de recursos naturales"));
        factores.add( new Factor("07","condiciones locales que afectan la estructura fisica"));
        factores.add( new Factor("08","usos sociales/culturales del patrimonio"));
        factores.add( new Factor("09","otras actividades humanas"));
        factores.add( new Factor("10","cambio climatico y fenomenos metereologicos externos"));
        factores.add( new Factor("11","fenomenos ecologicos o geologicos repentinos"));
        factores.add( new Factor("12","especies invasoras o superabundantes"));
        factores.add( new Factor("13"," factores de gestion e institucional"));
        factores.add( new Factor("14","otros factores"));

        variables.add(new Variable("11","vivienda","01"));
        variables.add(new Variable("12","desarrollo comercial","01"));
        variables.add(new Variable("13","zonas industriales","01"));
        variables.add(new Variable("14","infraestructuras  hoteleras y conexas","01"));
        variables.add(new Variable("15","servicios de informacion visitantes","01"));

        variables.add(new Variable("201","infraestructura transporte terrestre","02"));
        variables.add(new Variable("202","infraestructura transporte aereo","02"));

        variables.add(new Variable("301","infraestructura recursos hidricos","03"));
        variables.add(new Variable("302","instalacion de energia renovable","03"));
        variables.add(new Variable("303","instalacion de energia no renovable","03"));
        variables.add(new Variable("304","servicios localizados","03"));
        variables.add(new Variable("305","principales redes transporte de energia","03"));

        variables.add(new Variable("401","contaminacion de aguas marinas","04"));
        variables.add(new Variable("402","contaminacion de aguas subterraneas","04"));
        variables.add(new Variable("403","contaminacion de aguas superficiales","04"));
        variables.add(new Variable("404","contaminacion del aire","04"));
        variables.add(new Variable("405","residuos solidos","04"));
        variables.add(new Variable("406","aportes de energia en exceso","04"));

        variables.add(new Variable("501","pesca/recoleccion de recursos acuaticos","05"));
        variables.add(new Variable("502","acuicultura","05"));
        variables.add(new Variable("503","cambios en la explotacion de la tierra","05"));
        variables.add(new Variable("504","ganaderia/pastoreo animales domesticados","05"));
        variables.add(new Variable("505","produccion de cultivos","05"));
        variables.add(new Variable("506","recoleccion comercial de plantas silvestres","05"));
        variables.add(new Variable("507","recoleccion plantas silvestre para subsistencia","05"));
        variables.add(new Variable("508","caza comercial","05"));
        variables.add(new Variable("509","caza de subsistencia","05"));
        variables.add(new Variable("510","silvicultura/produccion de madera","05"));

        variables.add(new Variable("601","mineria","06"));
        variables.add(new Variable("602","explotacion de canteras","06"));
        variables.add(new Variable("603","petroleo y gas","06"));
        variables.add(new Variable("604","agua","06"));

        variables.add(new Variable("701","viento","07"));
        variables.add(new Variable("702","humedad relativa","07"));
        variables.add(new Variable("703","temperatura","07"));
        variables.add(new Variable("704","radiacion/luz","07"));
        variables.add(new Variable("705","polvo","07"));
        variables.add(new Variable("706","agua","07"));
        variables.add(new Variable("707","plagas","07"));
        variables.add(new Variable("708","microorganismos","07"));

        variables.add(new Variable("801","usos rituales/espirituales/religiosos y asociados","08"));
        variables.add(new Variable("802","valoracion del patrimonio por la sociedad","08"));
        variables.add(new Variable("803","actividades autoctonas de caza y recoleccion","08"));
        variables.add(new Variable("804","cambios en los sistemas tradicionales de vida y sistemas de conocimiento","08"));
        variables.add(new Variable("805","identidad, cohesion social, cambios en la poblacion y comunidades locales","08"));
        variables.add(new Variable("806","repercusiones del turismo/visitantes/actividades recreativas","08"));

        variables.add(new Variable("901","actividades ilicitas","09"));
        variables.add(new Variable("902","destruccion deliberada del patrimonio","09"));
        variables.add(new Variable("903","entrenamiento militar","09"));
        variables.add(new Variable("904","guerra","09"));
        variables.add(new Variable("905","terrorismo","09"));
        variables.add(new Variable("906","disturbios civiles","09"));

        variables.add(new Variable("101","tormentas","10"));
        variables.add(new Variable("102","inundaciones","10"));
        variables.add(new Variable("103","sequia","10"));
        variables.add(new Variable("104","desertificacion","10"));
        variables.add(new Variable("105","cambios en las aguas oceanicas","10"));
        variables.add(new Variable("106","cambios en la temperatura","10"));
        variables.add(new Variable("107","otras repercusiones a consecuencia del cambio climatico","10"));

        variables.add(new Variable("111","erupciones volcanicas","11"));
        variables.add(new Variable("112","terremotos","11"));
        variables.add(new Variable("113","tsunami/marejada","11"));
        variables.add(new Variable("114","avalanchas/desprendimiento de tierra","11"));
        variables.add(new Variable("115","erosion y sedimentacion","11"));
        variables.add(new Variable("116","fuego","11"));

        variables.add(new Variable("121","traslado de especies","12"));
        variables.add(new Variable("122","especies alienigenas/invasoras terrestres","12"));
        variables.add(new Variable("123","especies alienigenas/invasoras de agua dulce","12"));
        variables.add(new Variable("124","especies invasoras/alienigenas marinas","12"));
        variables.add(new Variable("125","especies superabundantes","12"));
        variables.add(new Variable("126","material modificado geneticamente","12"));

        variables.add(new Variable("131","actividades de investigacion/supervision con escasas repercusiones","13"));
        variables.add(new Variable("132","actividades de investigacion/supervision con importantes repercusiones","13"));
        variables.add(new Variable("133","actividades de gestion","13"));

        variables.add(new Variable("141"," otros factores","14"));

        items_factores = new CharSequence[factores.size()];
        for (int i=0; i<factores.size(); i++) items_factores[i] = factores.get(i).getNombre_factor();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_registration_form2);

        Intent intent = getIntent();
        monitor = intent.getStringExtra("MONITOR");
        codArea = intent.getStringExtra("AREA");

        monitor = intent.getStringExtra("MONITOR");
        Toast.makeText(
                MonitoringRegistrationForm2Activity.this,
                ""+monitor,
                Toast.LENGTH_SHORT)
                .show();


        btn_factores = (Button) findViewById(R.id.btn_factores);
        btn_variables = (Button) findViewById(R.id.btn_variables);
        et_longitud = (EditText) findViewById(R.id.et_longitud);
        et_latitud = (EditText) findViewById(R.id.et_latitud);

        fab_reg = (FloatingActionButton) findViewById(R.id.fab_reg);

        btn_factores.setOnClickListener(this);
        btn_variables.setOnClickListener(this);

        fab_reg.setOnClickListener(this);

        btn_variables.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_factores:
                createRadioListDialog(items_factores, "Seleccione un Factor", 1).show();
                break;
            case R.id.btn_variables:
                createRadioListDialog(items_variables, "Selecione una Variable", 2).show();
                break;
            case R.id.fab_reg:
                String respuesta = procesarOpciones(opciones);
                if(!respuesta.equals("")) createSimpleDialog(respuesta, "Opciones Elegidas", 1).show();
                else createSimpleDialog("Aun faltan elementos por seleccionar", "ERROR USUARIO", 2).show();
                break;
            default:
                break;
        }
    }

    public AlertDialog createRadioListDialog(final CharSequence[] items, String title, final int zn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int op = which+1;
                int con = 0;
                String opc = "";

                switch (zn){
                    case 1:
                        list_variables = new ArrayList<String>();
                        for (int i=0; i<factores.size(); i++){
                            if(factores.get(i).getNombre_factor().equals(items[which]))
                                opc = factores.get(i).getCodigo_factor()+"";
                        }

                        opciones[0]=Integer.parseInt(opc);
                        opciones[1] = 0;

                        for (int i = 0; i<variables.size(); i++) {
                            if(variables.get(i).getFactor().equals(opc))
                                list_variables.add(variables.get(i).getNombre_variable());
                        }

                        items_variables = new CharSequence[list_variables.size()];

                        for (int i = 0; i < list_variables.size(); i++)
                            items_variables[i] = list_variables.get(i);

                        btn_variables.setEnabled(true);
                        break;
                    case 2:
                        for (int i=0; i<variables.size(); i++){
                            if(variables.get(i).getNombre_variable().equals(items[which]))
                                opciones[1]=Integer.parseInt(variables.get(i).getCodigo_variable());
                        }

                        for (int i=0; i<opciones.length; i++) Log.e("OPCION:",""+opciones[i]);
                        break;
                    default:
                        break;
                }

                Toast.makeText(
                        MonitoringRegistrationForm2Activity.this,
                        "Seleccionaste: " + items[which],
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        return builder.create();
    }

    public AlertDialog createSimpleDialog(String mensaje, String titulo, int op) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final int opci = op;
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (opci){
                                    case 1:
                                        int lat = Integer.parseInt(et_latitud.getText().toString());
                                        int longi = Integer.parseInt(et_longitud.getText().toString());
                                        String vrble = opciones[1]+"";
                                        Intent intent = new Intent(MonitoringRegistrationForm2Activity.this, MonitoringRegistrationForm3Activity.class);

                                        intent.putExtra("MONITOR",monitor);
                                        intent.putExtra("AREA",codArea);
                                        intent.putExtra("VARIABLE",vrble);
                                        intent.putExtra("LONGITUD",""+longi);
                                        intent.putExtra("LATITUD",""+lat);

                                        startActivity(intent);
                                        finish();
                                        break;
                                    case 2:
                                        builder.create().dismiss();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        });

        return builder.create();
    }

    public String procesarOpciones(int[] opciones){
        for (int i=0; i<opciones.length; i++)if(opciones[i] == 0) return "";

        String resp = "";
        for (int i=0; i<opciones.length; i++){
            switch (i){
                case 0:
                    for (int j=0; j<factores.size(); j++){
                        if(factores.get(j).getCodigo_factor().equals(""+opciones[i]))
                            resp += "FACTORES: "+factores.get(j).getNombre_factor()+"\n";
                    }
                    break;
                case 1:
                    for (int j=0; j<variables.size(); j++){
                        if(variables.get(j).getCodigo_variable().equals(""+opciones[i]))
                            resp += "VARIABLES: "+variables.get(j).getNombre_variable()+"\n";
                    }
                    break;
                default:
                    break;
            }
        }
        return resp;
    }
}
