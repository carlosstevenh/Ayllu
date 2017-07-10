package com.example.edwin.ayllu;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.edwin.ayllu.domain.FactorDbHelper;
import com.example.edwin.ayllu.domain.Reporte;
import com.example.edwin.ayllu.domain.VariableDbHelper;
import com.example.edwin.ayllu.io.ApiConstants;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.edwin.ayllu.domain.FactorContract.FactorEntry;
import static com.example.edwin.ayllu.domain.VariableContract.VariableEntry;

public class MonitoringDetailFragment extends Fragment implements View.OnClickListener{

    Reporte reporte;
    String [] imgs;
    String estado = "", factor;
    int sizeimgs = 0;
    private ArrayList<File> files = new ArrayList<>();

    TextView    tvPropiedad, tvFactor, tvVariable, tvFecha, tvLatitud, tvLongitud, tvMonitor,
            tvRepercuciones1, tvRepercuciones2, tvOrigen, tvPorcentaje, tvFrecuencia;

    FloatingActionButton fabMonitoring;

    private MonitoringImageSwipeAdapter adapter;
    private LinearLayout dotsLayout;
    private ViewPager vpMonitoring;

    private MonitoringRegistrationFormFragment fragment;
    private String[] items_porcentaje, items_frecuencia;
    private FactorDbHelper factorDbHelper;
    private VariableDbHelper variableDbHelper;
    private Cursor cursor;

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        items_porcentaje = getResources().getStringArray(R.array.listPorcentaje);
        items_frecuencia = getResources().getStringArray(R.array.listFrecuencia);
        factorDbHelper = new FactorDbHelper(getActivity());
        variableDbHelper = new VariableDbHelper(getActivity());

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

        //Obtiene el codigo del factor a través del nombre de la variable
        cursor = variableDbHelper.generateConditionalQuery(new String[]{reporte.getVariable()},VariableEntry.NOMBRE);
        if (cursor.moveToFirst()){
            String codfac = cursor.getString(3);
            cursor.close();

            //Obtiene el nombre del factor a través de su codigo
            cursor = factorDbHelper.generateConditionalQuery(new String[]{codfac}, FactorEntry.CODIGO);
            if(cursor.moveToFirst()){
                factor = cursor.getString(2);
                cursor.close();
            }
        }


        estado = getArguments().getString("ESTADO");
        reporte.setEstado(estado);

        if(estado.equals("ONLINE")){
            sizeimgs = reporte.getSize();
            imgs = new String[sizeimgs];
            for (int i = 0; i<sizeimgs; i++) imgs[i] = ApiConstants.URL_IMG + reporte.getPruebas(i+1);
        } else {
            File file = new File(Environment.getExternalStorageDirectory() + "/Ayllu/Offline/" + reporte.getPrueba1());
            files.add(file);
            sizeimgs = 1;
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
        dotsLayout = (LinearLayout) view.findViewById(R.id.layoutDots);

        tvPropiedad = (TextView) view.findViewById(R.id.tv_propiedad);
        tvFactor = (TextView) view.findViewById(R.id.tv_factor);
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
        if (estado.equals("ONLINE")) adapter = new MonitoringImageSwipeAdapter(getActivity(), imgs);
        else adapter = new MonitoringImageSwipeAdapter(getActivity(), files);
        vpMonitoring.setAdapter(adapter);
        vpMonitoring.addOnPageChangeListener(viewPagerPageChangeListener);

        String repercusiones = reporte.getRepercusiones();
        String origen = reporte.getOrigen();
        String rep1, rep2, org;

        if(repercusiones.charAt(0) == '1') rep1 = getResources().getString(R.string.monitoring_detail_rep_positive);
        else  rep1 = getResources().getString(R.string.monitoring_detail_rep_negative);

        if(repercusiones.charAt(2) == '1') rep2 = getResources().getString(R.string.monitoring_detail_rep_current);
        else rep2 = getResources().getString(R.string.monitoring_detail_rep_potential);

        if(origen.charAt(0) == '1') org = getResources().getString(R.string.monitoring_detail_org_internal);
        else org = getResources().getString(R.string.monitoring_detail_org_external);

        //Cargamos los Datos del Monitoreo
        tvPropiedad.setText(reporte.getArea());
        tvFactor.setText(factor);
        tvVariable.setText(reporte.getVariable());
        tvFecha.setText(reporte.getFecha_mon());
        tvLatitud.setText(reverseCoordinates(reporte.getLatitud(),"LATITUD"));
        tvLongitud.setText(reverseCoordinates(reporte.getLongitud(),"LONGITUD"));
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
                    params.putString("FACTOR_NAME", factor);
                    params.putString("VAR_NAME", reporte.getVariable());
                    params.putString("LATITUD", reporte.getLatitud());
                    params.putString("LONGITUD", reporte.getLongitud());
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
        TextView[] dots = new TextView[sizeimgs];

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

        builder.setPositiveButton(getResources().getString(R.string.monitoring_detail_dialog_option_positive),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.create().dismiss();
                    }
                });

        return builder.create();
    }

    private String reverseCoordinates(String cad, String opc){
        String reverseCad;
        if (opc.equals("LATITUD")){
            reverseCad =    cad.charAt(0) +  "-" + cad.charAt(1) + cad.charAt(2) + "°" +
                    cad.charAt(3) + cad.charAt(4) + "'" +
                    cad.charAt(5) + cad.charAt(6) + "''";
        }
        else {
            reverseCad =    cad.charAt(0) +  "-" + cad.charAt(1) + cad.charAt(2) + cad.charAt(3) + "°" +
                    cad.charAt(4) + cad.charAt(5) + "'" +
                    cad.charAt(6) + cad.charAt(7) + "''";
        }

        return reverseCad;
    }
}
