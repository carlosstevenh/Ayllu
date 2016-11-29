package com.example.edwin.ayllu;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.edwin.ayllu.Adiminstrador.Administrador;
import com.example.edwin.ayllu.domain.Usuario;

public class SplashScreen extends Activity {
    String tipo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        AdminSQLite admin = new AdminSQLite(getApplicationContext(),"login", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor datos = bd.rawQuery(
                "select * from "+ admin.TABLENAME , null);
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
                        Intent intent = new Intent(SplashScreen.this, Administrador.class);
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
                //bd.execSQL("TRUNCATE TABLE "+admin.TABLENAME, null);
                bd1.close();

            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);


        }
        bd.close();
    }
}
