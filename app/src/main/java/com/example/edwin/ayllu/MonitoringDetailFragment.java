package com.example.edwin.ayllu;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.Locale;

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

public class MonitoringDetailFragment extends Fragment implements View.OnClickListener {
    TextView tvRep1, tvRep2, tvOrigen, tvPorcentaje, tvFrecuencia, tvLatitud, tvLongitud, tvVariable,
            titCoordenadas, titVariable;
    View vDiv1, vDiv2;
    FloatingActionButton fabRegist;
    LinearLayout lyLatitud, lyLongitud, lyVariable;
    Task task;

    MonitoringImageSwipeAdapter adapter;
    ViewPager vpMonitoring;

    MonitoringInfoFragment fragment;

    ProgressDialog loading;
    HttpLoggingInterceptor logging;

    String por_name = "", fre_name = "", var_name = "", rep1_name = "POSITIVAS",
            rep2_name = "ACTUALES", orig_name = "INTERNO", tipo_upload = "";

    private ArrayList<File> files;

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        task = new Task();

        task.setMonitor(getArguments().getString("MONITOR"));
        task.setVariable(getArguments().getString("PUNTO_AFECTACION"));
        task.setFecha(getArguments().getString("FECHA"));
        task.setRepercusiones(getArguments().getString("REPERCUSIONES"));
        task.setOrigen(getArguments().getString("ORIGEN"));
        task.setPorcentaje(Integer.parseInt(getArguments().getString("POR_NUMBER")));
        task.setFrecuencia(Integer.parseInt(getArguments().getString("FRE_NUMBER")));

        int sizeFile = Integer.parseInt(getArguments().getString("FILES_NUMBER"));

        if(sizeFile >= 1) task.setNombre(getArguments().getString("PRUEBA1"));
        if(sizeFile >= 2) task.setNombre2(getArguments().getString("PRUEBA2"));
        if(sizeFile == 3) task.setNombre3(getArguments().getString("PRUEBA3"));

        files = new ArrayList<>(sizeFile);

        String PATH_IMG;
        if(!task.getNombre().equals("null")){
            PATH_IMG = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ayllu/" + task.getNombre();
            files.add(new File(PATH_IMG));
        }
        if(!task.getNombre2().equals("null")){
            PATH_IMG = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ayllu/" + task.getNombre2();
            files.add(new File(PATH_IMG));
        }
        if(!task.getNombre3().equals("null")){
            PATH_IMG = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ayllu/" + task.getNombre3();
            files.add(new File(PATH_IMG));
        }


        por_name = getArguments().getString("POR_NAME");
        fre_name = getArguments().getString("FRE_NAME");

        if (task.getRepercusiones().charAt(0) == '0') rep1_name = "NEGATIVAS";
        if (task.getRepercusiones().charAt(2) == '0') rep2_name = "POTENCIALES";
        if (task.getOrigen().charAt(0) == '0') orig_name = "EXTERNO";

        tipo_upload = getArguments().getString("TYPE_UPLOAD");

        assert tipo_upload != null;
        if (tipo_upload.equals("NEW")) {
            task.setVariable(getArguments().getString("VAR_COD"));
            task.setArea(getArguments().getString("AREA"));
            task.setLatitud(Integer.parseInt(getArguments().getString("LATITUD")));
            task.setLongitud(Integer.parseInt(getArguments().getString("LONGITUD")));

            var_name = getArguments().getString("VAR_NAME");
        }
    }


    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_monitoring_detail, container, false);

        vpMonitoring = (ViewPager) view.findViewById(R.id.vp_monitoring);

        titCoordenadas = (TextView) view.findViewById(R.id.tv_title_coordenadas);
        titVariable = (TextView) view.findViewById(R.id.tv_title_variable);
        lyLatitud = (LinearLayout) view.findViewById(R.id.area_latitud);
        lyLongitud = (LinearLayout) view.findViewById(R.id.area_longitud);
        lyVariable = (LinearLayout) view.findViewById(R.id.area_variable);
        vDiv1 = view.findViewById(R.id.divisor5);
        vDiv2 = view.findViewById(R.id.divisor6);

        tvRep1 = (TextView) view.findViewById(R.id.tv_repercuciones1);
        tvRep2 = (TextView) view.findViewById(R.id.tv_repercuciones2);
        tvOrigen = (TextView) view.findViewById(R.id.tv_origen);
        tvPorcentaje = (TextView) view.findViewById(R.id.tv_porcentaje);
        tvFrecuencia = (TextView) view.findViewById(R.id.tv_frecuencia);
        tvLatitud = (TextView) view.findViewById(R.id.tv_latitud);
        tvLongitud = (TextView) view.findViewById(R.id.tv_longitud);
        tvVariable = (TextView) view.findViewById(R.id.tv_variable);

        fabRegist = (FloatingActionButton) view.findViewById(R.id.fab_reg);

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

        //Cargamos las Imagenes
        adapter = new MonitoringImageSwipeAdapter(getActivity(), files);
        vpMonitoring.setAdapter(adapter);

        tvRep1.setText(rep1_name);
        tvRep2.setText(rep2_name);
        tvOrigen.setText(orig_name);
        tvPorcentaje.setText(por_name);
        tvFrecuencia.setText(fre_name);
        tvLatitud.setText(String.format(Locale.getDefault(),"%d",task.getLatitud()));
        tvLongitud.setText(String.format(Locale.getDefault(),"%d",task.getLongitud()));
        tvVariable.setText(var_name);

        return view;
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

            PostClient service1 = PostClient.retrofit.create(PostClient.class);
            loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.procesando),getResources().getString(R.string.esperar),false,false);

            if(files.size()==2){
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("fotoUp", files.get(0).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(0)));
                MultipartBody.Part filePart2 = MultipartBody.Part.createFormData("fotoUp2", files.get(1).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(1)));

                Call<String>call1 = service1.upLoad2(filePart,filePart2);
                call1.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()) uploadMonitoring(tip_upload);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        loading.dismiss();
                        fragment = new MonitoringInfoFragment();
                        Bundle params = new Bundle();
                        params.putString("RESULT","ERROR");
                        fragment.setArguments(params);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.monitoring_principal_context, fragment)
                                .commit();
                    }
                });
            }
            else if(files.size() == 3){
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("fotoUp", files.get(0).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(0)));
                MultipartBody.Part filePart2 = MultipartBody.Part.createFormData("fotoUp2", files.get(1).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(1)));
                MultipartBody.Part filePart3 = MultipartBody.Part.createFormData("fotoUp3", files.get(2).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(2)));

                Call<String>call1 = service1.upLoad3(filePart,filePart2,filePart3);
                call1.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()) uploadMonitoring(tip_upload);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        loading.dismiss();
                        fragment = new MonitoringInfoFragment();
                        Bundle params = new Bundle();
                        params.putString("RESULT","ERROR");
                        fragment.setArguments(params);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.monitoring_principal_context, fragment)
                                .commit();
                    }
                });
            }
            else{

                //MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("fotoUp", files.get(0).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(0)));
                Call<String> call1 = service1.uploadAttachment(filePart);
                call1.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()) uploadMonitoring(tip_upload);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        loading.dismiss();
                        fragment = new MonitoringInfoFragment();
                        Bundle params = new Bundle();
                        params.putString("RESULT","ERROR");
                        fragment.setArguments(params);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.monitoring_principal_context, fragment)
                                .commit();
                    }
                });
            }
        } else {
            //Registramos el Monitoreo en el dispositivo en caso de Desconección
            TaskDbHelper taskDbHelper = new TaskDbHelper(getActivity());
            taskDbHelper.saveTask(task);

            fragment = new MonitoringInfoFragment();
            Bundle params = new Bundle();
            params.putString("RESULT","OFFLINE");
            fragment.setArguments(params);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.monitoring_principal_context, fragment)
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
        logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.URL_API_AYLLU)
                .client(httpClient.build())
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
                    Log.e("TAG:RESPONSE",response.message());
                    if(response.isSuccessful()) {
                        loading.dismiss();
                        fragment = new MonitoringInfoFragment();
                        Bundle params = new Bundle();
                        params.putString("RESULT","OK");
                        fragment.setArguments(params);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.monitoring_principal_context, fragment)
                                .commit();
                    }
                    else {
                        loading.dismiss();
                        fragment = new MonitoringInfoFragment();
                        Bundle params = new Bundle();
                        params.putString("RESULT","ERROR");
                        fragment.setArguments(params);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.monitoring_principal_context, fragment)
                                .commit();
                    }
                }
                @Override
                public void onFailure(Call<Task> call, Throwable t) {
                    loading.dismiss();
                    fragment = new MonitoringInfoFragment();
                    Bundle params = new Bundle();
                    params.putString("RESULT","ERROR");
                    fragment.setArguments(params);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .add(R.id.monitoring_principal_context, fragment)
                            .commit();
                }
            });
        } else if (tip_upload.equals("MONITORING")) {
            Call<Task> call = service.monitorearPunto(task);
            call.enqueue(new retrofit2.Callback<Task>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if(response.isSuccessful()){
                        loading.dismiss();
                        fragment = new MonitoringInfoFragment();
                        Bundle params = new Bundle();
                        params.putString("RESULT","OK");
                        fragment.setArguments(params);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.monitoring_principal_context, fragment)
                                .commit();
                    }else {
                        loading.dismiss();
                        fragment = new MonitoringInfoFragment();
                        Bundle params = new Bundle();
                        params.putString("RESULT","ERROR");
                        fragment.setArguments(params);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.monitoring_principal_context, fragment)
                                .commit();
                    }
                }

                @Override
                public void onFailure(Call<Task> call, Throwable t) {
                    loading.dismiss();
                    fragment = new MonitoringInfoFragment();
                    Bundle params = new Bundle();
                    params.putString("RESULT","ERROR");
                    fragment.setArguments(params);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .add(R.id.monitoring_principal_context, fragment)
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
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork != null){
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) return true;
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) return true;
        }
        return false;
    }
}
