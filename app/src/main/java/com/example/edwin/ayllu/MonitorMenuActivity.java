package com.example.edwin.ayllu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.ImagenContract;
import com.example.edwin.ayllu.domain.ImagenDbHelper;
import com.example.edwin.ayllu.domain.Mensaje;
import com.example.edwin.ayllu.domain.Task;
import com.example.edwin.ayllu.domain.TaskContract;
import com.example.edwin.ayllu.domain.TaskDbHelper;
import com.example.edwin.ayllu.io.ApiConstants;
import com.example.edwin.ayllu.io.AylluApiService;
import com.example.edwin.ayllu.io.PostClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
import static com.example.edwin.ayllu.domain.ImagenContract.ImagenEntry;
import static com.example.edwin.ayllu.domain.TaskContract.TaskEntry;

public class MonitorMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private int[] layouts;
    private Button btnSkip, btnNext;

    Cursor cursor1, cursor2;
    String monitor = "", pais = "";

    //Progresbar
    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private int sizeImg = 0;
    private int sizeData = 0;
    private int portj = 0;
    private int dif = 0;
    private Handler progressBarHandler = new Handler();

    //Bases de Datos
    private TaskDbHelper taskDbHelper;
    private ImagenDbHelper imagenDbHelper;

    /**
     * =============================================================================================
     * METODO: Establece todas las acciones a realizar una vez creado el Activity
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtiene el codigo y pais del monitor que ha ingresado
        AdminSQLite admin = new AdminSQLite(getApplicationContext(), "login", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        //Prepara la sentencia SQL para la consulta en la Tabla de usuarios
        Cursor cursor = bd.rawQuery("SELECT codigo, pais FROM login LIMIT 1", null);
        cursor.moveToFirst();
        monitor = cursor.getString(0);
        pais = cursor.getString(1);
        cursor.close();

        //Aplicando transparencia sobre el Toolbar
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_monitor_menu);

        //Referenciando los views en la interfaz
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);


        //Creando un vector de IDs de los layouts para el menu del monitor
        layouts = new int[]{
                R.layout.slide_register_monitoring,
                R.layout.slide_institutional_response,
                R.layout.slide_statistical_maps,
                R.layout.slide_mobile_settings};

        //Añadiendo los bottom dots
        addBottomDots(0);

        //Llamando al metodo para aplicar transparencia sobre el toolbar
        changeStatusBarColor();

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        //Consultar si existen monitoreos en el celular
        taskDbHelper = new TaskDbHelper(this);
        imagenDbHelper = new ImagenDbHelper(this);

        cursor1 = taskDbHelper.generateQuery("SELECT * FROM ");
        cursor2 = imagenDbHelper.generateQuery("SELECT * FROM ");

        Log.e("TAMAÑO TABLA IMAGENES", cursor2.getCount()+"");
        Log.e("TAMAÑO TABLA TASK", cursor1.getCount()+"");

        // prepare for a progress bar dialog
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Subiendo Datos...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();

        Log.e("TOTAL IMG",imagenDbHelper.getSizeDatabase()+"");
        Log.e("TOTAL DATOS",taskDbHelper.getSizeDatabase()+"");

        if (taskDbHelper.getSizeDatabase() > 0 && imagenDbHelper.getSizeDatabase() > 0 && wifiConected()){
            createSimpleDialog(getResources().getString(R.string.monitoring_menu_message_upload),
                    getResources().getString(R.string.titleDetailMonitoringDialog), "UPLOAD").show();

        }
        else progressBar.dismiss();
    }
    /**
     * =============================================================================================
     * METODO: Registrar Monitoreos en estado Offline
     */
    private void uploadMonitoring (final Cursor cur_info, final Cursor cur_img){
        //------------------------------------------------------------------------------------------
        //REGISTRA MONITOREOS QUE ESTAN ALMACENADOS EN EL MOVIL
        //reset progress bar status
        progressBarStatus = 0;
        //Obtenemos el total de registros a subir
        sizeImg  = imagenDbHelper.getSizeDatabase();
        sizeData = taskDbHelper.getSizeDatabase();
        sizeImg += sizeData;

        Log.e("TAMAÑO", sizeImg+"");

        portj = 100/sizeImg;
        dif = 100 - (portj * sizeImg);

        new Thread(new Runnable() {
            public void run() {
                //----------------------------------------------------------------------------------
                //REGISTRAMOS LA FOTOGRAFIAS
                if (cur_img.moveToFirst()){
                    do {
                        for (int i = 1; i < 4; i++){
                            // Analizamos si la fotografia ya se ha subido
                            if (!cur_img.getString(i).equals("null")){
                                //Subimos la información al servidor e incrementamos el progresbar
                                progressBarStatus += uploadImages(cur_img, i);
                                //Actualizar el progressBar
                                progressBarHandler.post(new Runnable() {
                                    public void run() {
                                        progressBar.setProgress(progressBarStatus);
                                    }
                                });
                            }
                        }
                    } while (cur_img.moveToNext());
                }
                //----------------------------------------------------------------------------------
                //REGISTRAMOS LOS DATOS
                if (cur_info.moveToFirst()){
                    do {
                        // Analizamos si el dato ya se ha registrado
                        if (!cur_info.getString(11).equals("null")){
                            //Subimos la información al servidor e incrementamos el progresbar
                            progressBarStatus += uploadData(cur_info);
                            // Comprobamos si se completaron los registro y completamos el progresbar
                            if ((portj*sizeImg) == progressBarStatus) progressBarStatus += dif;
                            // Actualizamos el status del progresbar
                            progressBarHandler.post(new Runnable() {
                                public void run() {
                                    progressBar.setProgress(progressBarStatus);
                                }
                            });
                        }
                    } while (cur_info.moveToNext());
                }
                //----------------------------------------------------------------------------------
                // Terminamos el dialogo del progresbar
                if (progressBarStatus >= 100) {
                    //Dormir por dos segundos para mirar el 100%
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //Cerrar el progresBar
                    progressBar.dismiss();
                    //Eliminamos los registros de las imagenes que se han subido
                    String[] cond = new String[] {"null", "null", "null"};
                    String[] atributs = new String[] {ImagenEntry.FOTOGRAFIA1, ImagenEntry.FOTOGRAFIA2, ImagenEntry.FOTOGRAFIA3};
                    imagenDbHelper.generateConditionalDelete(cond, atributs);

                    //Eliminamos los registros de los datos que se han subido
                    cond = new String[] {"null"};
                    atributs = new String[] {TaskEntry.NOMBRE};
                    taskDbHelper.generateConditionalDelete(cond, atributs);

                    //Cerramos la conexiones con la base de datos
                    cur_img.close();
                    cur_info.close();
                    imagenDbHelper.close();
                    taskDbHelper.close();
                }
            }
        }).start();
    }

    /**
     * =============================================================================================
     * METODO: Subir Fotografias
     * **/
    private int uploadImages (Cursor current_cursor, final int position){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);
        PostClient service1 = PostClient.retrofit.create(PostClient.class);

        File file;
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Ayllu");
        imagesFolder.mkdirs();
        file = new File(imagesFolder,current_cursor.getString(position));
        final int numon = current_cursor.getInt(0);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("fotoUp", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        Call<String> call1 = service1.uploadAttachment(filePart);
        String res;
        try {
            res = call1.execute().body();

            if (res.equals("[1]")){
                String[] cond = new String[]{numon+""};

                if (position == 1) imagenDbHelper.generateConditionalUpdate(cond, new String[]{ImagenContract.ImagenEntry.FOTOGRAFIA1, ImagenContract.ImagenEntry._ID});
                if (position == 2) imagenDbHelper.generateConditionalUpdate(cond, new String[]{ImagenContract.ImagenEntry.FOTOGRAFIA2, ImagenContract.ImagenEntry._ID});
                if (position == 3) imagenDbHelper.generateConditionalUpdate(cond, new String[]{ImagenContract.ImagenEntry.FOTOGRAFIA3, ImagenContract.ImagenEntry._ID});
            }

            return portj;
        } catch (IOException e) {
            e.printStackTrace();
            return portj;
        }
    }

    /**
     * =============================================================================================
     * METODO: Sube los datos al servidor
     */
    private int uploadData (Cursor current_data){
        //------------------------------------------------------------------------------------------
        //Preparamos el servicio de Retrofit
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
        //------------------------------------------------------------------------------------------
        //Obtengo cada uno de los monitoreos de la tabla Task
        Task tk = new Task(
                current_data.getString(1), current_data.getString(2), current_data.getString(3),
                current_data.getString(4), current_data.getString(5),
                current_data.getString(6), current_data.getString(7), current_data.getString(8),
                Integer.parseInt(current_data.getString(9)),
                Integer.parseInt(current_data.getString(10)),
                current_data.getString(11), current_data.getString(12),current_data.getString(13),
                current_data.getString(14));

        final int numon1 = current_data.getInt(0);
        //------------------------------------------------------------------------------------------
        //Comprobamos si el registro ya se subio y analizamos el tipo de registro
        if(tk.getTipo().equals("N")){
            AylluApiService service = retrofit.create(AylluApiService.class);
            Call<Mensaje> call = service.registrarPunto(tk);
            try {
                Mensaje mensaje;
                mensaje = call.execute().body();
                Log.e("MENSAJE REGISTRO",mensaje.getDescripcion());

                if (mensaje.getEstado().equals("1")){
                    String[] cond = new String[]{numon1  + ""};
                    taskDbHelper.generateConditionalUpdate(cond, new String[]{TaskContract.TaskEntry.NOMBRE, TaskContract.TaskEntry._ID});
                }

                return portj;
            } catch (IOException e) {
                e.printStackTrace();
                return portj;
            }
        }
        else {
            AylluApiService service = retrofit.create(AylluApiService.class);
            Call<Mensaje> call = service.monitorearPunto(tk);
            try {
                Mensaje mensaje;
                mensaje = call.execute().body();
                if (mensaje.getEstado().equals("1")){
                    String[] cond = new String[]{numon1  + ""};
                    taskDbHelper.generateConditionalUpdate(cond, new String[]{TaskContract.TaskEntry.NOMBRE, TaskContract.TaskEntry._ID});
                }

                return portj;
            } catch (IOException e) {
                e.printStackTrace();
                return portj;
            }
        }
    }

    /**
     * =============================================================================================
     * METODO: Añade los puntos al LinearLayout encargado de mostrar información de el Slide Actual
     **/
    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];

        //Limpiamos y recargamos todos lo views para los puntos
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            //Creamos un nuevo view para el punto con el caracter (.)
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorPrimary));
            dotsLayout.addView(dots[i]);
        }
        //Determinamos que punto esta activo segun el layout actual
        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.colorTextIcons));
    }

    /**
     * =============================================================================================
     * METODO: Obtenemos el Slide actual
     **/
    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    /**
     * =============================================================================================
     * METODO: Carga el Activity encargado de las estadisticas
     **/
    private void launchEstadisticas() {
        Intent intent = new Intent(MonitorMenuActivity.this, MenuGraficasEstadisticas.class);
        intent.putExtra("Monitor", monitor);
        startActivity(intent);
    }

    /**
     * =============================================================================================
     * METODO: Carga el Activity encargado de la respuesta institucional
     **/
    private void launchRespuestaInstitucional() {
        Intent intent = new Intent(MonitorMenuActivity.this, InstitutionalActivity.class);
        intent.putExtra("MONITOR", monitor);
        intent.putExtra("PAIS", pais);
        startActivity(intent);
    }

    /**
     * =============================================================================================
     * METODO: Carga el Activity encargado del Monitoreo
     **/
    private void launchRegistroMonitoreo() {
        Intent intent = new Intent(MonitorMenuActivity.this, MonitoringActivity.class);
        startActivity(intent);
    }

    /**
     * =============================================================================================
     * METODO: Carga el Activity encargada de la Configuración
     **/
    private void launchConfiguracion() {
        Intent intent = new Intent(MonitorMenuActivity.this, SettingsAppActivity.class);
        startActivity(intent);
    }

    /**
     * =============================================================================================
     * METODO: Carga un mensaje en caso de seleccionar el boton de retroceso
     **/
    @Override
    public void onBackPressed() {
        createSimpleDialog(getResources().getString(R.string.monitoring_menu_message_exit),
                getResources().getString(R.string.titleDetailMonitoringDialog), "EXIT").show();
    }

    /**
     * =============================================================================================
     * METODO: Agrega el escucha al view encargado de los Slides con sus respectivos metodos
     **/
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            if(arg0 == 3) viewPager.setCurrentItem(0);
        }
    };

    /**
     * =============================================================================================
     * METODO: Aplica transparencia sobre el Toolbar de la Activity
     **/
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * =============================================================================================
     * METODO: Decteca el evento onClick sobre los botones de la Interfaz
     **/
    @Override
    public void onClick(View view) {
        int current;
        switch (view.getId()) {
            case R.id.btn_next:
                current = getItem(+1);
                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    viewPager.setCurrentItem(0);
                }
                break;
            //Lanza una activity en el caso en el que el usuario haya elegido un Slide
            case R.id.btn_skip:
                current = getItem(0);
                if (current == 0) launchRegistroMonitoreo();
                else if (current == 1) launchRespuestaInstitucional();
                else if (current == 2) launchEstadisticas();
                else if (current == 3) launchConfiguracion();
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Funcionalidad en Construccion", Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * =============================================================================================
     * METODO: Encargado de adaptar el Slide al view del Activity
     **/
    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
    /**
     * =============================================================================================
     * METODO: Genera un Dialogo basico en pantalla
     **/
    public AlertDialog createSimpleDialog(String mensaje, String titulo, final String type) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_monitoring_info, null);
        builder.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title_dialog);
        TextView tvDescription = (TextView) v.findViewById(R.id.tv_description_dialog);

        tvTitle.setText(titulo);
        tvTitle.setCompoundDrawables(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_question), null, null, null);
        tvDescription.setText(mensaje);

        builder.setPositiveButton(getResources().getString(R.string.opcion_list_monitoring_dialog_acept),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.create().dismiss();
                        if (type.equals("EXIT")){
                            Intent intent = getIntent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                        }
                        else {
                            uploadMonitoring(cursor1, cursor2);
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.opcion_list_monitoring_dialog_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                                progressBar.dismiss();
                            }
                        });

        return builder.create();
    }

    //==============================================================================================
    //METODO: Verifica la conexion a internet
    protected Boolean wifiConected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnected() || mobile.isConnected())return true;
        else return false;
    }
}
