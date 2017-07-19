package com.example.edwin.ayllu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FormRespuesta extends AppCompatActivity {
    private Spinner eva, per, tie, pre, con,rec;
    String cod_mon = "", pais_mon = "";
    String [] eval, res;
    String pa,fm;
    LinearLayout lyPrincipal;
    ImageView ivType;
    TextView tvTitle, tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_respuesta);

        //se reciben los parametros recibidos por la actividad anterior
        Bundle bundle = getIntent().getExtras();
        pa = bundle.getString("pa");
        fm = bundle.getString("fm");
        Log.i("TAG", "punto afecation: " + fm);

        lyPrincipal = (LinearLayout) findViewById(R.id.ly_principal);
        ivType = (ImageView) findViewById(R.id.iv_type_record);
        tvTitle = (TextView) findViewById(R.id.tv_title_record);
        tvDescription = (TextView) findViewById(R.id.tv_description_record);

        //se instancian los elementos de la vista
        eva = (Spinner) findViewById(R.id.spinner_eva);
        per = (Spinner) findViewById(R.id.spinner_per);
        tie = (Spinner) findViewById(R.id.spinner_tie);
        pre = (Spinner) findViewById(R.id.spinner_pre);
        con = (Spinner) findViewById(R.id.spinner_con);
        rec = (Spinner) findViewById(R.id.spinner_rec);

        //se crean los elementos del spiner de evaluacion
        //se crean los elementos de los spinners de personal,tiempo,presupuesto,conocimeintos y recursos
        eval = getResources().getStringArray(R.array.list_general_evaluation);
        res = getResources().getStringArray(R.array.list_specific_evaluation);


        //se crean los adapters para los spinners
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, eval);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eva.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, res);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //se crea el spinner con los adapters
        per.setAdapter(dataAdapter1);
        tie.setAdapter(dataAdapter1);
        pre.setAdapter(dataAdapter1);
        con.setAdapter(dataAdapter1);
        rec.setAdapter(dataAdapter1);

        //------------------------------------------------------------------------------------------
        //Obtenemos el codigo del monitor y el pais del usuario en sesión
        Intent intent = getIntent();
        cod_mon = intent.getStringExtra("MONITOR");
        pais_mon = intent.getStringExtra("PAIS");
    }

    //metodo que se encarga de registrar una respuesta institucional
    public void registrar(View v){
        String monitor ="";
        //de la base de datos del dispositivo se obtiene la identificacion del monitores del que realizara el registro de la respuesta insitucional
        AdminSQLite admin = new AdminSQLite(getApplicationContext(),"login", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor datos = bd.rawQuery(
                "select * from "+ admin.TABLENAME , null);
        if(datos.getCount()==1){

            if (datos.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    monitor = datos.getString(1);

                } while (datos.moveToNext());
            }
        }
        else if (datos.getCount()>1){

            if (datos.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    if(datos.getString(5).equals("A")) monitor = datos.getString(1);

                } while (datos.moveToNext());
            }
        }

        bd.close();
        String evaluacion = "";

        //se realiza la conversion numeria dependiendo de la opcion que el monitor seleccione en la evaluacion
        String aux = String.valueOf(eva.getSelectedItem());
        if (aux.equals(eval[0])) evaluacion="1";
        else if (aux.equals(eval[1])) evaluacion = "2";
        else evaluacion = "3";

        //se obtiene el valor dependiendo de lo que el monitor selecciono y se la convierte a numeros para poder inserta en la base de datos
        String personal = (String.valueOf(per.getSelectedItem()));
        String tiempo = (String.valueOf(tie.getSelectedItem()));
        String presupuesto = (String.valueOf(pre.getSelectedItem()));
        String conocimiento = (String.valueOf(con.getSelectedItem()));
        String recursos = (String.valueOf(rec.getSelectedItem()));

        if(!per.getSelectedItem().equals(""))personal = comparar(personal);
        if(!tie.getSelectedItem().equals(""))tiempo = comparar(tiempo);
        if(!pre.getSelectedItem().equals(""))presupuesto = comparar(presupuesto);
        if(!con.getSelectedItem().equals(""))conocimiento = comparar(conocimiento);
        if(!rec.getSelectedItem().equals(""))recursos = comparar(recursos);

        Log.i("TAG", "punto afecation: " + pa+"-"+fm+"-"+ evaluacion+"-"+personal+"-"+tiempo+"-"+presupuesto+"-"+recursos+"-"+conocimiento+"-"+monitor);
        //se realiza la peticion del registro de la respuesta institucional al servidor
        final ProgressDialog loading = ProgressDialog.show(this,getResources().getString(R.string.institutional_response_form_process_message_upload),getResources().getString(R.string.institutional_response_form_process_message),false,false);
        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<String>> requestUser = service.addRespuesta(pa,fm, evaluacion,personal,tiempo,presupuesto,recursos,conocimiento,monitor);
        requestUser.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                loading.dismiss();
                if(response.isSuccessful()){
                    if(response.body().get(0).equals("1")){
                        Toast login = Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.institutional_response_form_process_message_positive), Toast.LENGTH_LONG);
                        login.show();
                    }
                    else{
                        Toast login = Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.institutional_response_form_process_message_negative), Toast.LENGTH_LONG);
                        login.show();
                    }
                }

                Intent intent = new Intent(FormRespuesta.this, MonitorMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                loading.dismiss();
                Toast login = Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.institutional_response_form_process_message_server), Toast.LENGTH_LONG);
                login.show();
            }
        });
        Log.i("TAG", "punto aaa: " +monitor);
    }

    //metodo encargador de convertir el resultado del spiner a un valir numerico
    private String comparar(String cad){
        String aux;
        if(cad.equals(res[0])) aux = "1";
        else if (cad.equals(res[1])) aux = "2";
        else if (cad.equals(res[2])) aux = "3";
        else if (cad.equals(res[3])) aux = "4";
        else aux ="1";
        return aux;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
