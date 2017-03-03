package com.example.edwin.ayllu.administrador;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private TextView id, nom, ape, con,con2;
    private Button edit;
    int codigo ;
    private String pais;
    private String tipo,ide;
    private String clave;
    private ArrayList<String> res;
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
        con2 = (TextView)findViewById(R.id.txtCon2);

        id.setText(bundle.getString("iden"));
        nom.setText(bundle.getString("nombre"));
        ape.setText(bundle.getString("apellido"));
        con.setText(bundle.getString("con"));
        con2.setText(bundle.getString("con"));

        edit = (Button) findViewById(R.id.btnReg);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editar();
            }
        });

    }
    public void editar(){
        if(con.getText().toString().equals(con2.getText().toString())){
            createSimpleDialog("Esta seguro que desea actualizar el monitor?","Atención.").show();
        }
        else{
            Toast login = Toast.makeText(getApplicationContext(),
                    "Las contraseñas no coinciden", Toast.LENGTH_LONG);
            login.show();
        }
    }

    public AlertDialog createSimpleDialog(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EditMonitor.this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog loading = ProgressDialog.show(EditMonitor.this,"Actualizando monitor","Por favor espere...",false,false);
                                RestClient service = RestClient.retrofit.create(RestClient.class);
                                Call<ArrayList<String>> requestAdd = service.editUsuario(id.getText().toString(),nom.getText().toString()
                                        ,ape.getText().toString(),con.getText().toString(),""+clave);
                                requestAdd.enqueue(new Callback<ArrayList<String>>() {
                                    @Override
                                    public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                                        loading.dismiss();
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
                                                        "Monitor actualizado", Toast.LENGTH_LONG);
                                                login.show();
                                                Intent intent = new Intent(getApplicationContext(),Administrador.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                            else{
                                                Toast login = Toast.makeText(getApplicationContext(),
                                                        "Error al actualizar", Toast.LENGTH_LONG);
                                                login.show();
                                            }


                                        } else {
                                            int statusCode = response.code();
                                            Log.i("TAG", "error " + response.code());
                                            Toast login = Toast.makeText(getApplicationContext(),
                                                    "Error al actualizar", Toast.LENGTH_LONG);
                                            login.show();

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                                        loading.dismiss();
                                        Toast login = Toast.makeText(getApplicationContext(),
                                                "Error al actualizar el monitor", Toast.LENGTH_LONG);
                                        login.show();
                                    }
                                });
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
}
