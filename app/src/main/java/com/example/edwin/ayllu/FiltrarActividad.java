package com.example.edwin.ayllu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import com.example.edwin.ayllu.Adiminstrador.EditMonitor;
import com.example.edwin.ayllu.domain.Factor;
import com.example.edwin.ayllu.domain.Variable;

public class FiltrarActividad extends AppCompatActivity implements View.OnClickListener {

    private CheckBox fecha,factor,variable,arg,bol,chi,col,ecu,per;
    private Button bt;
    private DatePicker dp;
    private ImageView desde,hasta;
    private CardView cvf;
    private TextView inicio,fin;
    int año;
    int mes;
    int dia;
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    private long fcDesde = 0,fcHasta = 0,fcActual = 0;

    private ArrayList<Factor> factores = new ArrayList<>();
    private ArrayList<Variable> variables = new ArrayList<>();

    private CharSequence[] items_factores,items_variables;
    private ArrayList<String> list_variables;

    private String[] paisesOpciones = {"null","null","null","null","null","null"};
    private String[] opciones = {"0","0"};


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

        setContentView(R.layout.activity_filtrar_actividad);
        inicio = (TextView)findViewById(R.id.inicio);
        fin = (TextView) findViewById(R.id.fin);

        desde = (ImageView) findViewById(R.id.imgViewDesde);
        hasta = (ImageView) findViewById(R.id.hasta);
        desde.setEnabled(false);
        hasta.setEnabled(false);
        cvf = (CardView) findViewById(R.id.CardViewFecha);

        fecha = (CheckBox) findViewById(R.id.fechaMonitoreo);
        factor = (CheckBox) findViewById(R.id.factor);
        arg = (CheckBox) findViewById(R.id.argentina);
        bol = (CheckBox) findViewById(R.id.bolivia);
        chi = (CheckBox) findViewById(R.id.chile);
        col = (CheckBox) findViewById(R.id.colombia);
        ecu = (CheckBox) findViewById(R.id.ecuador);
        per = (CheckBox) findViewById(R.id.peru);
        variable = (CheckBox) findViewById(R.id.variable);
        variable.setEnabled(false);

        fecha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    desde.setEnabled(true);
                    hasta.setEnabled(true);
                }
                else{
                    desde.setEnabled(false);
                    hasta.setEnabled(false);
                }
            }
        });

        factor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    createRadioListDialog(items_factores, "Seleccione un Factor", 1).show();
                    variable.setEnabled(true);
                }
                else{
                    opciones[0] = "0";
                    variable.setEnabled(false);
                    variable.setChecked(false);
                }
            }
        });

        variable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    createRadioListDialog(items_variables, "Selecione una Variable", 2).show();
                }
                else {
                    opciones[1] = "0";
                }
            }
        });

        desde.setOnClickListener(this);
        hasta.setOnClickListener(this);

        final Calendar calendar = Calendar.getInstance();
        año = calendar.get(Calendar.YEAR);
        mes =  calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        fcActual = convertirFecha(dia,mes,año);

        bt = (Button) findViewById(R.id.button);
        bt.setOnClickListener(this);

    }

    private void actualizarLaFechaDesde() {
        fcDesde = convertirFecha(dia,mes,año);
        if(fcDesde>fcActual){
            inicio.setText("");
            Toast.makeText(
                    FiltrarActividad.this,
                    "Fecha incorrecta",
                    Toast.LENGTH_SHORT)
                    .show();
        }
        else{
            inicio.setText(new StringBuilder()
                    .append(mes +1).append("-")
                    .append(dia).append("-")
                    .append(año));
        }
    }

    private void actualizarLaFechaHasta() {
        fcHasta = convertirFecha(dia,mes,año);
        if(fcHasta>fcActual){
            fin.setText("");
            Toast.makeText(
                    FiltrarActividad.this,
                    "Fecha incorrecta",
                    Toast.LENGTH_SHORT)
                    .show();
        }
        else{
            fin.setText(new StringBuilder()
                    .append(mes +1).append("-")
                    .append(dia).append("-")
                    .append(año));
        }
    }

    private long convertirFecha(int dia, int mes, int año) {
        return (dia*24)+(mes*30*24)+(año*12*30*24);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgViewDesde:
                this.showDialog(DATE_DIALOG_ID);
                break;
            case R.id.hasta:
                this.showDialog(TIME_DIALOG_ID);
                break;
            case R.id.button:
                this.filtrar();
                break;
        }
    }

    private void filtrar() {
        boolean apro = true;

        paisesOpciones[0] = "null";
        paisesOpciones[1] = "null";
        paisesOpciones[2] = "null";
        paisesOpciones[3] = "null";
        paisesOpciones[4] = "null";
        paisesOpciones[5] = "null";

        if(arg.isChecked())paisesOpciones[0]= "01";
        if(bol.isChecked())paisesOpciones[1]= "02";
        if(chi.isChecked())paisesOpciones[2]= "03";
        if(col.isChecked())paisesOpciones[3]= "04";
        if(ecu.isChecked())paisesOpciones[4]= "05";
        if(per.isChecked())paisesOpciones[5]= "06";

        String aux1 = "";
        for(int i = 0; i < 6; i++)if(!paisesOpciones[i].equals("null")) aux1 = paisesOpciones[i];
        if(!aux1.equals("")){
            for(int i = 0; i < 6; i++)if(paisesOpciones[i].equals("null")) paisesOpciones[i] = aux1;
        }
        Bundle parametro = new Bundle();

        parametro.putString("p1", paisesOpciones[0]);
        parametro.putString("p2", paisesOpciones[1]);
        parametro.putString("p3", paisesOpciones[2]);
        parametro.putString("p4", paisesOpciones[3]);
        parametro.putString("p5", paisesOpciones[4]);
        parametro.putString("p6", paisesOpciones[5]);

        boolean aux=false;
        if(fecha.isChecked()){
            if(fcHasta!=0 && fcDesde!=0 && fcDesde<fcHasta )aux=true;
            else apro = false;
        }
        if(aux){
            parametro.putString("fi",inicio.getText().toString());
            parametro.putString("ff",fin.getText().toString());
        }
        else {
            parametro.putString("fi","null");
            parametro.putString("ff","null");
        }
        if(factor.isChecked())parametro.putString("fac",""+opciones[0]);
        else parametro.putString("fac","null");
        if(variable.isChecked())parametro.putString("var",""+opciones[1]);
        else parametro.putString("var","null");

        if(apro){
            Intent intent = new Intent(FiltrarActividad.this,MontoreosPuntosCriticos.class);
            intent.putExtras(parametro);
            startActivity(intent);
        }
        else{
            paisesOpciones[0] = "null";
            paisesOpciones[1] = "null";
            paisesOpciones[2] = "null";
            paisesOpciones[3] = "null";
            paisesOpciones[4] = "null";
            paisesOpciones[5] = "null";

            Toast.makeText(
                    FiltrarActividad.this,
                    "seleccione el rango de fechas",
                    Toast.LENGTH_LONG)
                    .show();
        }

    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new  DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            año = year;
            mes = monthOfYear;
            dia = dayOfMonth;

            actualizarLaFechaDesde();
        }
    };

    private DatePickerDialog.OnDateSetListener timeSetListener = new  DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            año = year;
            mes = monthOfYear;
            dia = dayOfMonth;

            actualizarLaFechaHasta();
        }
    };
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,dateSetListener,año,mes,dia);
            case TIME_DIALOG_ID:
                return new DatePickerDialog(this,timeSetListener,año,mes,dia);
        }
        return null;
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

                        opciones[0]=opc;
                        opciones[1] = "0";

                        for (int i = 0; i<variables.size(); i++) {
                            if(variables.get(i).getFactor().equals(opc))
                                list_variables.add(variables.get(i).getNombre_variable());
                        }

                        items_variables = new CharSequence[list_variables.size()];

                        for (int i = 0; i < list_variables.size(); i++)
                            items_variables[i] = list_variables.get(i);

                        break;
                    case 2:
                        for (int i=0; i<variables.size(); i++){
                            if(variables.get(i).getNombre_variable().equals(items[which]))
                                opciones[1]=variables.get(i).getCodigo_variable();
                        }

                        for (int i=0; i<opciones.length; i++) Log.e("OPCION:",""+opciones[i]);
                        break;
                    default:
                        break;
                }

                Toast.makeText(
                        FiltrarActividad.this,
                        "Seleccionaste: " + items[which],
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        return builder.create();
    }

}
