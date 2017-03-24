package com.example.edwin.ayllu;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.edwin.ayllu.domain.Task;
import com.example.edwin.ayllu.domain.TaskDbHelper;
import com.example.edwin.ayllu.io.ApiConstants;
import com.example.edwin.ayllu.io.AylluApiService;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailReportFragment extends Fragment implements View.OnClickListener {
    TextView tvRep1, tvRep2, tvOrigen, tvPorcentaje, tvFrecuencia, tvLatitud, tvLongitud, tvVariable,
            titCoordenadas, titVariable;
    View vDiv1, vDiv2;
    FloatingActionButton fabRegist;
    LinearLayout lyLatitud, lyLongitud, lyVariable;
    ImageView imgMonitoring;
    File file;
    Task task;

    String por_name = "", fre_name = "", var_name = "", rep1_name = "POSITIVAS",
            rep2_name = "ACTUALES", orig_name = "INTERNO", tipo_upload = "",
            info_record = "";

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        task = new Task();

        task.setMonitor(intent.getStringExtra("MONITOR"));
        task.setVariable(intent.getStringExtra("PUNTO_AFECTACION"));
        task.setFecha(intent.getStringExtra("FECHA"));
        task.setRepercusiones(intent.getStringExtra("REPERCUSIONES"));
        task.setOrigen(intent.getStringExtra("ORIGEN"));
        task.setPorcentaje(Integer.parseInt(intent.getStringExtra("POR_NUMBER")));
        task.setFrecuencia(Integer.parseInt(intent.getStringExtra("FRE_NUMBER")));
        task.setNombre(intent.getStringExtra("PRUEBA"));

        String PATH_IMG = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ayllu/" + task.getNombre();
        file = new File(PATH_IMG);

        por_name = intent.getStringExtra("POR_NAME");
        fre_name = intent.getStringExtra("FRE_NAME");

        if (task.getRepercusiones().charAt(0) == '0') rep1_name = "NEGATIVAS";
        if (task.getRepercusiones().charAt(2) == '0') rep2_name = "POTENCIALES";
        if (task.getOrigen().charAt(0) == '0') orig_name = "EXTERNO";

        tipo_upload = intent.getStringExtra("TYPE_UPLOAD");

        if (tipo_upload.equals("NEW")) {
            task.setVariable(intent.getStringExtra("VAR_COD"));
            task.setArea(intent.getStringExtra("AREA"));
            task.setLatitud(Integer.parseInt(intent.getStringExtra("LATITUD")));
            task.setLongitud(Integer.parseInt(intent.getStringExtra("LONGITUD")));

            var_name = intent.getStringExtra("VAR_NAME");
        }
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onResume() {
        super.onResume();

        titCoordenadas = (TextView) getActivity().findViewById(R.id.tv_title_coordenadas);
        titVariable = (TextView) getActivity().findViewById(R.id.tv_title_variable);
        lyLatitud = (LinearLayout) getActivity().findViewById(R.id.area_latitud);
        lyLongitud = (LinearLayout) getActivity().findViewById(R.id.area_longitud);
        lyVariable = (LinearLayout) getActivity().findViewById(R.id.area_variable);
        vDiv1 = getActivity().findViewById(R.id.divisor5);
        vDiv2 = getActivity().findViewById(R.id.divisor6);

        tvRep1 = (TextView) getActivity().findViewById(R.id.tv_repercuciones1);
        tvRep2 = (TextView) getActivity().findViewById(R.id.tv_repercuciones2);
        tvOrigen = (TextView) getActivity().findViewById(R.id.tv_origen);
        tvPorcentaje = (TextView) getActivity().findViewById(R.id.tv_porcentaje);
        tvFrecuencia = (TextView) getActivity().findViewById(R.id.tv_frecuencia);
        tvLatitud = (TextView) getActivity().findViewById(R.id.tv_latitud);
        tvLongitud = (TextView) getActivity().findViewById(R.id.tv_longitud);
        tvVariable = (TextView) getActivity().findViewById(R.id.tv_variable);

        fabRegist = (FloatingActionButton) getActivity().findViewById(R.id.fab_reg);
        imgMonitoring = (ImageView) getActivity().findViewById(R.id.iv_monitoring);

        fabRegist.setOnClickListener(this);

        if (tipo_upload.equals("MONITORING")) {
            titCoordenadas.setVisibility(View.INVISIBLE);
            titVariable.setVisibility(View.INVISIBLE);
            lyLatitud.setVisibility(View.INVISIBLE);
            lyLongitud.setVisibility(View.INVISIBLE);
            lyVariable.setVisibility(View.INVISIBLE);
            vDiv1.setVisibility(View.INVISIBLE);
            vDiv2.setVisibility(View.INVISIBLE);
        }

        Picasso.with(getActivity()).load(file).fit().centerCrop().into(imgMonitoring);

        tvRep1.setText(rep1_name);
        tvRep2.setText(rep2_name);
        tvOrigen.setText(orig_name);
        tvPorcentaje.setText(por_name);
        tvFrecuencia.setText(fre_name);
        tvLatitud.setText(task.getLatitud() + "");
        tvLongitud.setText(task.getLongitud() + "");
        tvVariable.setText(var_name);

    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_detail_report, container, false);
        return root;
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_reg:
                uploadMonitoringImage(tipo_upload);
                break;
            default:
                break;
        }
    }


    /**
     * =============================================================================================
     * METODO: Subir las pruebas de un monitoreo al servidor
     **/
    public void uploadMonitoringImage(final String tip_upload) {
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
            call1.enqueue(new retrofit2.Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.isSuccessful()) uploadMonitoring(tip_upload);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    RecordInfoFragment recordInfoFragment = new RecordInfoFragment();
                    Bundle params = new Bundle();
                    params.putString("RESULT","ERROR");
                    recordInfoFragment.setArguments(params);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack("DetailReportFragment")
                            .add(R.id.presentation_principal_context, recordInfoFragment)
                            .commit();
                }
            });
        } else {
            //Registramos el Monitoreo en el dispositivo en caso de Desconección
            TaskDbHelper taskDbHelper = new TaskDbHelper(getActivity());
            taskDbHelper.saveTask(task);

            RecordInfoFragment recordInfoFragment = new RecordInfoFragment();
            Bundle params = new Bundle();
            params.putString("RESULT","OFFLINE");
            recordInfoFragment.setArguments(params);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack("DetailReportFragment")
                    .add(R.id.presentation_principal_context, recordInfoFragment)
                    .commit();
        }
    }

    /**
     * =============================================================================================
     * METODO: Subir datos de un monitoreo al servidor
     **/
    public void uploadMonitoring (String tip_upload){
        //--------------------------------------------------------------------------------------
        //Subimos el monitoreo al Servidor
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.URL_API_AYLLU)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        AylluApiService service = retrofit.create(AylluApiService.class);

        if (tip_upload.equals("NEW")) {
            //Creamos un Objeto tipo task con los datos del formulario
            Call<Task> call = service.registrarPunto(task);
            call.enqueue(new retrofit2.Callback<Task>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if(response.isSuccessful()){
                        RecordInfoFragment recordInfoFragment = new RecordInfoFragment();
                        Bundle params = new Bundle();
                        params.putString("RESULT","OK");
                        recordInfoFragment.setArguments(params);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack("DetailReportFragment")
                                .add(R.id.presentation_principal_context, recordInfoFragment)
                                .commit();
                    }
                }

                @Override
                public void onFailure(Call<Task> call, Throwable t) {
                    RecordInfoFragment recordInfoFragment = new RecordInfoFragment();
                    Bundle params = new Bundle();
                    params.putString("RESULT","ERROR");
                    recordInfoFragment.setArguments(params);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack("DetailReportFragment")
                            .add(R.id.presentation_principal_context, recordInfoFragment)
                            .commit();
                }
            });
        } else if (tip_upload.equals("MONITORING")) {
            Call<Task> call = service.monitorearPunto(task);
            call.enqueue(new retrofit2.Callback<Task>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if(response.isSuccessful()){
                        RecordInfoFragment recordInfoFragment = new RecordInfoFragment();
                        Bundle params = new Bundle();
                        params.putString("RESULT","OK");
                        recordInfoFragment.setArguments(params);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack("DetailReportFragment")
                                .add(R.id.presentation_principal_context, recordInfoFragment)
                                .commit();
                    }
                }

                @Override
                public void onFailure(Call<Task> call, Throwable t) {
                    RecordInfoFragment recordInfoFragment = new RecordInfoFragment();
                    Bundle params = new Bundle();
                    params.putString("RESULT","ERROR");
                    recordInfoFragment.setArguments(params);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack("DetailReportFragment")
                            .add(R.id.presentation_principal_context, recordInfoFragment)
                            .commit();
                }
            });
        }
    }

    /**
     * =============================================================================================
     * METODO: Verifica la Conexión a internet
     **/
    protected Boolean wifiConected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return wifi.isConnected() || mobile.isConnected();
    }
}
