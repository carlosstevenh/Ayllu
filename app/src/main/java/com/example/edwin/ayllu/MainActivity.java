package com.example.edwin.ayllu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.edwin.ayllu.administrador.Administrador;
import com.example.edwin.ayllu.domain.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public ArrayList<Usuario> u;
    public ArrayList<Usuario> lista;//usuarios pertenecientes a el pais del administrador
    public static Usuario user;
    private EditText et1,et2;
    private static final String TAG = "ERRORES";
    private CheckBox contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //se instancian los elementos de la vista
        et1=(EditText)findViewById(R.id.txtName);
        et2=(EditText)findViewById(R.id.txtPsw);
        contrasena = (CheckBox) findViewById(R.id.cbCon);

        contrasena.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    et2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else{
                    et2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

    }

    //metodo que se encarga de iniciar sesión
    public void login(View v) {

        //peticcion que se le realiza al servidor para el inicio de sesión
        final ProgressDialog loading = ProgressDialog.show(this,getResources().getString(R.string.iniciandoSesion),getResources().getString(R.string.esperar),false,false);

        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<Usuario>> requestUser = service.getUsuario(et1.getText().toString(),et2.getText().toString());
        requestUser.enqueue(new Callback<ArrayList<Usuario>>() {

            @Override
            public void onResponse(Call<ArrayList<Usuario>>call, Response<ArrayList<Usuario>> response) {
                loading.dismiss();
                if (response.isSuccessful()) {

                    u = response.body();
                    user=u.get(0);

                    //se inserta el usuario que inicio sesion a la base de datos del dispositivo
                    AdminSQLite admin = new AdminSQLite(getApplicationContext(), "login", null, 1);
                    SQLiteDatabase bd = admin.getWritableDatabase();

                    ContentValues registro = new ContentValues();

                    registro.put(admin.COD_USU, user.getCodigo_usu());
                    registro.put(admin.IDE_USU, user.getIdentificacion_usu());
                    registro.put(admin.NOM_USU, user.getNombre_usu());
                    registro.put(admin.APE_USU, user.getApellido_usu());
                    registro.put(admin.TIP_USU, user.getTipo_usu());
                    registro.put(admin.CON_USU, user.getContrasena_usu());
                    registro.put(admin.CLA_API, user.getClave_api());
                    registro.put(admin.PAI_USU, user.getPais_usu());

                    bd.insert(admin.TABLENAME,null,registro);
                    bd.close();

                    String tipo = user.getTipo_usu();
                    //si el usuario es administrador lo redirecciona al menu de administrador
                    if(tipo.equals("A")){

                        Log.i("TAG", "Bienvenido administrador!! ");
                        AdminSQLite admin1 = new AdminSQLite(getApplicationContext(), "login", null, 1);
                        //obtiene todos los monitores del pais del administrador
                        getUsuarios(user.getPais_usu(),admin1);
                        menuAdministrador();

                    }
                    //si el usuario es monitor lo redirecciona al menu de monitor
                    else {
                        Log.i("TAG", "Bienvenido monitor!! ");
                        Intent intent = new Intent(MainActivity.this, MonitorMenuActivity.class);
                        startActivity(intent);
                        finish();
                    }


                } else {
                    int statusCode = response.code();
                    Log.i("TAG", "error " + response.code());

                    // handle request errors yourself
                    //ResponseBody errorBody = response.errorBody();
                }


            }

            @Override
            public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {

                loading.dismiss();
                Log.e(TAG,"Error al iniciar sesión!!!!"+ t.getMessage());
                Toast login = Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.noSePudoConectarServidor), Toast.LENGTH_SHORT);
                login.show();
                //et3.setText("xxxxxx");
            }

        });
    }

    //metodo que se encarga de obtener los monitores del pais del administrador
    void getUsuarios(String pais, final AdminSQLite al){
        Log.i("TAG", "AA:> " +pais);
        //se realiza la peticion al servidor para obtener los monitores
        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<Usuario>> requestUsers = service.getUsuarios(pais);
        requestUsers.enqueue(new Callback<ArrayList<Usuario>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuario>> call, Response<ArrayList<Usuario>> response) {
                if (response.isSuccessful()) {
                    lista = response.body();

                    //se ingresa los monitores a la base datos del dispositivo
                    SQLiteDatabase bd1 = al.getWritableDatabase();
                    ContentValues monitores = new ContentValues();
                    for(int i = 0; i < lista.size(); i++){
                        Log.i("TAG", "entra ciclo: "+ lista.size());
                        monitores.put(al.COD_USU, lista.get(i).getCodigo_usu());
                        monitores.put(al.IDE_USU, lista.get(i).getIdentificacion_usu());
                        monitores.put(al.NOM_USU, lista.get(i).getNombre_usu());
                        monitores.put(al.APE_USU, lista.get(i).getApellido_usu());
                        monitores.put(al.TIP_USU, lista.get(i).getTipo_usu());
                        monitores.put(al.CON_USU, lista.get(i).getContrasena_usu());
                        monitores.put(al.CLA_API,lista.get(i).getClave_api());
                        monitores.put(al.PAI_USU, lista.get(i).getPais_usu());
                        bd1.insert(al.TABLENAME, null, monitores);
                        //bd1.close();

                    }
                    Log.i("TAG", "Numero de monitores: "+ lista.size() );

                } else {
                    int statusCode = response.code();
                    Log.i("TAG", "error " + response.code());

                    // handle request errors yourself
                    //ResponseBody errorBody = response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {
                Log.i("TAG", "monitores: " + t.getMessage());
            }
        });
    }
    //metodo que se encarga de redireccionar al menu del administrador 
    void menuAdministrador(){

        Intent i=new Intent(this, Administrador.class);
        startActivity(i);
        finish();

    }
}

