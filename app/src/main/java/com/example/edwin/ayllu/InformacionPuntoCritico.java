package com.example.edwin.ayllu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.MonitoreoGeneral;
import com.example.edwin.ayllu.domain.Prueba;
import com.example.edwin.ayllu.domain.PuntoCritico;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformacionPuntoCritico extends AppCompatActivity {
    private String pa,fm;
    private TextView paf,fec,nom,pais,tra,sub,sec,are,fac,var,lon,lat;
    private ArrayList<MonitoreoGeneral> mg;
    private ImageView foto1,foto2,foto3;
    FloatingActionButton fab_pruebas, fab_graficas;
    private String URL = "http://138.68.40.165/camara/imagenes/";
    private int tamamo = 0;
    private ArrayList<Prueba> pruebas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_punto_critico);

        Bundle bundle = getIntent().getExtras();
        pa = bundle.getString("pa");
        fm = bundle.getString("fm");
        Log.i("TAG", "punto afecation: " + pa);

        //se define los elementos de la vista
        fec = (TextView) findViewById(R.id.fec);
        nom = (TextView) findViewById(R.id.user);
        pais = (TextView) findViewById(R.id.pais);
        tra = (TextView) findViewById(R.id.tramo);
        sub = (TextView) findViewById(R.id.subtramo);
        sec = (TextView) findViewById(R.id.seccion);
        are = (TextView) findViewById(R.id.area);
        fac = (TextView) findViewById(R.id.factor);
        var = (TextView) findViewById(R.id.variable);
        lon = (TextView) findViewById(R.id.longitud);
        lat = (TextView) findViewById(R.id.latitud);

        foto1 = (ImageView) findViewById(R.id.foto1);
        foto2 = (ImageView) findViewById(R.id.foto2);
        foto3 = (ImageView) findViewById(R.id.foto3);


            foto1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String foto = URL;
                    if(tamamo > 0){
                        foto += pruebas.get(0).getNombre();
                        createDialogImage(foto).show();
                    }

                }
            });


            foto2.setOnClickListener(new View.OnClickListener() {
                String foto = URL;
                @Override
                public void onClick(View view) {
                    String foto = URL;
                    if(tamamo > 1){
                        foto += pruebas.get(0).getNombre();
                        createDialogImage(foto).show();
                    }

                }
            });

            foto3.setOnClickListener(new View.OnClickListener() {
                String foto = URL;
                @Override
                public void onClick(View view) {
                    String foto = URL;
                    if(tamamo > 2){
                        foto += pruebas.get(0).getNombre();
                        createDialogImage(foto).show();
                    }

                }
            });


        //se realiza la peticion al servidor del nombre de las pruebas del monitoreo
        RestClient service1 = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<Prueba>> getPrueba = service1.getPrueba(pa,fm);
        getPrueba.enqueue(new retrofit2.Callback<ArrayList<Prueba>>() {
            @Override
            public void onResponse(Call<ArrayList<Prueba>> call, Response<ArrayList<Prueba>> response) {
                if(response.isSuccessful()){
                    ArrayList<Prueba> aux = response.body();
                    if(response.body().size() != 0){
                        pruebas = aux;
                        tamamo = aux.size();
                        if(tamamo<=3){
                            for (int i = 0; i < tamamo; i++){
                                String ruta = URL + aux.get(i).getNombre();
                                if(i+1 == 1) cargarImg(ruta,foto1);
                                if(i+1 == 2) cargarImg(ruta,foto2);
                                if(i+1 == 3) cargarImg(ruta,foto3);

                            }
                        }
                        else {
                            for (int i = 0; i < 3; i++){
                                String ruta = URL + aux.get(i).getNombre();
                                if(i+1 == 1) cargarImg(ruta,foto1);
                                if(i+1 == 2) cargarImg(ruta,foto2);
                                if(i+1 == 3) cargarImg(ruta,foto3);
                            }
                        }

                    }

                }
                else{
                    Log.e("URL:",URL);
                    Toast.makeText(
                            InformacionPuntoCritico.this,
                            getResources().getString(R.string.noSeEncontraronDatos),
                            Toast.LENGTH_LONG)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Prueba>> call, Throwable t) {
                Toast.makeText(
                        InformacionPuntoCritico.this,
                        getResources().getString(R.string.noSePudoConectarServidor),
                        Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        });

        //se hace la peticion al servidor de la informacion general de un monitoreo
        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<MonitoreoGeneral>> mon = service.informacion(pa,fm);
        mon.enqueue(new Callback<ArrayList<MonitoreoGeneral>>() {
            @Override
            public void onResponse(Call<ArrayList<MonitoreoGeneral>> call, Response<ArrayList<MonitoreoGeneral>> response) {
                if (response.isSuccessful()) {
                    mg = response.body();

                    MonitoreoGeneral aux = mg.get(0);
                    fec.setText("  "+aux.getFecha());
                    nom.setText("  "+aux.getMonitor());
                    pais.setText("  "+aux.getPais());
                    tra.setText("  "+aux.getTramo());
                    sub.setText("  "+aux.getSubtramo());
                    sec.setText("  "+aux.getSeccion());
                    are.setText("  "+aux.getArea());
                    fac.setText("  "+aux.getFactor());
                    var.setText("  "+aux.getVariable());
                    lon.setText("  "+aux.getLongitud());
                    lat.setText("  "+aux.getLatitud());

                }
                else{
                    Toast.makeText(
                            InformacionPuntoCritico.this,
                            getResources().getString(R.string.noSeEncontraronDatos),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MonitoreoGeneral>> call, Throwable t) {
                Toast.makeText(
                        InformacionPuntoCritico.this,
                        getResources().getString(R.string.noSePudoConectarServidor),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        //se define un variable de tipo button el cual sera la encargada de manejar los eventos del boton analisis
        fab_graficas = (FloatingActionButton) findViewById(R.id.fab_graficas);

        //metodo encargado de llamar a la actividad encargada de realizar la grafica estadistica
        fab_graficas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle parametro = new Bundle();
                parametro.putString("pa",pa);
                Intent intent = new Intent(InformacionPuntoCritico.this,ActividadEstadisticaPuntoAfactacion.class);
                intent.putExtras(parametro);
                startActivity(intent);
            }
        });

    }
    //METODO: Encargado de obtener las imagenes en miniatura del servidor
    void cargarImg(String foto, ImageView img){

        //final ProgressDialog loading = ProgressDialog.show(this, getResources().getString(R.string.procesando),getResources().getString(R.string.esperar),false,false);
        Picasso.with(InformacionPuntoCritico.this).
                load(foto).into(img, new com.squareup.picasso.Callback() {

            @Override
            public void onSuccess() {
                //loading.dismiss();
            }

            @Override
            public void onError() {
                //loading.dismiss();
                Toast.makeText(
                        InformacionPuntoCritico.this,
                        getResources().getString(R.string.noEcontroPrueba),
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    /**
     * =============================================================================================
     * METODO: Mostrar imagenes ampliadas
     **/
    public AlertDialog createDialogImage(String file) {
        Log.e("Entro","aqui");
        final AlertDialog.Builder builder = new AlertDialog.Builder(InformacionPuntoCritico.this);
        LayoutInflater inflater = InformacionPuntoCritico.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_image, null);
        builder.setView(v);

        ImageView img = (ImageView) v.findViewById(R.id.dialogImage);

        Picasso.with(InformacionPuntoCritico.this).
                load(file).into(img, new com.squareup.picasso.Callback() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });

        return builder.create();
    }
}
