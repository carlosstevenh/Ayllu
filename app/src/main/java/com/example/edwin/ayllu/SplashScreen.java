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

    /**
     * =============================================================================================
     * METODO: BLOQUEA EL EVENTO DE RETROCESO DEL USUARIO
     **/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
