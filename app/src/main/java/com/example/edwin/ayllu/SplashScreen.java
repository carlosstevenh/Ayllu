package com.example.edwin.ayllu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;

import com.example.edwin.ayllu.administrador.Administrador;
import com.example.edwin.ayllu.domain.AreaDbHelper;
import com.example.edwin.ayllu.domain.Categoria;
import com.example.edwin.ayllu.domain.FactorDbHelper;
import com.example.edwin.ayllu.domain.PaisDbHelper;
import com.example.edwin.ayllu.domain.SeccionDbHelper;
import com.example.edwin.ayllu.domain.SubtramoDbHelper;
import com.example.edwin.ayllu.domain.Task;
import com.example.edwin.ayllu.domain.TaskDbHelper;
import com.example.edwin.ayllu.domain.TramoDbHelper;
import com.example.edwin.ayllu.domain.VariableDbHelper;
import com.example.edwin.ayllu.domain.Zona;
import com.example.edwin.ayllu.io.AylluApiAdapter;
import com.example.edwin.ayllu.io.AylluApiService;
import com.example.edwin.ayllu.io.ApiConstants;
import com.example.edwin.ayllu.io.model.CategoriaResponse;
import com.example.edwin.ayllu.io.model.ZonaResponse;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class SplashScreen extends Activity {
    String tipo;
    ArrayList<Zona> zonas = new ArrayList<>();
    ArrayList<Categoria> categorias = new ArrayList<>();

    @Override
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

        //------------------------------------------------------------------------------------------
        //REGISTRA MONITOREOS QUE ESTAN ALMACENADOS EN EL MOVIL
        TaskDbHelper taskDbHelper = new TaskDbHelper(this);
        Cursor cursor = taskDbHelper.generateQuery("SELECT * FROM ");
        int con = 0;

        if (cursor.moveToFirst()) {
            if (wifiConected()) {
                do {
                    //Obtengo cada uno de los monitoreos de la tabla Task
                    Task tk = new Task(
                            cursor.getString(1), cursor.getString(2), cursor.getString(3),
                            Integer.parseInt(cursor.getString(4)),
                            Integer.parseInt(cursor.getString(5)),
                            cursor.getString(6), cursor.getString(7), cursor.getString(8),
                            Integer.parseInt(cursor.getString(9)),
                            Integer.parseInt(cursor.getString(10)),
                            cursor.getString(11),cursor.getString(12),cursor.getString(13));
                    con ++;

                    //Subo el monitoreo al servidor
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                    httpClient.addInterceptor(logging);

                    //Variable para almacenar la foto
                    PostClient service1 = PostClient.retrofit.create(PostClient.class);
                    File file = null;
                    File file2 = null;
                    File file3 = null;
                    String foto = cursor.getString(11);
                    String foto2 = cursor.getString(12);
                    String foto3 = cursor.getString(13);
                    File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Ayllu");
                    imagesFolder.mkdirs();
                    //se carga la foto al la variable

                    if(foto2.equals("null")){
                        file = new File(imagesFolder,foto);
                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("fotoUp", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                        Call<String> call1 = service1.uploadAttachment(filePart);
                        call1.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                Log.e("Fotos1 ","registro exitoso");
                                //Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });
                    }
                    else if (foto3.equals("null")){
                        file = new File(imagesFolder,foto);
                        file2 = new File(imagesFolder,foto2);

                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("fotoUp", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                        MultipartBody.Part filePart2 = MultipartBody.Part.createFormData("fotoUp2", file2.getName(), RequestBody.create(MediaType.parse("image/*"), file2));

                        Call<String>call1 = service1.upLoad2(filePart,filePart2);
                        call1.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                Log.e("Fotos2 ","registro exitoso");
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });
                    }
                    else if (!foto.equals("null") && !foto2.equals("null") && !foto3.equals("null")){
                        file = new File(imagesFolder,foto);
                        file2 = new File(imagesFolder,foto2);
                        file3 = new File(imagesFolder,foto3);

                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("fotoUp", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                        MultipartBody.Part filePart2 = MultipartBody.Part.createFormData("fotoUp2", file2.getName(), RequestBody.create(MediaType.parse("image/*"), file2));
                        MultipartBody.Part filePart3 = MultipartBody.Part.createFormData("fotoUp3", file3.getName(), RequestBody.create(MediaType.parse("image/*"), file3));

                        Call<String>call1 = service1.upLoad3(filePart,filePart2,filePart3);
                        call1.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                Log.e("Fotos3 ","registro exitoso");
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });
                    }

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(ApiConstants.URL_API_AYLLU)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(httpClient.build())
                            .build();

                    AylluApiService service = retrofit.create(AylluApiService.class);
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
                //Elimino la base de datos Task
                taskDbHelper.deleteDataBase();
            }
        }
    }

    //==============================================================================================
    //METODO: Verifica la conexion a internet
    protected Boolean wifiConected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected() )return true;
        else return false;
    }
}
