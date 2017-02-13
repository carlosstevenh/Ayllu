package com.example.edwin.ayllu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.Task;
import com.example.edwin.ayllu.io.ApiAylluService;
import com.example.edwin.ayllu.io.ApiConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MonitoringRegistrationForm3Activity extends AppCompatActivity implements View.OnClickListener{

    EditText et_fecha, et_porcentaje, et_frecuencia;
    RadioGroup rg_reper1, rg_reper2, rg_origen;
    FloatingActionButton fb_regMon;

    String monitor = "", area = "", variable = "";
    int longitud, latitud;
    int[] repercusiones = {1,0,1,0};
    String origen  = "10";
    String fecha = "", porcentaje = "", frecuencia="";

    //camara
    private ImageButton upload;
    private String foto;
    private File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_registration_form3);

        et_fecha = (EditText) findViewById(R.id.et_fecha);
        et_porcentaje = (EditText) findViewById(R.id.et_porcentaje);
        et_frecuencia = (EditText) findViewById(R.id.et_frecuencia);

        rg_reper1 = (RadioGroup) findViewById(R.id.rg_repercusiones1);
        rg_reper2 = (RadioGroup) findViewById(R.id.rg_repercusiones2);
        rg_origen = (RadioGroup) findViewById(R.id.rg_origen);

        fb_regMon = (FloatingActionButton) findViewById(R.id.fab_regMon);
        fb_regMon.setOnClickListener(this);

        upload = (ImageButton) findViewById(R.id.upload);

        Intent intent = getIntent();

        monitor = intent.getStringExtra("MONITOR");
        area = intent.getStringExtra("AREA");
        variable = intent.getStringExtra("VARIABLE");
        longitud = Integer.parseInt(intent.getStringExtra("LONGITUD"));
        latitud = Integer.parseInt(intent.getStringExtra("LATITUD"));

        monitor = intent.getStringExtra("MONITOR");
        Toast.makeText(
                MonitoringRegistrationForm3Activity.this,
                ""+monitor,
                Toast.LENGTH_SHORT)
                .show();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCamara();
            }
        });

    }
    public void getCamara(){

        Intent cameraIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(
                Environment.getExternalStorageDirectory(), "Ayllu");
        imagesFolder.mkdirs();
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());
        foto = format+".jpg";
        file = new File(imagesFolder, foto);
        Uri uriSavedImage = Uri.fromFile(file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(cameraIntent, 1);
    }

    public void comprobarRepercusiones1(View view){
        switch (rg_reper1.getCheckedRadioButtonId()){
            case R.id.rb_positive:
                repercusiones[0] = 1;
                repercusiones[1] = 0;
                break;
            case R.id.rb_negative:
                repercusiones[0] = 0;
                repercusiones[1] = 1;
                break;
        }
    }

    public void comprobarRepercusiones2(View view){
        switch (rg_reper2.getCheckedRadioButtonId()){
            case R.id.rb_current:
                repercusiones[2] = 1;
                repercusiones[3] = 0;
                break;
            case R.id.rb_potencial:
                repercusiones[2] = 0;
                repercusiones[3] = 1;
                break;
        }
    }

    public void comprobarOrigen(View view){
        switch (rg_origen.getCheckedRadioButtonId()){
            case R.id.rb_interno:
                origen = "10";
                break;
            case R.id.rb_externo:
                origen = "01";
                break;
        }
    }

    public boolean comprobarCajasTexto(View view){
        //aqui

        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
        String format = s.format(new Date());
        fecha = format;
        //fecha = et_fecha.getText().toString();
        porcentaje = et_porcentaje.getText().toString();
        frecuencia = et_frecuencia.getText().toString();

        if(fecha.equals("") || porcentaje.equals("") || frecuencia.equals("")) return false;
        else return true;
    }
    
    @Override
    public void onClick(View view) {
        Toast login = Toast.makeText(getApplicationContext(),
                "Registro exitoso", Toast.LENGTH_SHORT);
        switch (view.getId()){
            case R.id.fab_regMon:
                comprobarRepercusiones1(view);
                comprobarRepercusiones2(view);
                comprobarOrigen(view);
                if(comprobarCajasTexto(view)){
                    int por = Integer.parseInt(porcentaje);
                    int fre = Integer.parseInt(frecuencia);

                    String rep = "";
                    for (int i =0; i<4; i++) rep += ""+repercusiones[i];

                    Task tk = new Task(monitor, variable, area, latitud, longitud, fecha, rep, origen, por, fre,file.getName());

                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    // set your desired log level
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                    // add your other interceptors …

                    //upload image
                    if(file != null){
                        PostClient service1 = PostClient.retrofit.create(PostClient.class);
                        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("fotoUp", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                        Call<String> call1 = service1.uploadAttachment(filePart);
                        call1.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                Toast login = Toast.makeText(getApplicationContext(),
                                        "Registro exitoso", Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });
                    }


                    // add logging as last interceptor

                    //upLoadImg();
                    httpClient.addInterceptor(logging);  // <-- this is the important line!

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
                            Intent intent = new Intent(MonitoringRegistrationForm3Activity.this, MonitorMenuActivity.class);
                            intent.putExtra("MONITOR",monitor);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Task> call, Throwable t) {

                        }
                    });

            }else createSimpleDialog("Existen campos sin llenar", "ERROR FORMULARIO INCOMPLETO").show();
                break;
            default:
                break;
        }
    }

    public AlertDialog createSimpleDialog(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        });

        return builder.create();
    }
}
