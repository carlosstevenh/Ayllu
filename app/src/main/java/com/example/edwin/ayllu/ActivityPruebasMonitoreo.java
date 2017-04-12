package com.example.edwin.ayllu;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.Prueba;
import com.example.edwin.ayllu.io.ApiConstants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;


public class ActivityPruebasMonitoreo extends AppCompatActivity {
    private String pa,fm;
    private ImageView imageView;
    private String URL = ApiConstants.URL_IMG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pruebas_monitoreo);

        imageView = (ImageView) findViewById(R.id.myImageView);
        Bundle bundle = getIntent().getExtras();
        pa = bundle.getString("pa");
        fm = bundle.getString("fm");
        //File file;

        //peticion que se realiza al servidor para obtener el nombre de la prueba del monitoreo
        final ProgressDialog loading = ProgressDialog.show(this, getResources().getString(R.string.procesando),getResources().getString(R.string.esperar),false,false);
        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<Prueba>> getPrueba = service.getPrueba(pa,fm);
        getPrueba.enqueue(new retrofit2.Callback<ArrayList<Prueba>>() {
            @Override
            public void onResponse(Call<ArrayList<Prueba>> call, Response<ArrayList<Prueba>> response) {
                if(response.isSuccessful()){
                    ArrayList<Prueba> aux = response.body();
                    if(response.body().size() != 0){
                        URL += aux.get(0).getPrueba1();
                    }

                    Log.e("URL:",URL);
                    Picasso.with(ActivityPruebasMonitoreo.this).
                            load(URL).into(imageView, new Callback() {

                                @Override
                                public void onSuccess() {
                                    loading.dismiss();
                                }

                                @Override
                                public void onError() {
                                    loading.dismiss();
                                    Toast.makeText(
                                            ActivityPruebasMonitoreo.this,
                                            getResources().getString(R.string.noEcontroPrueba),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                    finish();
                                }
                            });
                }
                else{
                    Log.e("URL:",URL);
                    loading.dismiss();
                    Toast.makeText(
                            ActivityPruebasMonitoreo.this,
                            getResources().getString(R.string.noSeEncontraronDatos),
                            Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Prueba>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(
                        ActivityPruebasMonitoreo.this,
                        getResources().getString(R.string.noSePudoConectarServidor),
                        Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        });

    }
}
