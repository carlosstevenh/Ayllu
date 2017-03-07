package com.example.edwin.ayllu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.edwin.ayllu.domain.FactorContract;
import com.example.edwin.ayllu.domain.FactorDbHelper;
import com.example.edwin.ayllu.domain.Task;
import com.example.edwin.ayllu.domain.TaskDbHelper;
import com.example.edwin.ayllu.domain.VariableContract;
import com.example.edwin.ayllu.domain.VariableDbHelper;
import com.example.edwin.ayllu.io.AylluApiService;
import com.example.edwin.ayllu.io.ApiConstants;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;

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

public class MonitoringRegistrationFormActivity extends AppCompatActivity implements View.OnClickListener, FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
    //VARIABLES: Componetes del Formulario de Registro
    Button btn_presencia, btn_frecuencia;
    RadioGroup rg_reper1, rg_reper2, rg_origen;
    FloatingActionButton fab_regMon, fab_point, fab_camera;
    FloatingActionButton fab_factor, fab_variable;
    FloatingActionsMenu fab_menu, fab_menu2;


    //VARIABLES: Control y Procesamiento de datos del formulario
    String punto_afectacion = "", area = "", monitor = "", op_reg;
    String origen = "10";
    String fecha = "";
    int longitud = 0, latitud = 0;
    int[] repercusiones = {1, 0, 1, 0};

    //VARIABLES: Control de la selección de opciones
    CharSequence[] items_factores, items_variables;
    private String[] opciones = {"0", "0"};
    private int[] pos = {-1, -1};
    String item = "";
    int i = 0;

    //VARIABLES: Control y Procesamiento de datos de la camara
    String foto;
    File file = null;

    //VARIABLES: Animaciones de los botones
    Interpolator interpolador;

    //VARIABLES: Control de datos fijos
    private FactorDbHelper factorDbHelper;
    private VariableDbHelper variableDbHelper;
    Cursor cursor;

    //VARIABLES: Control de datos para el (PORCENTAJE DE APARICIÓN) y (FRECUENCIA DE APARICIÓN)
    private CharSequence[] items_presencia = new CharSequence[]{"[1%-10%] RESTRINGIDA", "[11%-50%] LOCALIZADA", "[51%-90%] EXTENSIVA", "[91%-100%] EXTENDIDA"};
    private CharSequence[] items_frecuencia = new CharSequence[]{"UNA ÚNICA VEZ", "APARICIÓN INTERMITENTE", "PRESENCIA FRECUENTE"};
    private int[] seleccion_items = {1, 1};
    private int[] pos_seleccion = {-1, -1};

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_monitoring_registration_form);

        //------------------------------------------------------------------------------------------
        //Vincula los elementos de la interfaz del formulario con las variables
        btn_presencia = (Button) findViewById(R.id.btn_presencia);
        btn_frecuencia = (Button) findViewById(R.id.btn_frecuencia);

        rg_reper1 = (RadioGroup) findViewById(R.id.rg_repercusiones1);
        rg_reper2 = (RadioGroup) findViewById(R.id.rg_repercusiones2);
        rg_origen = (RadioGroup) findViewById(R.id.rg_origen);

        fab_regMon = (FloatingActionButton) findViewById(R.id.fab_regMon);
        fab_point = (FloatingActionButton) findViewById(R.id.fab_point);
        fab_camera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab_factor = (FloatingActionButton) findViewById(R.id.fab_factor);
        fab_variable = (FloatingActionButton) findViewById(R.id.fab_variable);
        fab_menu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        fab_menu2 = (FloatingActionsMenu) findViewById(R.id.fab_menu2);

        //------------------------------------------------------------------------------------------
        //Minimizamos los botones del menu
        fab_regMon.setScaleX(0);
        fab_regMon.setScaleY(0);
        fab_point.setScaleX(0);
        fab_point.setScaleY(0);
        fab_camera.setScaleX(0);
        fab_camera.setScaleY(0);

        fab_regMon.setEnabled(false);
        fab_point.setEnabled(false);
        fab_camera.setEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            interpolador = AnimationUtils.loadInterpolator(getBaseContext(),
                    android.R.interpolator.fast_out_slow_in);
        }

        //------------------------------------------------------------------------------------------
        //Añadimos el escucha del evento OnClick a cada uno de los botones del formulario
        btn_presencia.setOnClickListener(this);
        btn_frecuencia.setOnClickListener(this);

        fab_regMon.setOnClickListener(this);
        fab_point.setOnClickListener(this);
        fab_camera.setOnClickListener(this);
        fab_factor.setOnClickListener(this);
        fab_variable.setOnClickListener(this);
        fab_menu.setOnFloatingActionsMenuUpdateListener(this);
        fab_menu2.setOnFloatingActionsMenuUpdateListener(this);

        fab_variable.setEnabled(false);

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        fecha = s.format(new Date());

        //------------------------------------------------------------------------------------------
        //Se obtiene los parametros enviados por el Motor de busqueda basico

        Intent intent = getIntent();
        monitor = intent.getStringExtra("MONITOR");
        op_reg = intent.getStringExtra("OPCION");

        if (op_reg.equals("M")) {
            punto_afectacion = intent.getStringExtra("PUNTO");

            fab_factor.setScaleX(0);
            fab_factor.setScaleY(0);
            fab_variable.setScaleX(0);
            fab_variable.setScaleY(0);
            fab_menu.setScaleX(0);
            fab_menu.setScaleY(0);

            fab_menu.setEnabled(false);
            fab_factor.setEnabled(false);
            fab_variable.setEnabled(false);
        } else {
            fab_menu2.setScaleX(0);
            fab_menu2.setScaleY(0);

            fab_menu2.setVisibility(View.GONE);

            area = intent.getStringExtra("AREA");
        }

        //------------------------------------------------------------------------------------------
        //Se obtiene todos los factores
        factorDbHelper = new FactorDbHelper(this);
        variableDbHelper = new VariableDbHelper(this);
        i = 0;

        cursor = factorDbHelper.generateQuery("SELECT * FROM ");
        if (cursor.moveToFirst()) {
            items_factores = new CharSequence[cursor.getCount()];
            do {
                items_factores[i] = cursor.getString(2);
                i++;
            } while (cursor.moveToNext());
        }
    }

    /**
     * =============================================================================================
     * METODO: Recolecta la prueba (Fotografia) del monitoreo y la almacena en un archivo
     **/
    public void getCamara() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Ayllu");

        imagesFolder.mkdirs();
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());

        foto = format + ".jpg";
        file = new File(imagesFolder, foto);
        Uri uriSavedImage = Uri.fromFile(file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(cameraIntent, 1);
    }

    /**
     * =============================================================================================
     * METODO: Valida la selección del Checbox Repercusiones (Positivas/Negativas)
     **/
    public void comprobarRepercusiones1() {
        switch (rg_reper1.getCheckedRadioButtonId()) {
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

    /**
     * =============================================================================================
     * METODO: Valida la selección del Checbox Repercusiones (Actuales/Potenciales)
     **/
    public void comprobarRepercusiones2() {
        switch (rg_reper2.getCheckedRadioButtonId()) {
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

    /**
     * =============================================================================================
     * METODO: Valida la selección del Checbox Origen
     **/
    public void comprobarOrigen() {
        switch (rg_origen.getCheckedRadioButtonId()) {
            case R.id.rb_interno:
                origen = "10";
                break;
            case R.id.rb_externo:
                origen = "01";
                break;
        }
    }

    /**
     * =============================================================================================
     * METODO: Administra los eventos de tipo onClick
     **/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //--------------------------------------------------------------------------------------
            //EVENTO: Registrar un Monitoreo
            case R.id.fab_regMon:
                fab_menu.collapse();
                //Comprobar la selección de los Checbox
                comprobarRepercusiones1();
                comprobarRepercusiones2();
                comprobarOrigen();

                //Comprobamos que se creo la imagen
                if (file != null) {
                    if (op_reg.equals("N")) {
                        if (!opciones[1].equals("0")) uploadMonitoring("NEW");
                        else
                            createSimpleDialog("Por favor Seleciona una Variable", "ALERTA").show();
                    } else uploadMonitoring("MONITORING");
                } else
                    createSimpleDialog("Debes de Tomar una Foto del punto de afectación", "ALERTA").show();

                break;
            //--------------------------------------------------------------------------------------
            //EVENTO: Tomar fotografia
            case R.id.fab_camera:
                fab_menu.collapse();
                getCamara();
                break;
            //--------------------------------------------------------------------------------------
            //EVENTO: Establecer las coordenadas geograficas
            case R.id.fab_point:
                fab_menu.collapse();
                createPointDialog(latitud, longitud).show();
                break;
            //--------------------------------------------------------------------------------------
            //EVENTO: Seleccionar un Factor
            case R.id.fab_factor:
                createRadioListDialog(items_factores, "FACTORES", 1).show();
                break;
            //--------------------------------------------------------------------------------------
            //EVENTO: Seleccionar una Variable
            case R.id.fab_variable:
                createRadioListDialog(items_variables, "VARIABLES", 2).show();
                break;
            //--------------------------------------------------------------------------------------
            //EVENTO: Seleccionar la Presencia de Aparición
            case R.id.btn_presencia:
                createSelectorDialog(items_presencia, "PRESENCIA DE APARICIÓN", 1).show();
                break;
            //--------------------------------------------------------------------------------------
            //EVENTO: Seleccionar la Frecuencia de Aparición
            case R.id.btn_frecuencia:
                createSelectorDialog(items_frecuencia, "FRECUENCIA DE APARICIÓN", 2).show();
                break;
            default:
                break;
        }
    }

    public void uploadMonitoring(String tip_upload) {
        //Construimos el dato repercusiones con la seleccion de los dos Checbox
        String rep = "";
        for (int i = 0; i < 4; i++) rep += "" + repercusiones[i];
        //Comprobamos la conexión a internet
        if (wifiConected()) {
            //----------------------------------------------------------------------
            //Subimos la Imagen al Servidor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            PostClient service1 = PostClient.retrofit.create(PostClient.class);
            //MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("fotoUp", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
            Call<String> call1 = service1.uploadAttachment(filePart);
            call1.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    //Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_SHORT);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
            //----------------------------------------------------------------------
            //Subimos el monitoreo al Servidor
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.URL_API_AYLLU)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();

            AylluApiService service = retrofit.create(AylluApiService.class);

            if (tip_upload.equals("NEW")) {
                //Creamos un Objeto tipo task con los datos del formulario
                Task tk = new Task(monitor, opciones[1], area, latitud, longitud, fecha, rep, origen, seleccion_items[0], seleccion_items[1], file.getName());
                Call<Task> call = service.registrarPunto(tk);
                call.enqueue(new Callback<Task>() {
                    @Override
                    public void onResponse(Call<Task> call, Response<Task> response) {
                        Intent intent = new Intent(MonitoringRegistrationFormActivity.this, MonitorMenuActivity.class);
                        intent.putExtra("MONITOR", monitor + "");
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Task> call, Throwable t) {

                    }
                });
            } else if (tip_upload.equals("MONITORING")){
                //Creamos un Objeto tipo task con los datos del formulario
                Task tk = new Task(monitor, punto_afectacion, "", 0, 0, fecha, rep, origen, seleccion_items[0], seleccion_items[1], file.getName());
                Call<Task> call = service.monitorearPunto(tk);
                call.enqueue(new Callback<Task>() {
                    @Override
                    public void onResponse(Call<Task> call, Response<Task> response) {
                        Intent intent = new Intent(MonitoringRegistrationFormActivity.this, MonitorMenuActivity.class);
                        intent.putExtra("MONITOR", monitor + "");
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Task> call, Throwable t) {

                    }
                });
            }
        } else {
            if (tip_upload.equals("NEW")) {
                Task tk = new Task(monitor, opciones[1], area, latitud, longitud, fecha, rep, origen, seleccion_items[0], seleccion_items[1], file.getName());
                //Registramos el Monitoreo en el dispositivo en caso de Desconección
                TaskDbHelper taskDbHelper = new TaskDbHelper(this);
                taskDbHelper.saveTask(tk);
            }
            //Regresamos al Menu del monitor
            Intent intent = new Intent(MonitoringRegistrationFormActivity.this, MonitorMenuActivity.class);
            intent.putExtra("MONITOR", monitor + "");
            startActivity(intent);
            finish();
        }
    }

    /**
     * =============================================================================================
     * METODO: Genera un Dialogo basico en pantalla
     **/
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
                        });

        return builder.create();
    }

    /**
     * =============================================================================================
     * METODO: Genera un Dialogo con las opciones de las categorias (FACTOR/VARIABLE)
     **/
    public AlertDialog createRadioListDialog(final CharSequence[] items, String title, final int zn) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setSingleChoiceItems(items, pos[zn - 1], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (zn) {
                    //------------------------------------------------------------------------------
                    //DIALOGO: Se crea una lista de RadioButtons de los Factores
                    case 1:
                        item = items_factores[which].toString();
                        cursor = factorDbHelper.generateConditionalQuery(new String[]{item}, FactorContract.FactorEntry.NOMBRE);
                        cursor.moveToFirst();

                        opciones[0] = cursor.getString(1);
                        pos[0] = which;
                        opciones[1] = "0";
                        pos[1] = -1;

                        items_variables = null;
                        fab_variable.setEnabled(true);

                        i = 0;
                        cursor = variableDbHelper.generateConditionalQuery(new String[]{opciones[0]}, VariableContract.VariableEntry.FACTOR);
                        if (cursor.moveToFirst()) {
                            items_variables = new CharSequence[cursor.getCount()];
                            do {
                                items_variables[i] = cursor.getString(2);
                                i++;
                            } while (cursor.moveToNext());
                        }
                        break;
                    //------------------------------------------------------------------------------
                    //DIALOGO: Se crea una lista de RadioButtons de las Variables
                    case 2:
                        item = items_variables[which].toString();
                        cursor = variableDbHelper.generateConditionalQuery(new String[]{item}, VariableContract.VariableEntry.NOMBRE);
                        cursor.moveToFirst();

                        opciones[1] = cursor.getString(1);
                        pos[1] = which;
                        break;
                    default:
                        break;
                }
            }
        })
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        });


        return builder.create();
    }

    /**
     * =============================================================================================
     * METODO: Genera un Dialogo con las opciones de la Presencia y Frecuencia de aparición
     **/
    public AlertDialog createSelectorDialog(final CharSequence[] items, String title, final int opn) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setSingleChoiceItems(items, pos_seleccion[opn - 1], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (opn) {
                    //------------------------------------------------------------------------------
                    //DIALOGO: Se crea una lista de RadioButtons de la Presencia
                    case 1:
                        seleccion_items[0] = which + 1;
                        pos_seleccion[0] = which;
                        btn_presencia.setText(items_presencia[which]);
                        break;
                    //------------------------------------------------------------------------------
                    //DIALOGO: Se crea una lista de RadioButtons de la Frecuencia
                    case 2:
                        seleccion_items[1] = which + 1;
                        pos_seleccion[1] = which;
                        btn_frecuencia.setText(items_frecuencia[which]);
                        break;
                    default:
                        break;
                }
            }
        })
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        });


        return builder.create();
    }

    /**
     * =============================================================================================
     * METODO: Genera un Dialogo personalizado para obtener las Coordenadas geograficas del punto
     **/
    public AlertDialog createPointDialog(int l1, int l2) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MonitoringRegistrationFormActivity.this);

        LayoutInflater inflater = MonitoringRegistrationFormActivity.this.getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_point, null);

        builder.setView(v);

        FloatingActionButton fab_manual = (FloatingActionButton) v.findViewById(R.id.fab_manual);
        FloatingActionButton fab_fijar = (FloatingActionButton) v.findViewById(R.id.fab_fijar);

        final EditText et_latitud = (EditText) v.findViewById(R.id.et_latitud);
        final EditText et_longitud = (EditText) v.findViewById(R.id.et_longitud);

        et_latitud.setText(l1 + "");
        et_longitud.setText(l2 + "");

        et_latitud.setEnabled(false);
        et_longitud.setEnabled(false);

        fab_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_latitud.setEnabled(true);
                et_longitud.setEnabled(true);
            }
        });

        fab_fijar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        builder.setPositiveButton("HECHO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!et_latitud.getText().equals("") && !et_longitud.getText().equals("")) {
                            latitud = Integer.parseInt(et_latitud.getText().toString());
                            longitud = Integer.parseInt(et_longitud.getText().toString());
                            builder.create().dismiss();
                        }
                    }
                });

        return builder.create();
    }

    /**
     * =============================================================================================
     * METODO: Verifica la Conexión a internet
     **/
    protected Boolean wifiConected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return wifi.isConnected() || mobile.isConnected();
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onMenuExpanded() {
        animationButton(fab_camera, 1, 1);
        animationButton(fab_regMon, 1, 1);

        fab_regMon.setEnabled(true);
        fab_camera.setEnabled(true);

        if (op_reg.equals("N")) {
            animationButton(fab_point, 1, 1);
            fab_point.setEnabled(true);
        }
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onMenuCollapsed() {
        fab_regMon.setEnabled(false);
        fab_camera.setEnabled(false);

        animationButton(fab_camera, 0, 0);
        animationButton(fab_regMon, 0, 0);

        if (op_reg.equals("N")) {
            animationButton(fab_point, 0, 0);
            fab_point.setEnabled(false);
        }
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    public void animationButton(FloatingActionButton fb, float x, float y) {
        fb.animate()
                .scaleX(x)
                .scaleY(y)
                .setInterpolator(interpolador)
                .setDuration(10)
                .setStartDelay(100);
    }
}
