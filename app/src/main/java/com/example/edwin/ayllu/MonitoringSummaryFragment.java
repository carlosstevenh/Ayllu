package com.example.edwin.ayllu;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.edwin.ayllu.domain.Imagen;
import com.example.edwin.ayllu.domain.ImagenDbHelper;
import com.example.edwin.ayllu.domain.Task;
import com.example.edwin.ayllu.domain.TaskDbHelper;
import com.example.edwin.ayllu.io.ApiConstants;
import com.example.edwin.ayllu.io.AylluApiService;
import com.example.edwin.ayllu.io.PostClient;
import com.getbase.floatingactionbutton.FloatingActionButton;

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

public class MonitoringSummaryFragment extends Fragment implements View.OnClickListener {
    TextView tvRep1, tvRep2, tvOrigen, tvPorcentaje, tvFrecuencia, tvLatitud, tvLongitud,
             tvFactor, tvVariable;
    FloatingActionButton fabRegist;
    Task task;
    Imagen imagen;

    private MonitoringImageSwipeAdapter adapter;
    private LinearLayout dotsLayout;
    private ViewPager vpMonitoring;

    MonitoringInfoFragment fragment;

    ProgressDialog loading;
    HttpLoggingInterceptor logging;

    String  por_name = "", fre_name = "", factor_name = "", var_name = "", tipo_upload = "",
            rep1_name = "", rep2_name = "", orig_name = "", lat_name = "", long_name = "";

    private ArrayList<File> files;

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        task = new Task();
        imagen = new Imagen();

        rep1_name = getResources().getString(R.string.summary_item_description_rep_positive);
        rep2_name = getResources().getString(R.string.summary_item_description_rep_current);
        orig_name = getResources().getString(R.string.summary_item_description_org_internal);


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

        imagen.setFotografia1(task.getNombre());
        imagen.setFotografia2(task.getNombre2());
        imagen.setFotografia3(task.getNombre3());

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

        if (task.getRepercusiones().charAt(0) == '0') rep1_name = getResources().getString(R.string.summary_item_description_rep_negative);
        if (task.getRepercusiones().charAt(2) == '0') rep2_name = getResources().getString(R.string.summary_item_description_rep_potential);
        if (task.getOrigen().charAt(0) == '0') orig_name = getResources().getString(R.string.summary_item_description_org_external);

        task.setLatitud(getArguments().getString("LATITUD_NUMBER"));
        task.setLongitud(getArguments().getString("LONGITUD_NUMBER"));
        lat_name = getArguments().getString("LATITUD");
        long_name = getArguments().getString("LONGITUD");
        var_name = getArguments().getString("VAR_NAME");
        factor_name = getArguments().getString("FACTOR_NAME");
        tipo_upload = getArguments().getString("TYPE_UPLOAD");

        assert tipo_upload != null;
        if (tipo_upload.equals("NEW")) {
            task.setVariable(getArguments().getString("VAR_COD"));
            task.setArea(getArguments().getString("AREA"));

        } else task.setTipo("M");
    }


    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_monitoring_summary, container, false);

        vpMonitoring = (ViewPager) view.findViewById(R.id.vp_monitoring);
        dotsLayout = (LinearLayout) view.findViewById(R.id.layoutDots);

        tvRep1 = (TextView) view.findViewById(R.id.tv_repercuciones1);
        tvRep2 = (TextView) view.findViewById(R.id.tv_repercuciones2);
        tvOrigen = (TextView) view.findViewById(R.id.tv_origen);
        tvPorcentaje = (TextView) view.findViewById(R.id.tv_porcentaje);
        tvFrecuencia = (TextView) view.findViewById(R.id.tv_frecuencia);
        tvLatitud = (TextView) view.findViewById(R.id.tv_latitud);
        tvLongitud = (TextView) view.findViewById(R.id.tv_longitud);
        tvFactor = (TextView) view.findViewById(R.id.tv_factor);
        tvVariable = (TextView) view.findViewById(R.id.tv_variable);

        fabRegist = (FloatingActionButton) view.findViewById(R.id.fab_reg);
        fabRegist.setOnClickListener(this);

        //Cargamos las Imagenes
        adapter = new MonitoringImageSwipeAdapter(getActivity(), files);
        vpMonitoring.setAdapter(adapter);
        vpMonitoring.addOnPageChangeListener(viewPagerPageChangeListener);

        tvRep1.setText(rep1_name);
        tvRep2.setText(rep2_name);
        tvOrigen.setText(orig_name);
        tvPorcentaje.setText(por_name);
        tvFrecuencia.setText(fre_name);
        tvLatitud.setText(lat_name);
        tvLongitud.setText(long_name);
        tvVariable.setText(var_name);
        tvFactor.setText(factor_name);

        addBottomDots(0);

        return view;
    }

    /**
     * =============================================================================================
     * METODO: Añade los puntos al LinearLayout encargado de mostrar información de el Slide Actual
     **/
    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[files.size()];

        dotsLayout.removeAllViews();
        //Limpiamos y recargamos todos lo views para los puntos
        for (int i = 0; i < dots.length; i++) {
            //Creamos un nuevo view para el punto con el caracter (.)
            dots[i] = new TextView(getActivity());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(60);
            dots[i].setTextColor(getResources().getColor(R.color.colorPrimary));
            dotsLayout.addView(dots[i]);
        }
        //Determinamos que punto esta activo segun el layout actual
        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.colorTextIcons));
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
            loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.summary_process_message_upload),getResources().getString(R.string.summary_process_message),false,false);

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
            ImagenDbHelper imagenDbHelper = new ImagenDbHelper(getActivity());

            taskDbHelper.saveTask(task);
            imagenDbHelper.saveImagen(imagen);

            fragment = new MonitoringInfoFragment();
            Bundle params = new Bundle();
            params.putString("RESULT","OFFLINE");
            if (tip_upload.equals("NEW")){
                params.putString("TIPO","NEW");
                params.putString("AREA",task.getArea());
            } else {
                params.putString("TIPO","MONITORING");
            }
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
                    if(response.isSuccessful()) {
                        loading.dismiss();
                        fragment = new MonitoringInfoFragment();
                        Bundle params = new Bundle();
                        params.putString("RESULT","OK");
                        params.putString("TIPO","NEW");
                        params.putString("AREA",task.getArea());
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
                        params.putString("TIPO","NEW");
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
                    params.putString("TIPO","NEW");
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
                        params.putString("TIPO","MONITORING");
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
                        params.putString("TIPO","MONITORING");
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
                    params.putString("TIPO","MONITORING");
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
}
