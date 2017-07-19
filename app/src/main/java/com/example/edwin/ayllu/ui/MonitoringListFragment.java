package com.example.edwin.ayllu.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.AdminSQLite;
import com.example.edwin.ayllu.MonitoringDetailFragment;
import com.example.edwin.ayllu.MonitoringRegistrationFormFragment;
import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.domain.AreaDbHelper;
import com.example.edwin.ayllu.domain.MonitoreoDbHelper;
import com.example.edwin.ayllu.domain.PaisDbHelper;
import com.example.edwin.ayllu.domain.Reporte;
import com.example.edwin.ayllu.domain.SeccionDbHelper;
import com.example.edwin.ayllu.domain.SubtramoDbHelper;
import com.example.edwin.ayllu.domain.TramoDbHelper;
import com.example.edwin.ayllu.io.AylluApiAdapter;
import com.example.edwin.ayllu.io.model.ReporteResponse;
import com.example.edwin.ayllu.ui.adapter.MonitoringAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

import static com.example.edwin.ayllu.domain.TramoContract.TramoEntry;
import static com.example.edwin.ayllu.domain.SubtramoContract.SubtramoEntry;
import static com.example.edwin.ayllu.domain.SeccionContract.SeccionEntry;
import static com.example.edwin.ayllu.domain.AreaContract.AreaEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonitoringListFragment extends Fragment implements View.OnClickListener, FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
    //VARIABLES DE VISTA
    private MonitoringAdapter adapter;
    private RecyclerView mReporteList;
    private FloatingActionButton fab_tramo, fab_subtramo, fab_seccion, fab_area;
    private FloatingActionButton fab_search, fab_new;
    private FloatingActionsMenu menu;
    private TextView tvInfo;

    MonitoringRegistrationFormFragment fragment;
    MonitoringDetailFragment monitoringDetailFragment;

    //VARIABLES DATOS TEMPORALES
    ArrayList<Reporte> reportes = new ArrayList<>();
    CharSequence[] items_tramos, items_subtramos, items_secciones, items_areas, items_tipos;;
    int[] op = {0, 0, 0, 0};
    int[] pos = {-1, -1, -1, -1};

    Interpolator interpolador;
    String item = "";
    String monitor = "", pais = "";
    int i = 0;

    //VARIABLES CONTROL DE DATOS FIJOS
    PaisDbHelper paisDbHelper;
    TramoDbHelper tramoDbHelper;
    SubtramoDbHelper subtramoDbHelper;
    SeccionDbHelper seccionDbHelper;
    AreaDbHelper areaDbHelper;
    MonitoreoDbHelper monitoreoDbHelper;
    Cursor cursor;

    /**
     * =============================================================================================
     * METODO: Encargado de activarse cuando el fragmento se Infle
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //------------------------------------------------------------------------------------------
        //Relacionamos variables con la actividad actual
        adapter = new MonitoringAdapter(getActivity());

        paisDbHelper = new PaisDbHelper(getActivity());
        tramoDbHelper = new TramoDbHelper(getActivity());
        subtramoDbHelper = new SubtramoDbHelper(getActivity());
        seccionDbHelper = new SeccionDbHelper(getActivity());
        areaDbHelper = new AreaDbHelper(getActivity());
        monitoreoDbHelper = new MonitoreoDbHelper(getActivity());
        int i = 0;
        //------------------------------------------------------------------------------------------
        //Obtenemos el codigo del monitor y el pais del usuario en sesión
        AdminSQLite admin = new AdminSQLite(getActivity().getApplicationContext(), "login", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        //Prepara la sentencia SQL para la consulta en la Tabla de usuarios
        Cursor cursor = bd.rawQuery("SELECT codigo, pais FROM login LIMIT 1", null);
        cursor.moveToFirst();
        monitor = cursor.getString(0);
        pais = cursor.getString(1);
        cursor.close();
        //------------------------------------------------------------------------------------------
        //Obtenemos los tramos correspondientes al pais del usuario actual
        cursor = tramoDbHelper.generateConditionalQuery(new String[]{pais}, TramoEntry.PAIS);
        if (cursor.moveToFirst()) {
            items_tramos = new CharSequence[cursor.getCount()];
            do {
                items_tramos[i] = cursor.getString(2);
                i++;
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    /**
     * =============================================================================================
     * METODO: Encargado de activarse cuando se cree la vista y cargar con datos el RecyclerView
     **/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitoring_list, container, false);

        mReporteList = (RecyclerView) view.findViewById(R.id.monitoring_list);

        fab_new = (FloatingActionButton) view.findViewById(R.id.fab_new);
        fab_search = (FloatingActionButton) view.findViewById(R.id.fab_search);
        fab_tramo = (FloatingActionButton) view.findViewById(R.id.fab_tramo);
        fab_subtramo = (FloatingActionButton) view.findViewById(R.id.fab_subtramo);
        fab_seccion = (FloatingActionButton) view.findViewById(R.id.fab_seccion);
        fab_area = (FloatingActionButton) view.findViewById(R.id.fab_area);
        menu = (FloatingActionsMenu) view.findViewById(R.id.menu_fab);

        tvInfo = (TextView) view.findViewById(R.id.tv_info);

        fab_new.setScaleX(0);
        fab_search.setScaleX(0);
        fab_new.setScaleY(0);
        fab_search.setScaleY(0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            interpolador = AnimationUtils.loadInterpolator(getActivity().getBaseContext(),
                    android.R.interpolator.fast_out_slow_in);
        }


        fab_subtramo.setEnabled(false);
        fab_seccion.setEnabled(false);
        fab_area.setEnabled(false);
        fab_new.setEnabled(false);
        fab_search.setEnabled(false);

        fab_tramo.setOnClickListener(this);
        fab_subtramo.setOnClickListener(this);
        fab_seccion.setOnClickListener(this);
        fab_area.setOnClickListener(this);
        menu.setOnFloatingActionsMenuUpdateListener(this);
        fab_search.setOnClickListener(this);
        fab_new.setOnClickListener(this);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reportes.size() > 0) {
                    Reporte reporte = reportes.get(mReporteList.getChildAdapterPosition(view));

                    monitoringDetailFragment = new MonitoringDetailFragment();
                    Bundle params = new Bundle();
                    params.putString("PUNTO", reporte.getCod_paf() + "");
                    params.putString("AREA", reporte.getArea());
                    params.putString("VARIABLE", reporte.getVariable());
                    params.putString("FECHA", reporte.getFecha_mon());
                    params.putString("LATITUD", reporte.getLatitud());
                    params.putString("LONGITUD", reporte.getLongitud());
                    params.putString("MONITOR", reporte.getUsuario());
                    params.putString("REPERCUSIONES", reporte.getRepercusiones());
                    params.putString("ORIGEN", reporte.getOrigen());
                    params.putString("PORCENTAJE", reporte.getPorcentaje() + "");
                    params.putString("FRECUENCIA", reporte.getFrecuencia() + "");
                    params.putString("PRUEBA1",reporte.getPrueba1());
                    params.putString("PRUEBA2",reporte.getPrueba2());
                    params.putString("PRUEBA3",reporte.getPrueba3());
                    params.putString("ESTADO",reporte.getEstado());
                    monitoringDetailFragment.setArguments(params);

                    //Inflamos el layout para el Fragmento MonitoringListFragment
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.monitoring_principal_context, monitoringDetailFragment)
                            .commit();
                }
            }
        });

        setupReporteList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (reportes.size() > 0) tvInfo.setVisibility(View.INVISIBLE);
        comprobarFiltros();
        menu.collapse();

    }

    /**
     * =============================================================================================
     * METODO: Presenta en Interfaz un mensaje Tipo Dialog
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

        builder.setPositiveButton(getResources().getString(R.string.opcionListMonitoringDialog),
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
     * METODO: Presenta en Interfaz un mensaje Tipo Dialog
     **/
    public AlertDialog createOfflineMonitoringDialog(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_monitoring_info, null);
        builder.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title_dialog);
        TextView tvDescription = (TextView) v.findViewById(R.id.tv_description_dialog);

        tvTitle.setText(titulo);
        tvDescription.setText(mensaje);

        builder.setPositiveButton(getResources().getString(R.string.opcion_list_monitoring_dialog_acept),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reportes = monitoreoDbHelper.getMonitoreos();
                        tvInfo.setVisibility(View.INVISIBLE);
                        new HackingBackgroundTask().execute();
                        builder.create().dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.opcion_list_monitoring_dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.create().dismiss();
                    }
                });

        return builder.create();
    }

    /**
     * =============================================================================================
     * METODO: Presenta en Interfaz un mensaje tipo dialog con los datos correspondientes a las
     * Zonas y asi aplicar los filtros correspondientes para la consulta de monitoreos
     **/
    public AlertDialog createRadioListDialog(final CharSequence[] items, final String title, final int zn) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title)
                .setSingleChoiceItems(items, pos[zn - 1], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (zn) {
                            //----------------------------------------------------------------------
                            case 1:
                                item = items_tramos[which].toString();
                                cursor = tramoDbHelper.generateConditionalQuery(new String[]{item}, TramoEntry.DESCRIPCION);
                                cursor.moveToFirst();
                                op[0] = cursor.getInt(1);
                                pos[0] = which;

                                cleanVectors(1);
                                inhabilitarFiltros(1);

                                cursor = subtramoDbHelper.generateConditionalQuery(new String[]{op[0] + ""}, SubtramoEntry.TRAMO);
                                items_subtramos = dataFilter(cursor, 2);
                                break;
                            //----------------------------------------------------------------------
                            case 2:
                                item = items_subtramos[which].toString();
                                cursor = subtramoDbHelper.generateConditionalQuery(new String[]{item}, SubtramoEntry.DESCRIPCION);
                                cursor.moveToFirst();
                                op[1] = cursor.getInt(1);
                                pos[1] = which;

                                cleanVectors(2);
                                inhabilitarFiltros(2);

                                cursor = seccionDbHelper.generateConditionalQuery(new String[]{op[1] + ""}, SeccionEntry.SUBTRAMO);
                                items_secciones = dataFilter(cursor, 2);
                                break;
                            //----------------------------------------------------------------------
                            case 3:
                                item = items_secciones[which].toString();
                                cursor = seccionDbHelper.generateConditionalQuery(new String[]{item}, SeccionEntry.DESCRIPCION);
                                cursor.moveToFirst();
                                op[2] = cursor.getInt(1);
                                pos[2] = which;

                                cleanVectors(3);
                                inhabilitarFiltros(3);

                                cursor = areaDbHelper.generateConditionalQuery(new String[]{op[2] + ""}, AreaEntry.SECCION);
                                items_areas = dataFilter(cursor, 3);
                                items_tipos = dataFilterTipos(cursor, 2, 3);
                                break;
                            //----------------------------------------------------------------------
                            case 4:
                                item = items_areas[which].toString();
                                cursor = areaDbHelper.generateConditionalQuery(new String[]{item}, AreaEntry.PROPIEDAD_NOMINADA);
                                cursor.moveToFirst();
                                op[3] = cursor.getInt(1);
                                pos[3] = which;
                                break;
                            //----------------------------------------------------------------------
                            default:
                                break;
                        }

                    }
                })
                .setNegativeButton(getResources().getString(R.string.opcion_list_monitoring_dialog_ok),
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
     * METODO: Procesa todos los eventos de tipo onClick de cada uno de los botones de la Interfaz
     **/
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.fab_tramo:
                createRadioListDialog(items_tramos, getResources().getString(R.string.descriptionTramo), 1).show();
                break;
            case R.id.fab_subtramo:
                createRadioListDialog(items_subtramos, getResources().getString(R.string.descriptionSubtramo), 2).show();
                break;
            case R.id.fab_seccion:
                createRadioListDialog(items_secciones, getResources().getString(R.string.descriptionSeccion), 3).show();
                break;
            case R.id.fab_area:
                createRadioListDialog(items_tipos, getResources().getString(R.string.descriptionPropiedad), 4).show();
                break;
            case R.id.fab_new:
                if (op[3] != 0) {
                    fragment = new MonitoringRegistrationFormFragment();
                    Bundle params = new Bundle();
                    params.putString("AREA", op[3] + "");
                    params.putString("OPCION", "N");
                    fragment.setArguments(params);

                    //Inflamos el layout para el Fragmento MonitoringListFragment
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack("MonitoringList")
                            .replace(R.id.monitoring_principal_context, fragment)
                            .commit();
                } else
                    createSimpleDialog(getResources().getString(R.string.descriptionListMonitoringDialog), getResources().getString(R.string.titleListMonitoringDialog)).show();
                break;
            case R.id.fab_search:
                if(wifiConected()){
                    if (op[0] == 0) createSimpleDialog(getResources().getString(R.string.institutional_list_dialog_description), getResources().getString(R.string.institutioanl_list_dialog_title)).show();
                    else {
                        menu.collapse();
                        final ProgressDialog loading = new ProgressDialog(getActivity());
                        loading.setMessage(getResources().getString(R.string.list_monitoring_process_message));
                        loading.setTitle(getResources().getString(R.string.list_monitoring_process_message_search));
                        loading.setProgress(10);
                        loading.setIndeterminate(true);
                        loading.show();

                        Call<ReporteResponse> call = AylluApiAdapter.getApiService("REPORTE").getReporte(op[0], op[1], op[2], op[3]);
                        call.enqueue(new Callback<ReporteResponse>() {
                            @Override
                            public void onResponse(Call<ReporteResponse> call, Response<ReporteResponse> response) {
                                if (response.isSuccessful()) {
                                    reportes = response.body().getReportes();
                                    if (reportes.size() > 0) tvInfo.setVisibility(View.INVISIBLE);
                                    if (reportes.size() == 0) {
                                        tvInfo.setText(getResources().getString(R.string.descriptionInfoListMonitoringNegative));
                                        tvInfo.setVisibility(View.VISIBLE);
                                    }
                                    new HackingBackgroundTask().execute();
                                    loading.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<ReporteResponse> call, Throwable t) {
                                loading.dismiss();
                                Toast.makeText(
                                        getActivity(),
                                        getResources().getString(R.string.list_monitoring_process_message_server),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }
                } else {
                    createOfflineMonitoringDialog(
                            getResources().getString(R.string.list_monitoring_description_dialog_offline)+
                            "\n"+getResources().getString(R.string.list_monitoring_description_dialog_offline_question),
                            getResources().getString(R.string.list_monitoring_title_dialog_offline)).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    private void setupReporteList() {
        mReporteList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReporteList.setAdapter(adapter);
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onMenuExpanded() {
        animationButton(fab_new, 1, 1);
        animationButton(fab_search, 1, 1);
        fab_new.setEnabled(true);
        fab_search.setEnabled(true);
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onMenuCollapsed() {
        fab_new.setEnabled(false);
        fab_search.setEnabled(false);

        animationButton(fab_new, 0, 0);
        animationButton(fab_search, 0, 0);
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

    /**
     * =============================================================================================
     * METODO:
     **/
    private class HackingBackgroundTask extends AsyncTask<Void, Void, ArrayList<Reporte>> {

        static final int DURACION = 100;

        @Override
        protected ArrayList<Reporte> doInBackground(Void... params) {
            // Simulación de la carga de items
            try {
                Thread.sleep(DURACION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Retornar en nuevos elementos para el adaptador
            return reportes;
        }

        @Override
        protected void onPostExecute(ArrayList<Reporte> result) {
            super.onPostExecute(result);

            // Limpiar elementos antiguos
            adapter.clear();

            // Añadir elementos nuevos
            adapter.addAll(result);
        }

    }

    /**
     * =============================================================================================
     * METODO:
     **/
    public void cleanVectors(int initial) {
        for (i = initial; i < 4; i++) {
            pos[i] = -1;
            op[i] = 0;
        }
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    public CharSequence[] dataFilter(Cursor cur, int position) {
        i = 0;
        CharSequence[] items = new CharSequence[0];

        if (cur.moveToFirst()) {
            items = new CharSequence[cursor.getCount()];
            do {
                items[i] = cursor.getString(position);
                i++;
            } while (cursor.moveToNext());
        }
        return items;
    }


    /**
     * =============================================================================================
     * METODO:
     **/
    public CharSequence[] dataFilterTipos(Cursor cur, int position, int position2) {
        i = 0;
        CharSequence[] items = new CharSequence[0];

        if (cur.moveToFirst()) {
            items = new CharSequence[cursor.getCount()];
            do {
                items[i] = cursor.getString(position) + " - " + cursor.getString(position2);
                i++;
            } while (cursor.moveToNext());
        }
        return items;
    }
    /**
     * =============================================================================================
     * METODO:
     **/
    public void comprobarFiltros(){
        if(pos[0] >= 0 ) fab_tramo.setEnabled(true);
        if(pos[1] >= 0 ) fab_subtramo.setEnabled(true);
        else fab_subtramo.setEnabled(false);
        if(pos[2] >= 0 ) fab_seccion.setEnabled(true);
        else fab_seccion.setEnabled(false);
        if(pos[3] >= 0 ) fab_area.setEnabled(true);
        else fab_area.setEnabled(false);
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    public void inhabilitarFiltros(int num_zn) {
        switch (num_zn) {
            case 1:
                items_subtramos = null;
                items_secciones = null;
                items_areas = null;
                fab_subtramo.setEnabled(true);
                fab_seccion.setEnabled(false);
                fab_area.setEnabled(false);
                break;
            case 2:
                items_secciones = null;
                items_areas = null;
                fab_seccion.setEnabled(true);
                fab_area.setEnabled(false);
                break;
            case 3:
                items_areas = null;
                fab_area.setEnabled(true);
                break;
            default:
                break;
        }
    }

    //==============================================================================================
    //METODO: Verifica la conexion a internet
    protected Boolean wifiConected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnected() || mobile.isConnected())return true;
        else return false;
    }
}
