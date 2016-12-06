package com.example.edwin.ayllu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.Usuario;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FormRespuesta extends AppCompatActivity {
    private Spinner eva, per, tie, pre, con,rec;
    List<String> eval, res;
    String pa,fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_respuesta);

        Bundle bundle = getIntent().getExtras();
        pa = bundle.getString("pa");
        fm = bundle.getString("fm");
        Log.i("TAG", "punto afecation: " + pa);

        eva = (Spinner) findViewById(R.id.spinner_ava);
        per = (Spinner) findViewById(R.id.spinner_per);
        tie = (Spinner) findViewById(R.id.spinner_tie);
        pre = (Spinner) findViewById(R.id.spinner_pre);
        con = (Spinner) findViewById(R.id.spinner_con);
        rec = (Spinner) findViewById(R.id.spinner_rec);

        eval = new ArrayList<String>();
        eval.add("Insignificantes");
        eval.add("Menores");
        eval.add("Significativas");

        res = new ArrayList<String>();
        res.add("Ninguna Capacidad");
        res.add("Capacidad baja");
        res.add("Capacidad media");
        res.add("Capacidad alta");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,  eval);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eva.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,  res);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        per.setAdapter(dataAdapter1);
        tie.setAdapter(dataAdapter1);
        pre.setAdapter(dataAdapter1);
        con.setAdapter(dataAdapter1);
        rec.setAdapter(dataAdapter1);
    }

    public void registrar(View v){
        String monitor ="";
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
        String aux = String.valueOf(eva.getSelectedItem());
        if (aux.equals("Insignificantes")) evaluacion="1";
        else if (aux.equals("Menores")) evaluacion = "2";
        else evaluacion = "3";

        String personal = comparar(String.valueOf(per.getSelectedItem()));
        String tiempo = comparar(String.valueOf(tie.getSelectedItem()));
        String presupuesto = comparar(String.valueOf(pre.getSelectedItem()));
        String conocimiento = comparar(String.valueOf(con.getSelectedItem()));
        String recursos = comparar(String.valueOf(rec.getSelectedItem()));

        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<String>> requestUser = service.addRespuesta(pa,fm, evaluacion,personal,tiempo,presupuesto,recursos,conocimiento,monitor);
        requestUser.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                Toast login = Toast.makeText(getApplicationContext(),
                        "Registro exitoso", Toast.LENGTH_SHORT);
                login.show();

                Intent intent = new Intent(FormRespuesta.this, MonitorMenuActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                Toast login = Toast.makeText(getApplicationContext(),
                        "Error al registrar", Toast.LENGTH_SHORT);
                login.show();
            }
        });
        Log.i("TAG", "punto aaa: " +monitor);
    }
    private String comparar(String cad){
        String aux ="";
        if(cad.equals("Ninguna Capacidad")) aux = "1";
        else if (cad.equals("Capacidad baja")) aux = "2";
        else if (cad.equals("Capacidad media")) aux = "3";
        else if (cad.equals("Capacidad alta")) aux = "4";
        return aux;

    }
}
