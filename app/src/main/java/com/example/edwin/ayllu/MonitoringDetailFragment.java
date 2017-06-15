package com.example.edwin.ayllu;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.edwin.ayllu.domain.Reporte;
import com.example.edwin.ayllu.domain.Task;
import com.example.edwin.ayllu.io.ApiConstants;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MonitoringDetailFragment extends Fragment implements View.OnClickListener{

    Reporte reporte;
    String [] imgs;

    TextView    tvArea, tvVariable, tvFecha, tvLatitud, tvLongitud, tvMonitor,
            tvRepercuciones1, tvRepercuciones2, tvOrigen, tvPorcentaje, tvFrecuencia;

    FloatingActionButton fabMonitoring;

    private MonitoringImageSwipeAdapter adapter;
    private LinearLayout dotsLayout;
    private ViewPager vpMonitoring;

    private MonitoringRegistrationFormFragment fragment;
    private String[] items_porcentaje, items_frecuencia;

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        items_porcentaje = getResources().getStringArray(R.array.listPorcentaje);
        items_frecuencia = getResources().getStringArray(R.array.listFrecuencia);

        reporte = new Reporte();

        reporte.setCod_paf(Integer.parseInt(getArguments().getString("PUNTO")));
        reporte.setArea(getArguments().getString("AREA"));
        reporte.setVariable(getArguments().getString("VARIABLE"));
        reporte.setFecha_mon(getArguments().getString("FECHA"));
        reporte.setLatitud(getArguments().getString("LATITUD"));
        reporte.setLongitud(getArguments().getString("LONGITUD"));
        reporte.setUsuario(getArguments().getString("MONITOR"));
        reporte.setRepercusiones(getArguments().getString("REPERCUSIONES"));
        reporte.setOrigen(getArguments().getString("ORIGEN"));
        reporte.setPorcentaje(Integer.parseInt(getArguments().getString("PORCENTAJE")));
        reporte.setFrecuencia(Integer.parseInt(getArguments().getString("FRECUENCIA")));
        reporte.setPrueba1(getArguments().getString("PRUEBA1"));
        reporte.setPrueba2(getArguments().getString("PRUEBA2"));
        reporte.setPrueba3(getArguments().getString("PRUEBA3"));

        int size = reporte.getSize();
        imgs = new String[size];
        for (int i = 0; i<size; i++) imgs[i] = ApiConstants.URL_IMG + reporte.getPruebas(i+1);

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
        dotsLayout = (LinearLayout) view.findViewById(R.id.layoutDots);

        tvArea = (TextView) view.findViewById(R.id.tv_area);
        tvVariable = (TextView) view.findViewById(R.id.tv_variable);
        tvFecha = (TextView) view.findViewById(R.id.tv_fecha);
        tvLatitud = (TextView) view.findViewById(R.id.tv_latitud);
        tvLongitud = (TextView) view.findViewById(R.id.tv_longitud);
        tvMonitor = (TextView) view.findViewById(R.id.tv_monitor);
        tvRepercuciones1 = (TextView) view.findViewById(R.id.tv_repercuciones1);
        tvRepercuciones2 = (TextView) view.findViewById(R.id.tv_repercuciones2);
        tvOrigen = (TextView) view.findViewById(R.id.tv_origen);
        tvPorcentaje = (TextView) view.findViewById(R.id.tv_porcentaje);
        tvFrecuencia = (TextView) view.findViewById(R.id.tv_frecuencia);
        fabMonitoring = (FloatingActionButton) view.findViewById(R.id.fab_monitoring);

        fabMonitoring.setOnClickListener(this);

        //Cargamos las Imagenes
        adapter = new MonitoringImageSwipeAdapter(getActivity(), imgs);
        vpMonitoring.setAdapter(adapter);
        vpMonitoring.addOnPageChangeListener(viewPagerPageChangeListener);

        String repercusiones = reporte.getRepercusiones();
        String origen = reporte.getOrigen();

        String rep1 = "";
        String rep2 = "";
        String org = "";

        if(repercusiones.charAt(0) == '1') rep1 = getResources().getString(R.string.optionPositivasRepercusiones);
        else  rep1 = getResources().getString(R.string.optionNegativasRepercusiones);

        if(repercusiones.charAt(2) == '1') rep2 = getResources().getString(R.string.optionActualesRepercusiones);
        else rep2 = getResources().getString(R.string.optionPotencialesRepercusiones);

        if(origen.charAt(0) == '1') org = getResources().getString(R.string.optionInternoOrigen);
        else org = getResources().getString(R.string.optionExternoOrigen);


        //Cargamos los Datos del Monitoreo
        tvArea.setText(reporte.getArea());
        tvVariable.setText(reporte.getVariable());
        tvFecha.setText(reporte.getFecha_mon());
        tvLatitud.setText(reporte.getLatitud());
        tvLongitud.setText(reporte.getLongitud());
        tvMonitor.setText(reporte.getUsuario());
        tvRepercuciones1.setText(rep1);
        tvRepercuciones2.setText(rep2);
        tvOrigen.setText(org);
        tvPorcentaje.setText(items_porcentaje[reporte.getPorcentaje()-1]);
        tvFrecuencia.setText(items_frecuencia[reporte.getFrecuencia()-1]);

        addBottomDots(0);

        return view;
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_monitoring:

                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                String fecha = s.format(new Date());

                if(fecha.equals(reporte.getFecha_mon())){
                    createSimpleDialog(
                            getResources().getString(R.string.descriptionDetailMonitoringDialog),
                            getResources().getString(R.string.titleDetailMonitoringDialog)
                    ).show();
                } else{
                    fragment = new MonitoringRegistrationFormFragment();
                    Bundle params = new Bundle();
                    params.putString("PUNTO", String.format(Locale.getDefault(),"%d",reporte.getCod_paf()));
                    params.putString("OPCION", "M");
                    fragment.setArguments(params);

                    //Inflamos el layout para el Fragmento MonitoringListFragment
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.monitoring_principal_context, fragment)
                            .commit();
                }
                break;
            default:
                break;
        }
    }

    /**
     * =============================================================================================
     * METODO: Añade los puntos al LinearLayout encargado de mostrar información de el Slide Actual
     **/
    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[imgs.length];

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
     * METODO: Genera un Dialogo basico en pantalla
     **/
    public AlertDialog createSimpleDialog(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_monitoring_info, null);
        builder.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title_dialog);
        TextView tvDescription = (TextView) v.findViewById(R.id.tv_description_dialog);

        tvTitle.setText(titulo);
        tvDescription.setText(mensaje);

        builder.setPositiveButton("HECHO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.create().dismiss();
                    }
                });

        return builder.create();
    }
}
