package com.example.edwin.ayllu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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

import com.example.edwin.ayllu.domain.Factor;
import com.example.edwin.ayllu.domain.FactorContract;
import com.example.edwin.ayllu.domain.FactorDbHelper;
import com.example.edwin.ayllu.domain.Variable;
import com.example.edwin.ayllu.domain.VariableContract;
import com.example.edwin.ayllu.domain.VariableDbHelper;

import static com.example.edwin.ayllu.domain.FactorContract.FactorEntry;
import static com.example.edwin.ayllu.domain.VariableContract.VariableEntry;

public class FiltrarActividad extends AppCompatActivity implements View.OnClickListener {

    private CheckBox fecha,factor,variable,arg,bol,chi,col,ecu,per;
    private Button bt;
    private DatePicker dp;
    private ImageView desde,hasta;
    private CardView cvf;
    private TextView inicio,fin;
    int año, mes, dia;
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    private long fcDesde = 0,fcHasta = 0,fcActual = 0;

    private CharSequence[] items_factores,items_variables;
    private String[] opciones = {"0","0"};
    private int[] pos = {-1, -1};

    private String[] paisesOpciones = {"null","null","null","null","null","null"};

    private FactorDbHelper factorDbHelper;
    private VariableDbHelper variableDbHelper;

    String item = "";
    Cursor cursor;
    int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        factorDbHelper = new FactorDbHelper(this);
        variableDbHelper = new VariableDbHelper(this);
        i = 0;

        cursor = factorDbHelper.generateQuery("SELECT * FROM ");
        if(cursor.moveToFirst()){
            items_factores = new CharSequence[cursor.getCount()];
            do {
                items_factores[i] = cursor.getString(2);
                i++;
            }while (cursor.moveToNext());
        }

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
        if(factor.isChecked() && !opciones[0].equals("0"))parametro.putString("fac",""+opciones[0]);
        else parametro.putString("fac","null");
        if(variable.isChecked() && !opciones[1].equals("0"))parametro.putString("var",""+opciones[1]);
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
    /**
     * =============================================================================================
     * METODO: Genera un Dialogo con las opciones de las categorias (FACTOR/VARIABLE)
     **/
    public AlertDialog createRadioListDialog(final CharSequence[] items, String title, final int zn) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setSingleChoiceItems(items, pos[zn - 1], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (zn) {
                    //------------------------------------------------------------------------------
                    //DIALOGO: Se crea una lista de RadioButtons de los Factores
                    case 1:
                        item = items_factores[which].toString();
                        cursor = factorDbHelper.generateConditionalQuery(new String[]{item}, FactorContract.FactorEntry.NOMBRE);
                        cursor.moveToFirst();

                        opciones[0] = cursor.getString(1);
                        pos[0] = which;
                        opciones[1] = "0";
                        pos[1] = -1;

                        items_variables = null;

                        i = 0;
                        cursor = variableDbHelper.generateConditionalQuery(new String[]{opciones[0]}, VariableContract.VariableEntry.FACTOR);
                        if (cursor.moveToFirst()) {
                            items_variables = new CharSequence[cursor.getCount()];
                            do {
                                items_variables[i] = cursor.getString(2);
                                i++;
                            } while (cursor.moveToNext());
                        }
                        break;
                    //------------------------------------------------------------------------------
                    //DIALOGO: Se crea una lista de RadioButtons de las Variables
                    case 2:
                        item = items_variables[which].toString();
                        cursor = variableDbHelper.generateConditionalQuery(new String[]{item}, VariableContract.VariableEntry.NOMBRE);
                        cursor.moveToFirst();

                        opciones[1] = cursor.getString(1);
                        pos[1] = which;
                        break;
                    default:
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

}
