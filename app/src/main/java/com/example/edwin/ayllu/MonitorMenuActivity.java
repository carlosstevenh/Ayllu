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
import com.example.edwin.ayllu.domain.Task;
import com.example.edwin.ayllu.domain.TaskContract;
import com.example.edwin.ayllu.domain.TaskDbHelper;
import com.example.edwin.ayllu.io.ApiConstants;
import com.example.edwin.ayllu.io.AylluApiService;
import com.example.edwin.ayllu.io.PostClient;

import java.io.File;
import java.util.ArrayList;

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

public class MonitorMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private int[] layouts;
    private Button btnSkip, btnNext;

    Cursor cursor, cursor1, cursor2;
    String monitor = "", pais = "", tipo_usu = "A";

    //Progresbar
    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private int sizeImg = 0;
    private int portj = 0;
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

        // prepare for a progress bar dialog
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Subiendo Datos ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();

        if (cursor2.moveToFirst() && wifiConected()){
            createSimpleDialog(getResources().getString(R.string.monitoring_menu_message_upload),
                    getResources().getString(R.string.titleDetailMonitoringDialog), "UPLOAD").show();

        }
    }
    /**
     * =============================================================================================
     * METODO: Registrar Monitoreos en estado Offline
     */
    private void uploadMonitoring (Cursor cur_info, final Cursor cur_img, View view){
        //------------------------------------------------------------------------------------------
        //REGISTRA MONITOREOS QUE ESTAN ALMACENADOS EN EL MOVIL
        //reset progress bar status
        progressBarStatus = 0;
        sizeImg  = cur_img.getCount();
        portj = 100/sizeImg;

        new Thread(new Runnable() {
            public void run() {
                do {
                    progressBarStatus += uploadImages(cur_img);

                    //Dormir por 1 segundo
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //Actualizar el progressBar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                } while (progressBarStatus < 100 && cur_img.moveToNext());

                // Imagenes subidas
                if (progressBarStatus >= 100) {

                    //Dormir por dos segundos para mirar el 100%
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //Cerrar el progresBar
                    progressBar.dismiss();
                }
            }
        }).start();

        if (cur_info.moveToFirst()){
            do {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                httpClient.addInterceptor(logging);
                PostClient service1 = PostClient.retrofit.create(PostClient.class);

                //Obtengo cada uno de los monitoreos de la tabla Task
                Task tk = new Task(
                        cur_info.getString(1), cur_info.getString(2), cur_info.getString(3),
                        cur_info.getString(4), cur_info.getString(5),
                        cur_info.getString(6), cur_info.getString(7), cur_info.getString(8),
                        Integer.parseInt(cur_info.getString(9)),
                        Integer.parseInt(cur_info.getString(10)),
                        cur_info.getString(11), cur_info.getString(12),cur_info.getString(13),
                        cur_info.getString(14));

                final int numon1 = cur_info.getInt(0);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ApiConstants.URL_API_AYLLU)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .client(httpClient.build())
                        .build();

                if(!cur_info.getString(11).equals("null")) {
                    if(tk.getTipo().equals("N")){
                        AylluApiService service = retrofit.create(AylluApiService.class);
                        Call<Task> call = service.registrarPunto(tk);
                        call.enqueue(new Callback<Task>() {
                            @Override
                            public void onResponse(Call<Task> call, Response<Task> response) {
                                if(response.isSuccessful()){
                                    String[] cond = new String[]{numon1  + ""};
                                    taskDbHelper.generateConditionalUpdate(cond, new String[]{TaskContract.TaskEntry.NOMBRE, TaskContract.TaskEntry._ID});
                                }
                            }

                            @Override
                            public void onFailure(Call<Task> call, Throwable t) {

                            }
                        });
                    }
                    else if (tk.getTipo().equals("M")){
                        AylluApiService service = retrofit.create(AylluApiService.class);
                        Call<Task> call = service.monitorearPunto(tk);
                        call.enqueue(new Callback<Task>() {
                            @Override
                            public void onResponse(Call<Task> call, Response<Task> response) {
                                if (response.isSuccessful()){
                                    String[] cond = new String[]{numon1  + ""};
                                    taskDbHelper.generateConditionalUpdate(cond, new String[]{TaskContract.TaskEntry.NOMBRE, TaskContract.TaskEntry._ID});
                                }
                            }

                            @Override
                            public void onFailure(Call<Task> call, Throwable t) {

                            }
                        });
                    }
                }
            } while (cur_info.moveToNext());
        }
    }

    /**
     * =============================================================================================
     * METODO: Subir Fotografias
     * **/
    private int uploadImages (Cursor current_cursor){
        ArrayList<String> nameFiles = new ArrayList<>();
        for (int j = 1; j < 4; j++) nameFiles.add(current_cursor.getString(j));
        final int numon = current_cursor.getInt(0);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        PostClient service1 = PostClient.retrofit.create(PostClient.class);

        File file;
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Ayllu");
        imagesFolder.mkdirs();

        for (int i = 0; i<nameFiles.size(); i++){
            if(!nameFiles.get(i).equals("null")){
                file = new File(imagesFolder,nameFiles.get(i));
                final int con = i;

                MultipartBody.Part filePart = MultipartBody.Part.createFormData("fotoUp", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                Call<String> call1 = service1.uploadAttachment(filePart);
                call1.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()){
                            String[] cond = new String[]{numon+""};

                            if (con == 0) imagenDbHelper.generateConditionalUpdate(cond, new String[]{ImagenContract.ImagenEntry.FOTOGRAFIA1, ImagenContract.ImagenEntry._ID});
                            if (con == 1) imagenDbHelper.generateConditionalUpdate(cond, new String[]{ImagenContract.ImagenEntry.FOTOGRAFIA2, ImagenContract.ImagenEntry._ID});
                            if (con == 2) imagenDbHelper.generateConditionalUpdate(cond, new String[]{ImagenContract.ImagenEntry.FOTOGRAFIA3, ImagenContract.ImagenEntry._ID});
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        }
        return portj;
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
        switch (view.getId()) {
            case R.id.btn_next:
                /*Cerrar Sesión
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();

                AdminSQLite admin = new AdminSQLite(getApplicationContext(), "login", null, 1);
                SQLiteDatabase bd = admin.getWritableDatabase();
                bd.delete(admin.TABLENAME, null, null);
                bd.close();*/
                break;
            //Lanza una activity en el caso en el que el usuario haya elegido un Slide
            case R.id.btn_skip:
                int current = getItem(0);
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
                            uploadMonitoring(cursor1, cursor2, getCurrentFocus());
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
