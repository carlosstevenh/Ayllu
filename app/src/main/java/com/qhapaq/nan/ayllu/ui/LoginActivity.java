package com.qhapaq.nan.ayllu.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.qhapaq.nan.ayllu.R;
import com.qhapaq.nan.ayllu.domain.area.AreaDbHelper;
import com.qhapaq.nan.ayllu.domain.Categoria;
import com.qhapaq.nan.ayllu.domain.factor.FactorDbHelper;
import com.qhapaq.nan.ayllu.domain.pais.PaisDbHelper;
import com.qhapaq.nan.ayllu.domain.seccion.SeccionDbHelper;
import com.qhapaq.nan.ayllu.domain.subtramo.SubtramoDbHelper;
import com.qhapaq.nan.ayllu.domain.tramo.TramoDbHelper;
import com.qhapaq.nan.ayllu.domain.usuario.UsuarioDbHelper;
import com.qhapaq.nan.ayllu.domain.variable.VariableDbHelper;
import com.qhapaq.nan.ayllu.domain.Zona;
import com.qhapaq.nan.ayllu.io.AylluApiAdapter;
import com.qhapaq.nan.ayllu.io.model.CategoriaResponse;
import com.qhapaq.nan.ayllu.io.model.UsuarioResponse;
import com.qhapaq.nan.ayllu.io.model.ZonaResponse;
import com.qhapaq.nan.ayllu.domain.usuario.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public ArrayList<Usuario> users = new ArrayList<>();
    ArrayList<Zona> zonas = new ArrayList<>();
    ArrayList<Categoria> categorias = new ArrayList<>();
    public ArrayList<Usuario> lista = new ArrayList<>();

    public static Usuario user;
    private EditText et1, et2;

    PaisDbHelper paisDbHelper;
    TramoDbHelper tramoDbHelper;
    SubtramoDbHelper subtramoDbHelper;
    SeccionDbHelper seccionDbHelper;
    AreaDbHelper areaDbHelper;
    FactorDbHelper factorDbHelper;
    VariableDbHelper variableDbHelper;
    UsuarioDbHelper usuarioDbHelper;

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //se instancian los elementos de la vista
        et1 = (EditText) findViewById(R.id.txtName);
        et2 = (EditText) findViewById(R.id.txtPsw);

        paisDbHelper = new PaisDbHelper(this);
        tramoDbHelper = new TramoDbHelper(this);
        subtramoDbHelper = new SubtramoDbHelper(this);
        seccionDbHelper = new SeccionDbHelper(this);
        areaDbHelper = new AreaDbHelper(this);
        factorDbHelper = new FactorDbHelper(this);
        variableDbHelper = new VariableDbHelper(this);
        usuarioDbHelper = new UsuarioDbHelper(this);

    }

    /**
     * =============================================================================================
     * METODO: INICIA SESIÓN
     */
    public void login(View v) {
        //peticcion que se le realiza al servidor para el inicio de sesión
        ProgressDialog loading = ProgressDialog.show(this, getResources().getString(R.string.login_user_process_authenticate), getResources().getString(R.string.login_user_process_message), false, false);
        downloadUsers(loading);
    }

    /**
     * =============================================================================================
     * METODO: REDIRECCIONA AL MENU DEL ADMINISTRADOR
     */
    void menuAdministrador() {
        Intent i = new Intent(this, AdminActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * =============================================================================================
     * METODO: REDIRECCIONA AL MENU DEL MONITOR
     */
    void menuMonitor() {
        Intent intent = new Intent(LoginActivity.this, MonitorMenuActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * =============================================================================================
     * METODO: VALIDA Y DESCARGA EL USUARIO QUE INTENTA LOGUEARSE
     */
    private void downloadUsers(final ProgressDialog progressDialog) {
        //String pass = SHA1.getHash(et2.getText().toString(),"SHA1");
        Usuario currrentUser = new Usuario(et1.getText().toString(), et2.getText().toString());

        Call<UsuarioResponse> callUser = AylluApiAdapter.getApiService("USUARIOS").loginUsuario(currrentUser);
        callUser.enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {
                if (response.isSuccessful()) {
                    users = response.body().getUsuarios();
                    if (!users.isEmpty()) downloadZones(progressDialog);
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.general_statistical_graph_process_message_server), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.general_statistical_graph_process_message_server), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
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
    private ArrayList<Zona> downloadZones(final ProgressDialog progressDialog) {
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
                } else {
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
    private ArrayList<Categoria> downloadCategories(final ProgressDialog progressDialog) {
        Call<CategoriaResponse> call3 = AylluApiAdapter.getApiService("CATEGORIAS").getCategoria();
        call3.enqueue(new Callback<CategoriaResponse>() {
            @Override
            public void onResponse(Call<CategoriaResponse> call, Response<CategoriaResponse> response) {
                if (response.isSuccessful()) {
                    categorias = response.body().getCategorias();
                    if (!categorias.isEmpty()) {
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

                        //Registro del usuario logueado en el dispositivo
                        user = users.get(0);
                        usuarioDbHelper.saveUsuario(user);
                        usuarioDbHelper.close();

                        //------------------------------------------------------------------------------
                        //Se determina el tipo de usuario que esta ingresando a la aplicación
                        String tipo = user.getTipo_usu();
                        progressDialog.dismiss();

                        switch (tipo) {
                            case "A":
                                menuAdministrador();
                                break;
                            case "M":
                                menuMonitor();
                                break;
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.general_statistical_graph_process_message_server), Toast.LENGTH_SHORT).show();
                    }
                } else {
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

