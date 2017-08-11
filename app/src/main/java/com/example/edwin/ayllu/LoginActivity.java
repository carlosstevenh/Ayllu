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

import com.example.edwin.ayllu.domain.AreaDbHelper;
import com.example.edwin.ayllu.domain.Categoria;
import com.example.edwin.ayllu.domain.FactorDbHelper;
import com.example.edwin.ayllu.domain.PaisDbHelper;
import com.example.edwin.ayllu.domain.SeccionDbHelper;
import com.example.edwin.ayllu.domain.SubtramoDbHelper;
import com.example.edwin.ayllu.domain.TramoDbHelper;
import com.example.edwin.ayllu.domain.VariableDbHelper;
import com.example.edwin.ayllu.domain.Zona;
import com.example.edwin.ayllu.io.AylluApiAdapter;
import com.example.edwin.ayllu.io.RestClient;
import com.example.edwin.ayllu.domain.SHA1;
import com.example.edwin.ayllu.io.model.CategoriaResponse;
import com.example.edwin.ayllu.io.model.ZonaResponse;
import com.example.edwin.ayllu.ui.AdminActivity;
import com.example.edwin.ayllu.domain.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public ArrayList<Usuario> users = new ArrayList<>();
    ArrayList<Zona> zonas = new ArrayList<>();
    ArrayList<Categoria> categorias = new ArrayList<>();
    public ArrayList<Usuario> lista = new ArrayList<>();//usuarios pertenecientes a el pais del administrador

    public static Usuario user;
    private EditText et1,et2;
    private static final String TAG = "ERRORES";
    private CheckBox contrasena;

    PaisDbHelper paisDbHelper;
    TramoDbHelper tramoDbHelper;
    SubtramoDbHelper subtramoDbHelper;
    SeccionDbHelper seccionDbHelper;
    AreaDbHelper areaDbHelper;
    FactorDbHelper factorDbHelper;
    VariableDbHelper variableDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //se instancian los elementos de la vista
        et1=(EditText)findViewById(R.id.txtName);
        et2=(EditText)findViewById(R.id.txtPsw);
        contrasena = (CheckBox) findViewById(R.id.cbCon);

        paisDbHelper = new PaisDbHelper(this);
        tramoDbHelper = new TramoDbHelper(this);
        subtramoDbHelper = new SubtramoDbHelper(this);
        seccionDbHelper = new SeccionDbHelper(this);
        areaDbHelper = new AreaDbHelper(this);
        factorDbHelper = new FactorDbHelper(this);
        variableDbHelper = new VariableDbHelper(this);

        contrasena.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b) et2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                else et2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });

    }

    //metodo que se encarga de iniciar sesión
    public void login(View v) {
        //peticcion que se le realiza al servidor para el inicio de sesión
        ProgressDialog loading = ProgressDialog.show(this,getResources().getString(R.string.login_user_process_authenticate),getResources().getString(R.string.login_user_process_message),false,false);
        downloadUsers(loading);
    }

    //metodo que se encarga de obtener los monitores del pais del administrador
    void getUsuarios(String pais, final AdminSQLite al){
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
                        monitores.put(AdminSQLite.COD_USU, lista.get(i).getCodigo_usu());
                        monitores.put(AdminSQLite.IDE_USU, lista.get(i).getIdentificacion_usu());
                        monitores.put(AdminSQLite.NOM_USU, lista.get(i).getNombre_usu());
                        monitores.put(AdminSQLite.APE_USU, lista.get(i).getApellido_usu());
                        monitores.put(AdminSQLite.TIP_USU, lista.get(i).getTipo_usu());
                        monitores.put(AdminSQLite.CON_USU, lista.get(i).getContrasena_usu());
                        monitores.put(AdminSQLite.CLA_API,lista.get(i).getClave_api());
                        monitores.put(AdminSQLite.PAI_USU, lista.get(i).getPais_usu());
                        bd1.insert(AdminSQLite.TABLENAME, null, monitores);
                    }
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

        Intent i=new Intent(this, AdminActivity.class);
        startActivity(i);
        finish();

    }
    /**
     * =============================================================================================
     * METODO: VALIDA Y DESCARGA EL USUARIO QUE INTENTA LOGUEARSE
     */
    private void downloadUsers(final ProgressDialog progressDialog){
        RestClient service = RestClient.retrofit.create(RestClient.class);
        String pass = SHA1.getHash(et2.getText().toString(),"SHA1");
        Call<ArrayList<Usuario>> requestUser = service.getUsuario(et1.getText().toString(),et2.getText().toString());
        requestUser.enqueue(new Callback<ArrayList<Usuario>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuario>>call, Response<ArrayList<Usuario>> response) {
                if (response.isSuccessful()) {
                    users = response.body();
                    if (!users.isEmpty()) downloadZones(progressDialog);
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.general_statistical_graph_process_message_server), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.general_statistical_graph_process_message_server), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.general_statistical_graph_process_message_server), Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     * =============================================================================================
     * METODO: DESCARGA LAS ZONAS
     */
    private ArrayList<Zona> downloadZones (final ProgressDialog progressDialog){
        Call<ZonaResponse> call2 = AylluApiAdapter.getApiService("ZONAS").getZona();
        call2.enqueue(new Callback<ZonaResponse>() {
            @Override
            public void onResponse(Call<ZonaResponse> call, Response<ZonaResponse> response) {
                if (response.isSuccessful()) {
                    zonas = response.body().getZonas();
                    if (!zonas.isEmpty()) downloadCategories(progressDialog);
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.general_statistical_graph_process_message_server), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.general_statistical_graph_process_message_server), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ZonaResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.general_statistical_graph_process_message_server), Toast.LENGTH_SHORT).show();
            }
        });

        return zonas;
    }

    /**
     * =============================================================================================
     * METODO: DESCARGA LAS ZONAS
     */
    private ArrayList<Categoria> downloadCategories (final ProgressDialog progressDialog){
        Call<CategoriaResponse> call3 = AylluApiAdapter.getApiService("CATEGORIAS").getCategoria();
        call3.enqueue(new Callback<CategoriaResponse>() {
            @Override
            public void onResponse(Call<CategoriaResponse> call, Response<CategoriaResponse> response) {
                if (response.isSuccessful()){
                    categorias = response.body().getCategorias();
                    if (!categorias.isEmpty()){
                        //------------------------------------------------------------------------------
                        //Se obtiene el usuario que inicio sesión
                        user=users.get(0);
                        //se inserta el usuario que inicio sesion a la base de datos del dispositivo
                        AdminSQLite admin = new AdminSQLite(getApplicationContext(), "login", null, 1);
                        SQLiteDatabase bd = admin.getWritableDatabase();

                        ContentValues registro = new ContentValues();

                        registro.put(AdminSQLite.COD_USU, user.getCodigo_usu());
                        registro.put(AdminSQLite.IDE_USU, user.getIdentificacion_usu());
                        registro.put(AdminSQLite.NOM_USU, user.getNombre_usu());
                        registro.put(AdminSQLite.APE_USU, user.getApellido_usu());
                        registro.put(AdminSQLite.TIP_USU, user.getTipo_usu());
                        registro.put(AdminSQLite.CON_USU, user.getContrasena_usu());
                        registro.put(AdminSQLite.CLA_API, user.getClave_api());
                        registro.put(AdminSQLite.PAI_USU, user.getPais_usu());

                        bd.insert(AdminSQLite.TABLENAME,null,registro);
                        bd.close();

                        //------------------------------------------------------------------------------
                        //Se obtienen los paises, tramos, subtramos y areas de la Zona descargada
                        paisDbHelper.savePaisList(zonas.get(0).getPaises());
                        tramoDbHelper.saveTramoList(zonas.get(0).getTramos());
                        subtramoDbHelper.saveSubtramoList(zonas.get(0).getSubtramos());
                        seccionDbHelper.saveSeccionList(zonas.get(0).getSecciones());
                        areaDbHelper.saveAreaList(zonas.get(0).getAreas());

                        paisDbHelper.close();
                        tramoDbHelper.close();
                        subtramoDbHelper.close();
                        seccionDbHelper.close();
                        areaDbHelper.close();

                        //------------------------------------------------------------------------------
                        //Se obtienen los Factores y Variables de la Categoria descargada
                        factorDbHelper.saveFactorList(categorias.get(0).getFactores());
                        variableDbHelper.saveVariableList(categorias.get(0).getVariables());

                        factorDbHelper.close();
                        variableDbHelper.close();

                        //------------------------------------------------------------------------------
                        //Se determina el tipo de usuario que esta ingresando a la aplicación
                        progressDialog.dismiss();
                        String tipo = user.getTipo_usu();
                        //Usuario tipo Administrador
                        if(tipo.equals("A")){
                            AdminSQLite admin1 = new AdminSQLite(getApplicationContext(), "login", null, 1);
                            //obtiene todos los monitores del pais del administrador
                            getUsuarios(user.getPais_usu(),admin1);
                            menuAdministrador();
                        }
                        //Usuario tipo Monitor
                        else {
                            Intent intent = new Intent(LoginActivity.this, MonitorMenuActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.general_statistical_graph_process_message_server), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.general_statistical_graph_process_message_server), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoriaResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.general_statistical_graph_process_message_server), Toast.LENGTH_SHORT).show();
            }
        });

        return categorias;
    }
}

