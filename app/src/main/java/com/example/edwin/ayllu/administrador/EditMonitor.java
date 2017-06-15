package com.example.edwin.ayllu.administrador;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
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
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditMonitor extends AppCompatActivity {
    private TextView id, nom, ape, con,con2;
    private Button edit;
    private int codigo ;
    private String pais;
    private String tipo,ide;
    private String clave;
    private ArrayList<String> res;
    private TextInputLayout tilNombre,tilApellido,tilIdentificacion,tilCon1,tilCon2;
    private String contrasena;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_monitor);
        //Se reciben los parametros suminstrados por la actividad anterior
        Bundle bundle = getIntent().getExtras();

        codigo = bundle.getInt("codigo");
        pais = bundle.getString("pais");
        tipo = bundle.getString("tipo");
        clave = bundle.getString("cla");
        ide = bundle.getString("iden");

        //se instacian los elementos de la vista
        id = (TextView)findViewById(R.id.txtide);
        nom = (TextView)findViewById(R.id.txtname);
        ape = (TextView)findViewById(R.id.txtApe);
        con = (TextView)findViewById(R.id.txtCon);
        con2 = (TextView)findViewById(R.id.contraseña2);

        tilIdentificacion = (TextInputLayout) findViewById(R.id.til_ide);
        tilNombre = (TextInputLayout) findViewById(R.id.til_nom);
        tilApellido = (TextInputLayout) findViewById(R.id.til_ape);
        tilCon1 = (TextInputLayout) findViewById(R.id.til_con);
        tilCon2 = (TextInputLayout) findViewById(R.id.til_con1);

        //se llena los elementos de la vista con los paramentros recividos
        id.setText(bundle.getString("iden"));
        nom.setText(bundle.getString("nombre"));
        ape.setText(bundle.getString("apellido"));
        //con.setText(bundle.getString("con"));
        contrasena = bundle.getString("con");

        edit = (Button) findViewById(R.id.btnReg);
        //se invoca al metodo click del boton para luego llamar al metodo encargado de editar el monitor
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editar();
            }
        });

    }
    //metodo encargado de mostrar los mensajes de confirmacion de edicion del metodo
    public void editar(){

        boolean c1 = true;
        boolean ci = true;
        boolean c = true;
        boolean i = esIdentificacionValido(tilIdentificacion.getEditText().getText().toString(),tilIdentificacion);
        boolean n = esNombreValido(tilNombre.getEditText().getText().toString(),tilNombre);
        boolean a = esNombreValido(tilApellido.getEditText().getText().toString(),tilApellido);

        if(!con.getText().toString().equals("")){
            c = esContrasenaValido(tilCon1.getEditText().getText().toString(),tilCon1);
            c1 = esContrasenaValido(tilCon2.getEditText().getText().toString(),tilCon2);
        }
        if(!con2.getText().toString().equals("")) {
            c = esContrasenaValido(tilCon1.getEditText().getText().toString(),tilCon1);
            c1 = esContrasenaValido(tilCon2.getEditText().getText().toString(),tilCon2);
        }
        if(!con2.getText().toString().equals("") && !con.getText().toString().equals("") && c && c1) ci = esContrasenaIguales();
        if(i && n && a && c && c1 && ci) {
            createSimpleDialog(getResources().getString(R.string.confirmacionAccionEditar), getResources().getString(R.string.advertencia)).show();
        }
    }
    //metodo encargado de crear los dialogos informativos y confirmacion de la edicion del monitor
    public AlertDialog createSimpleDialog(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EditMonitor.this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton(getResources().getString(R.string.confirmar),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Se realiza la peticion al servidor para actualizar la informacion del monitor
                                if(!con.getText().toString().equals("") && !con2.getText().toString().equals("")){
                                    contrasena = con.getText().toString();
                                }
                                final ProgressDialog loading = ProgressDialog.show(EditMonitor.this,getResources().getString(R.string.actualizadoMonitor),getResources().getString(R.string.esperar),false,false);
                                RestClient service = RestClient.retrofit.create(RestClient.class);
                                Call<ArrayList<String>> requestAdd = service.editUsuario(id.getText().toString(),nom.getText().toString()
                                        ,ape.getText().toString(),contrasena,""+clave);
                                requestAdd.enqueue(new Callback<ArrayList<String>>() {
                                    @Override
                                    public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                                        loading.dismiss();
                                        if (response.isSuccessful()) {
                                            res = response.body();
                                            String aux = res.get(0);
                                            if(aux.equals("1")){
                                                //se actaliza el monitor tambien en la base de datos del movil
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
                                                        getResources().getString(R.string.monitorActualizado), Toast.LENGTH_LONG);
                                                login.show();
                                                Intent intent = new Intent(getApplicationContext(),Administrador.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                            else{
                                                Toast login = Toast.makeText(getApplicationContext(),
                                                        getResources().getString(R.string.seProdujoUnError), Toast.LENGTH_LONG);
                                                login.show();
                                            }


                                        } else {
                                            int statusCode = response.code();
                                            Log.i("TAG", "error " + response.code());
                                            Toast login = Toast.makeText(getApplicationContext(),
                                                    getResources().getString(R.string.seProdujoUnError), Toast.LENGTH_LONG);
                                            login.show();

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                                        loading.dismiss();
                                        Toast login = Toast.makeText(getApplicationContext(),
                                                getResources().getString(R.string.noSePudoConectarServidor), Toast.LENGTH_LONG);
                                        login.show();
                                    }
                                });
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancelar),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        });

        return builder.create();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //METODOS: validan los datos que se introducen en las cajas de texto (nombre, apellido,contraseñas)

    private boolean esNombreValido(String nombre,TextInputLayout til) {
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        if (!patron.matcher(nombre).matches() || nombre.length() > 30) {
            til.setError(getResources().getString(R.string.campoInvalido));
            return false;
        } else {
            til.setError(null);
        }

        return true;
    }

    private boolean esIdentificacionValido(String nombre,TextInputLayout til) {
        Pattern patron = Pattern.compile("^.{1,}");
        if (!patron.matcher(nombre).matches()) {
            til.setError(getResources().getString(R.string.campoVacio));
            return false;
        } else {
            til.setError(null);
        }

        return true;
    }

    private boolean esContrasenaValido(String nombre, TextInputLayout til) {
        Pattern patron = Pattern.compile("(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$");
        if (!patron.matcher(nombre).matches() || nombre.length() > 30) {
            til.setError(getResources().getString(R.string.contrasenaError));
            return false;
        } else {
            til.setError(null);
        }

        return true;
    }
    private boolean esContrasenaIguales() {
        if (!con.getText().toString().equals(con2.getText().toString())) {
            tilCon1.setError(getResources().getString(R.string.contraseñasDiferentes));
            tilCon2.setError(getResources().getString(R.string.contraseñasDiferentes));
            return false;
        } else {
            tilCon1.setError(null);
            tilCon2.setError(null);
        }

        return true;
    }
}
