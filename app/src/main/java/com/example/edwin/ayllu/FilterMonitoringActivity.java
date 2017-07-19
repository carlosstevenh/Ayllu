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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import com.example.edwin.ayllu.domain.FactorContract;
import com.example.edwin.ayllu.domain.FactorDbHelper;
import com.example.edwin.ayllu.domain.VariableContract;
import com.example.edwin.ayllu.domain.VariableDbHelper;
import com.getbase.floatingactionbutton.FloatingActionButton;

public class FilterMonitoringActivity extends AppCompatActivity implements View.OnClickListener {

    private CheckBox fecha,factor,variable,arg,bol,chi,col,ecu,per;
    private FloatingActionButton fabFilter, fabDesde, fabHasta;
    private TextView inicio,fin, tvFactor, tvVariable;
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

        setContentView(R.layout.activity_filter_monitoring);
        inicio = (TextView)findViewById(R.id.inicio);
        fin = (TextView) findViewById(R.id.fin);
        tvFactor = (TextView) findViewById(R.id.tv_factor);
        tvVariable = (TextView) findViewById(R.id.tv_variable);

        fabDesde = (FloatingActionButton) findViewById(R.id.fab_desde);
        fabHasta = (FloatingActionButton) findViewById(R.id.fab_hasta);
        fabDesde.setEnabled(false);
        fabHasta.setEnabled(false);

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
                    fabDesde.setEnabled(true);
                    fabHasta.setEnabled(true);
                }
                else{
                    fabDesde.setEnabled(false);
                    fabHasta.setEnabled(false);
                    inicio.setText("");
                    fin.setText("");
                    inicio.setBackground(getResources().getDrawable(R.drawable.text_view_style_negative));
                    fin.setBackground(getResources().getDrawable(R.drawable.text_view_style_negative));
                    fcDesde = 0;
                    fcHasta = 0;
                }
            }
        });

        factor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    createRadioListDialog(items_factores, getResources().getString(R.string.search_form_alert_factor), 1).show();
                    variable.setEnabled(true);
                }
                else{
                    opciones[0] = "0";
                    variable.setEnabled(false);
                    variable.setChecked(false);
                    tvVariable.setText("");
                    tvFactor.setText("");
                    tvVariable.setBackground(getResources().getDrawable(R.drawable.text_view_style_negative));
                    tvFactor.setBackground(getResources().getDrawable(R.drawable.text_view_style_negative));
                }
            }
        });

        variable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    createRadioListDialog(items_variables, getResources().getString(R.string.search_form_alert_variable), 2).show();
                }
                else {
                    opciones[1] = "0";
                    tvVariable.setText("");
                    tvVariable.setBackground(getResources().getDrawable(R.drawable.text_view_style_negative));
                }
            }
        });

        fabDesde.setOnClickListener(this);
        fabHasta.setOnClickListener(this);

        final Calendar calendar = Calendar.getInstance();
        año = calendar.get(Calendar.YEAR);
        mes =  calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        fcActual = convertirFecha(dia,mes,año);

        fabFilter = (FloatingActionButton) findViewById(R.id.fab_filter);
        fabFilter.setOnClickListener(this);

    }

    //Obtiene la primera fecha que fue seleccionada
    private void actualizarLaFechaDesde() {
        fcDesde = convertirFecha(dia,mes,año);
        if(fcDesde>fcActual){
            inicio.setText("");
            Toast.makeText(
                    FilterMonitoringActivity.this,
                    getResources().getString(R.string.search_form_alert_date),
                    Toast.LENGTH_SHORT)
                    .show();
            inicio.setBackground(getResources().getDrawable(R.drawable.text_view_style_negative));
        }
        else{
            inicio.setText(new StringBuilder()
                    .append(mes +1).append("-")
                    .append(dia).append("-")
                    .append(año));
            inicio.setBackground(getResources().getDrawable(R.drawable.text_view_style_positive));
        }
    }

    //Obtiene la segunda fecha que fue seleccionada
    private void actualizarLaFechaHasta() {
        fcHasta = convertirFecha(dia,mes,año);
        if(fcHasta>fcActual){
            fin.setText("");
            Toast.makeText(
                    FilterMonitoringActivity.this,
                    getResources().getString(R.string.search_form_alert_date),
                    Toast.LENGTH_SHORT)
                    .show();
            fin.setBackground(getResources().getDrawable(R.drawable.text_view_style_negative));
        }
        else{
            fin.setText(new StringBuilder()
                    .append(mes +1).append("-")
                    .append(dia).append("-")
                    .append(año));
            fin.setBackground(getResources().getDrawable(R.drawable.text_view_style_positive));
        }
    }

    //Metodo utilizado para convertir la fecha para realizar los comparaciones
    private long convertirFecha(int dia, int mes, int año) {
        return (dia*24)+(mes*30*24)+(año*12*30*24);
    }

    //Metodo el cual identifica el boton que fue clickeado para realizar alguna acción
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_desde:
                this.showDialog(DATE_DIALOG_ID);
                break;
            case R.id.fab_hasta:
                this.showDialog(TIME_DIALOG_ID);
                break;
            case R.id.fab_filter:
                this.filtrar();
                break;
        }
    }

    //Metodo que se encarga de obtener todas las opciones que el usuario selecciono para posteriomente realizar el filtro de los monitoreos
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
            Intent intent = new Intent(FilterMonitoringActivity.this,MontoreosPuntosCriticos.class);
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
                    FilterMonitoringActivity.this,
                    getResources().getString(R.string.search_form_alert_date_range),
                    Toast.LENGTH_LONG)
                    .show();
        }

    }

    //Metodo que se encarga de crear el dialogo para la selecion de la primera fecha
    private DatePickerDialog.OnDateSetListener dateSetListener = new  DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            año = year;
            mes = monthOfYear;
            dia = dayOfMonth;

            actualizarLaFechaDesde();
        }
    };

    //Metodo que se encarga de crear el dialogo para la seleccion de la segunda fecha
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

                        tvFactor.setText(item);
                        tvFactor.setBackground(getResources().getDrawable(R.drawable.text_view_style_positive));
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

                        tvVariable.setText(item);
                        tvVariable.setBackground(getResources().getDrawable(R.drawable.text_view_style_positive));
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

    //Metodo que escucha el boton de back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
