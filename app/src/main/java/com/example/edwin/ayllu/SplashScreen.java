package com.example.edwin.ayllu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.example.edwin.ayllu.ui.AdministratorActivity;
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
import com.example.edwin.ayllu.io.model.CategoriaResponse;
import com.example.edwin.ayllu.io.model.ZonaResponse;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends Activity {
    String tipo;
    ArrayList<Zona> zonas = new ArrayList<>();
    ArrayList<Categoria> categorias = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        AdminSQLite admin = new AdminSQLite(getApplicationContext(),"login", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor datos = bd.rawQuery("SELECT * FROM "+ admin.TABLENAME , null);
        int aux = datos.getCount();
        int i = 0;
        if (datos.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                if(datos.getString(5).equals("A")) i++;
                tipo = datos.getString(5);

            } while (datos.moveToNext());
        }

        Log.i("TAG", "ADMINISTRADORES=> " + tipo);
        if(aux==1 || i == 1){
            if(i == 1){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashScreen.this, AdministratorActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },2000);
            }
            else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashScreen.this, MonitorMenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },2000);
            }

        }

        else {
            if(aux>1) {
                AdminSQLite admin1 = new AdminSQLite(getApplicationContext(), "login", null, 1);
                SQLiteDatabase bd1 = admin1.getWritableDatabase();
                bd1.delete(admin1.TABLENAME, null, null);
                bd1.close();

            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);


        }
        bd.close();
    }
    //==============================================================================================
    //METODO: OBTIENE Y PROCESA DATOS DE (ZONAS/CATEGORIAS) Y MONITOREOS ALMACENADOS EN EL MOVIL
    @Override
    protected void onStart() {
        super.onStart();
        //------------------------------------------------------------------------------------------
        //OBTIENE TODOS LOS DATOS DE ZONAS (PAISES/TRAMOS/SUBTRAMOS/SECCIONES/AREAS)
        final PaisDbHelper paisDbHelper = new PaisDbHelper(this);
        final TramoDbHelper tramoDbHelper = new TramoDbHelper(this);
        final SubtramoDbHelper subtramoDbHelper = new SubtramoDbHelper(this);
        final SeccionDbHelper seccionDbHelper = new SeccionDbHelper(this);
        final AreaDbHelper areaDbHelper = new AreaDbHelper(this);

        int sizePais = paisDbHelper.getSizeDatabase();
        int sizeTramo = tramoDbHelper.getSizeDatabase();
        int sizeSubtramo = subtramoDbHelper.getSizeDatabase();
        int sizeSeccion = seccionDbHelper.getSizeDatabase();
        int sizeArea = areaDbHelper.getSizeDatabase();

        if(sizePais == 0 && sizeTramo == 0 && sizeSubtramo == 0 && sizeSeccion == 0 && sizeArea == 0){
            deleteCache(this);
            Log.e("INFO","TABLAS DE ZONAS VACIAS :( - ESCRIBIENDO... :)");
            //--------------------------------------------------------------------------------------
            //OBTIENE LAS ZONAS
            Call<ZonaResponse> call2 = AylluApiAdapter.getApiService("ZONAS").getZona();
            call2.enqueue(new Callback<ZonaResponse>() {
                @Override
                public void onResponse(Call<ZonaResponse> call, Response<ZonaResponse> response) {
                    if (response.isSuccessful()) {
                        zonas = response.body().getZonas();
                        Log.e("INFO:","Se cargo toda la información de zonas");

                        paisDbHelper.savePaisList(zonas.get(0).getPaises());
                        tramoDbHelper.saveTramoList(zonas.get(0).getTramos());
                        subtramoDbHelper.saveSubtramoList(zonas.get(0).getSubtramos());
                        seccionDbHelper.saveSeccionList(zonas.get(0).getSecciones());
                        areaDbHelper.saveAreaList(zonas.get(0).getAreas());
                    }
                }

                @Override
                public void onFailure(Call<ZonaResponse> call, Throwable t) {
                    Log.e("ERROR",""+t.getMessage());
                }
            });
        }
        else Log.e("INFO","TABLAS DE ZONAS CON DATOS :)");

        //------------------------------------------------------------------------------------------
        //OBTIENE TODOS LOS DATOS DE CATEGORIAS (FACTORES/VARIABLES)
        final FactorDbHelper factorDbHelper = new FactorDbHelper(this);
        final VariableDbHelper variableDbHelper = new VariableDbHelper(this);

        int sizeFactor = factorDbHelper.getSizeDatabase();
        int sizeVariable = variableDbHelper.getSizeDatabase();

        if(sizeFactor == 0 && sizeVariable == 0){
            Log.e("INFO","TABLAS DE CATEGORIAS VACIAS :( - ESCRIBIENDO... :)");
            //--------------------------------------------------------------------------------------
            //OBTIENE LAS CATEGORIAS
            Call<CategoriaResponse> call3 = AylluApiAdapter.getApiService("CATEGORIAS").getCategoria();
            call3.enqueue(new Callback<CategoriaResponse>() {
                @Override
                public void onResponse(Call<CategoriaResponse> call, Response<CategoriaResponse> response) {
                    if (response.isSuccessful()) {
                        categorias = response.body().getCategorias();
                        Log.e("INFO:","Se cargo toda la información de las categorias");

                        factorDbHelper.saveFactorList(categorias.get(0).getFactores());
                        variableDbHelper.saveVariableList(categorias.get(0).getVariables());
                    }
                }

                @Override
                public void onFailure(Call<CategoriaResponse> call, Throwable t) {
                    Log.e("ERROR",""+t.getMessage());
                }
            });
        }
        else Log.e("INFO","TABLAS DE CATEGORIAS CON DATOS :)");
    }
    /**
     * =============================================================================================
     * METODO:
     **/

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        else if(dir!= null && dir.isFile()) return dir.delete();
        else return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
