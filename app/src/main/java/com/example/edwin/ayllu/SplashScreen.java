package com.example.edwin.ayllu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.edwin.ayllu.Adiminstrador.Administrador;
import com.example.edwin.ayllu.domain.Task;
import com.example.edwin.ayllu.domain.TaskDbHelper;
import com.example.edwin.ayllu.domain.Usuario;
import com.example.edwin.ayllu.io.ApiAylluService;
import com.example.edwin.ayllu.io.ApiConstants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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

    @Override
    protected void onStart() {
        super.onStart();

        TaskDbHelper taskDbHelper = new TaskDbHelper(this);
        final Cursor cursor = taskDbHelper.generateQuery("SELECT * FROM ");
        int con = 0;

        if (cursor.moveToFirst()) {

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifi.isConnected() || mobile.isConnected()) {

                if (cursor.moveToFirst()) {
                    do {
                        Task tk = new Task(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                                Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)), cursor.getString(6),
                                cursor.getString(7), cursor.getString(8), Integer.parseInt(cursor.getString(9)), Integer.parseInt(cursor.getString(10)));
                        con ++;
                        Log.i("INFORMACION","DATO ["+con+"]: "+tk.getFecha()+tk.getMonitor());


                        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                        httpClient.addInterceptor(logging);

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(ApiConstants.URL_API_AYLLU)
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                .client(httpClient.build())
                                .build();

                        ApiAylluService service = retrofit.create(ApiAylluService.class);
                        Call<Task> call = service.registrarPunto(tk);
                        call.enqueue(new Callback<Task>() {
                            @Override
                            public void onResponse(Call<Task> call, Response<Task> response) {

                            }
                            @Override
                            public void onFailure(Call<Task> call, Throwable t) {

                            }
                        });

                    } while (cursor.moveToNext());
                    taskDbHelper.deleteDataBase();
                }

            }
        }

        Log.e("Paso por aqui","jajaja");
    }
}
