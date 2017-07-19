package com.example.edwin.ayllu.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.FormRespuesta;
import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.io.RestClient;
import com.example.edwin.ayllu.domain.AreaContract;
import com.example.edwin.ayllu.domain.AreaDbHelper;
import com.example.edwin.ayllu.domain.Monitoreo;
import com.example.edwin.ayllu.domain.PaisDbHelper;
import com.example.edwin.ayllu.domain.SeccionContract;
import com.example.edwin.ayllu.domain.SeccionDbHelper;
import com.example.edwin.ayllu.domain.SubtramoContract;
import com.example.edwin.ayllu.domain.SubtramoDbHelper;
import com.example.edwin.ayllu.domain.TramoContract;
import com.example.edwin.ayllu.domain.TramoDbHelper;
import com.example.edwin.ayllu.ui.adapter.InstitutionalAdaprter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstitutionalListFragment extends Fragment implements View.OnClickListener, FloatingActionsMenu.OnFloatingActionsMenuUpdateListener{
    //VARIABLES DE VISTA
    private InstitutionalAdaprter adapter;
    private RecyclerView mReporteList;
    private FloatingActionButton fab_tramo, fab_subtramo, fab_seccion, fab_area;
    private FloatingActionButton fab_search;
    private FloatingActionsMenu menu;
    private TextView tvInfo;

    //VARIABLES DATOS TEMPORALES
    ArrayList<Monitoreo> monitoreos = new ArrayList<>();
    CharSequence[] items_tramos, items_subtramos, items_secciones, items_areas, items_tipos;
    int[] op = {0, 0, 0, 0};
    int[] pos = {-1, -1, -1, -1};

    Interpolator interpolador;
    String item = "";
    String cod_mon = "", pais_mon = "";
    int i = 0;

    //VARIABLES CONTROL DE DATOS FIJOS
    PaisDbHelper paisDbHelper;
    TramoDbHelper tramoDbHelper;
    SubtramoDbHelper subtramoDbHelper;
    SeccionDbHelper seccionDbHelper;
    AreaDbHelper areaDbHelper;
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
        adapter = new InstitutionalAdaprter(getActivity());

        paisDbHelper = new PaisDbHelper(getActivity());
        tramoDbHelper = new TramoDbHelper(getActivity());
        subtramoDbHelper = new SubtramoDbHelper(getActivity());
        seccionDbHelper = new SeccionDbHelper(getActivity());
        areaDbHelper = new AreaDbHelper(getActivity());
        int i = 0;
        //------------------------------------------------------------------------------------------
        //Obtenemos el codigo del monitor y el pais del usuario en sesión
        Intent intent = getActivity().getIntent();
        cod_mon = intent.getStringExtra("MONITOR");
        pais_mon = intent.getStringExtra("PAIS");
        //------------------------------------------------------------------------------------------
        //Obtenemos los tramos correspondientes al pais del usuario actual
        cursor = tramoDbHelper.generateConditionalQuery(new String[]{pais_mon}, TramoContract.TramoEntry.PAIS);
        if (cursor.moveToFirst()) {
            items_tramos = new CharSequence[cursor.getCount()];
            do {
                items_tramos[i] = cursor.getString(2);
                i++;
            } while (cursor.moveToNext());
        }
    }

    /**
     * =============================================================================================
     * METODO: Encargado de activarse cuando se cree la vista y cargar con datos el RecyclerView
     **/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_institutional_list, container, false);

        fab_search = (FloatingActionButton) root.findViewById(R.id.fab_search);
        fab_tramo = (FloatingActionButton) root.findViewById(R.id.fab_tramo);
        fab_subtramo = (FloatingActionButton) root.findViewById(R.id.fab_subtramo);
        fab_seccion = (FloatingActionButton) root.findViewById(R.id.fab_seccion);
        fab_area = (FloatingActionButton) root.findViewById(R.id.fab_area);
        menu = (FloatingActionsMenu) root.findViewById(R.id.menu_fab);

        tvInfo = (TextView) root.findViewById(R.id.tv_info);

        fab_search.setScaleX(0);
        fab_search.setScaleY(0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            interpolador = AnimationUtils.loadInterpolator(getActivity().getBaseContext(),
                    android.R.interpolator.fast_out_slow_in);
        }


        fab_subtramo.setEnabled(false);
        fab_seccion.setEnabled(false);
        fab_area.setEnabled(false);
        fab_search.setEnabled(false);

        fab_tramo.setOnClickListener(this);
        fab_subtramo.setOnClickListener(this);
        fab_seccion.setOnClickListener(this);
        fab_area.setOnClickListener(this);
        menu.setOnFloatingActionsMenuUpdateListener(this);
        fab_search.setOnClickListener(this);

        mReporteList = (RecyclerView) root.findViewById(R.id.institutional_list);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (monitoreos.size() > 0) {
                    Monitoreo m = monitoreos.get(mReporteList.getChildAdapterPosition(view));

                    Bundle parametro = new Bundle();
                    parametro.putString("pa", m.getCodigo());
                    parametro.putString("fm", m.getDate());

                    Intent intent = new Intent(getActivity(), FormRespuesta.class);
                    intent.putExtras(parametro);
                    intent.putExtra("MONITOR", cod_mon);
                    intent.putExtra("PAIS", pais_mon);
                    startActivity(intent);
                }
            }
        });

        setupReporteList();
        return root;
        //return inflater.inflate(R.layout.fragment_institutional_list, container, false);
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onResume() {
        super.onResume();
        if (monitoreos.size() > 0) tvInfo.setVisibility(View.INVISIBLE);
        comprobarFiltros();
        menu.collapse();
        if (op[0] != 0) getListReports(op[0]+"",op[1]+"",op[2]+"",op[3]+"");

    }

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
        tvTitle.setCompoundDrawables(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_question), null, null, null);
        tvDescription.setText(mensaje);

        builder.setPositiveButton(getResources().getString(R.string.institutional_list_dialog_option_ok),
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
                                cursor = tramoDbHelper.generateConditionalQuery(new String[]{item}, TramoContract.TramoEntry.DESCRIPCION);
                                cursor.moveToFirst();
                                op[0] = cursor.getInt(1);
                                pos[0] = which;

                                cleanVectors(1);
                                inhabilitarFiltros(1);

                                cursor = subtramoDbHelper.generateConditionalQuery(new String[]{op[0] + ""}, SubtramoContract.SubtramoEntry.TRAMO);
                                items_subtramos = dataFilter(cursor, 2);
                                break;
                            //----------------------------------------------------------------------
                            case 2:
                                item = items_subtramos[which].toString();
                                cursor = subtramoDbHelper.generateConditionalQuery(new String[]{item}, SubtramoContract.SubtramoEntry.DESCRIPCION);
                                cursor.moveToFirst();
                                op[1] = cursor.getInt(1);
                                pos[1] = which;

                                cleanVectors(2);
                                inhabilitarFiltros(2);

                                cursor = seccionDbHelper.generateConditionalQuery(new String[]{op[1] + ""}, SeccionContract.SeccionEntry.SUBTRAMO);
                                items_secciones = dataFilter(cursor, 2);
                                break;
                            //----------------------------------------------------------------------
                            case 3:
                                item = items_secciones[which].toString();
                                cursor = seccionDbHelper.generateConditionalQuery(new String[]{item}, SeccionContract.SeccionEntry.DESCRIPCION);
                                cursor.moveToFirst();
                                op[2] = cursor.getInt(1);
                                pos[2] = which;

                                cleanVectors(3);
                                inhabilitarFiltros(3);

                                cursor = areaDbHelper.generateConditionalQuery(new String[]{op[2] + ""}, AreaContract.AreaEntry.SECCION);
                                items_areas = dataFilter(cursor, 3);
                                items_tipos = dataFilterTipos(cursor, 2, 3);
                                break;
                            //----------------------------------------------------------------------
                            case 4:
                                item = items_areas[which].toString();
                                cursor = areaDbHelper.generateConditionalQuery(new String[]{item}, AreaContract.AreaEntry.PROPIEDAD_NOMINADA);
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
                .setNegativeButton(getResources().getString(R.string.institutional_list_dialog_option_ok),
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
                createRadioListDialog(items_tramos, getResources().getString(R.string.institutional_list_button_tramo), 1).show();
                break;
            case R.id.fab_subtramo:
                createRadioListDialog(items_subtramos, getResources().getString(R.string.institutional_list_button_subtramo), 2).show();
                break;
            case R.id.fab_seccion:
                createRadioListDialog(items_secciones, getResources().getString(R.string.institutional_list_button_seccion), 3).show();
                break;
            case R.id.fab_area:
                createRadioListDialog(items_tipos, getResources().getString(R.string.institutional_list_button_propiedad), 4).show();
                break;
            case R.id.fab_search:
                if (op[0] != 0) {
                    menu.collapse();
                    getListReports(op[0]+"",op[1]+"",op[2]+"",op[3]+"");
                }
                else
                    createSimpleDialog(getResources().getString(R.string.institutional_list_dialog_description), getResources().getString(R.string.institutioanl_list_dialog_title)).show();
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
        animationButton(fab_search, 1, 1);
        fab_search.setEnabled(true);
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onMenuCollapsed() {
        fab_search.setEnabled(false);
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
    private class HackingBackgroundTask extends AsyncTask<Void, Void, ArrayList<Monitoreo>> {

        static final int DURACION = 1;

        @Override
        protected ArrayList<Monitoreo> doInBackground(Void... params) {
            // Simulación de la carga de items
            try {
                Thread.sleep(DURACION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Retornar en nuevos elementos para el adaptador
            return monitoreos;
        }

        @Override
        protected void onPostExecute(ArrayList result) {
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

    /**
     * =============================================================================================
     * METODO:
     **/
    public void getListReports(String tramo, String subtramo, String seccion, String area){
        final ProgressDialog loading = ProgressDialog.show(getActivity(),getResources().getString(R.string.institutional_list_process_message_search),getResources().getString(R.string.institutional_list_process_message),false,false);
        RestClient service = RestClient.retrofit.create(RestClient.class);
        Log.e("Datos","tramo: "+tramo);
        Call<ArrayList<Monitoreo>> requestUser = service.monitoreos(tramo, subtramo, seccion, area);
        requestUser.enqueue(new Callback<ArrayList<Monitoreo>>() {
            @Override
            public void onResponse(Call<ArrayList<Monitoreo>> call, Response<ArrayList<Monitoreo>> response) {
                if (response.isSuccessful()) {
                    monitoreos = response.body();
                    if(response.body().size()==0){
                        tvInfo.setText(getResources().getString(R.string.institutional_list_message_negative));
                        tvInfo.setVisibility(View.VISIBLE);
                    }
                    else tvInfo.setVisibility(View.INVISIBLE);
                    new HackingBackgroundTask().execute();
                    loading.dismiss();

                }
                else{
                    Toast.makeText(
                            getActivity(),
                            getResources().getString(R.string.institutional_list_process_message_negative),
                            Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<ArrayList<Monitoreo>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(
                        getActivity(),
                        getResources().getString(R.string.institutional_list_process_message_server),
                        Toast.LENGTH_LONG).show();
            }
        });
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
}
