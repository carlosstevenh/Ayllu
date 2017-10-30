package com.qhapaq.nan.ayllu.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Bundle;
import android.view.KeyEvent;

import com.qhapaq.nan.ayllu.R;
import com.qhapaq.nan.ayllu.domain.usuario.UsuarioDbHelper;

public class SplashScreen extends Activity {
    String tipo;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        UsuarioDbHelper usuarioDbHelper = new UsuarioDbHelper(this);
        Cursor cursor = usuarioDbHelper.generateQuery("SELECT * FROM ");

        if (cursor.moveToFirst()) {
            tipo = cursor.getString(5);

            switch (tipo){
                case "A":
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashScreen.this, AdminActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    },2000);
                    break;
                case "M":
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashScreen.this, MonitorMenuActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    },2000);
                    break;
            }
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }

        usuarioDbHelper.close();
        cursor.close();
    }

    /**
     * =============================================================================================
     * METODO: BLOQUEA EL EVENTO DE RETROCESO DEL USUARIO
     **/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 || super.onKeyDown(keyCode, event);
    }
}
