package com.example.edwin.ayllu.Adiminstrador;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.AdminSQLite;
import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.RestClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditMonitor extends AppCompatActivity {
    private TextView id, nom, ape, con;
    int codigo ;
    private String pais;
    private String tipo,ide;
    private String clave;
    private ArrayList<String> res;
    private Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_monitor);
        Bundle bundle = getIntent().getExtras();

        codigo = bundle.getInt("codigo");
        pais = bundle.getString("pais");
        tipo = bundle.getString("tipo");
        clave = bundle.getString("cla");
        ide = bundle.getString("iden");

        id = (TextView)findViewById(R.id.txtide);
        nom = (TextView)findViewById(R.id.txtname);
        ape = (TextView)findViewById(R.id.txtApe);
        con = (TextView)findViewById(R.id.txtCon);

        id.setText(bundle.getString("iden"));
        nom.setText(bundle.getString("nombre"));
        ape.setText(bundle.getString("apellido"));
        con.setText(bundle.getString("con"));

    }
    public void eliminar(View view){
        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<String>> requestDelete = service.deleteUsuario(ide);
        requestDelete.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                res = response.body();
                String aux = res.get(0);
                if(aux.equals("1")){
                    AdminSQLite admin1 = new AdminSQLite(getApplicationContext(), "login", null, 1);
                    SQLiteDatabase bd1 = admin1.getWritableDatabase();
                    bd1.delete(admin1.TABLENAME, admin1.IDE_USU +"='"+ ide +"'", null);
                    bd1.close();
                    Intent intent = new Intent(getApplicationContext(),Administrador.class);
                    startActivity(intent);
                    Toast login = Toast.makeText(getApplicationContext(),
                            "Eliminación exitosa", Toast.LENGTH_SHORT);
                }
                else{
                    Toast login = Toast.makeText(getApplicationContext(),
                            "No se pudo eliminar", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                Toast login = Toast.makeText(getApplicationContext(),
                        "No se pudo eliminar", Toast.LENGTH_SHORT);
            }
        });
    }
    public void editar(View view){
        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<String>> requestAdd = service.editUsuario(id.getText().toString(),nom.getText().toString()
        ,ape.getText().toString(),con.getText().toString(),""+clave);
        requestAdd.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if (response.isSuccessful()) {
                    res = response.body();
                    String aux = res.get(0);
                    if(aux.equals("1")){
                        AdminSQLite admin = new AdminSQLite(getApplicationContext(), "login", null, 1);
                        SQLiteDatabase bd = admin.getWritableDatabase();
                        String update = "UPDATE " +admin.TABLENAME+" SET "+ admin.IDE_USU + "='" +id.getText().toString() +"', "
                                + admin.NOM_USU+ "='"+nom.getText().toString()+ "', "
                                + admin.APE_USU +"='" +ape.getText().toString() +"', "
                                + admin.CON_USU + "='" + con.getText().toString() +"' WHERE "
                                + admin.CLA_API + "='"+clave+"'";
                        bd.execSQL(update);
                        bd.close();
                        Toast login = Toast.makeText(getApplicationContext(),
                                "Registro adicionado", Toast.LENGTH_SHORT);
                        login.show();
                        Intent intent = new Intent(getApplicationContext(),Administrador.class);
                        startActivity(intent);

                        Log.i("TAG", "error "+ res.get(0) );
                    }
                    else{
                        Toast login = Toast.makeText(getApplicationContext(),
                                "Error al registrar", Toast.LENGTH_SHORT);
                    }


                } else {
                    int statusCode = response.code();
                    Log.i("TAG", "error " + response.code());
                    Toast login = Toast.makeText(getApplicationContext(),
                            "Error al registrar", Toast.LENGTH_SHORT);

                    // handle request errors yourself
                    //ResponseBody errorBody = response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                Toast login = Toast.makeText(getApplicationContext(),
                        "Error al registrar", Toast.LENGTH_SHORT);
            }
        });
    }
}
