package com.example.edwin.ayllu.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageButton;

import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.domain.Area;
import com.example.edwin.ayllu.domain.AreaDbHelper;
import com.example.edwin.ayllu.domain.PaisDbHelper;
import com.example.edwin.ayllu.domain.Reporte;
import com.example.edwin.ayllu.domain.Seccion;
import com.example.edwin.ayllu.domain.SeccionDbHelper;
import com.example.edwin.ayllu.domain.Subtramo;
import com.example.edwin.ayllu.domain.SubtramoDbHelper;
import com.example.edwin.ayllu.domain.Tramo;
import com.example.edwin.ayllu.domain.TramoDbHelper;
import com.example.edwin.ayllu.io.AylluApiAdapter;
import com.example.edwin.ayllu.io.model.ReporteResponse;
import com.example.edwin.ayllu.ui.adapter.ReporteAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import static com.example.edwin.ayllu.domain.TramoContract.TramoEntry;
import static com.example.edwin.ayllu.domain.SubtramoContract.SubtramoEntry;
import static com.example.edwin.ayllu.domain.SeccionContract.SeccionEntry;
import static com.example.edwin.ayllu.domain.AreaContract.AreaEntry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonitoringListFragment extends Fragment implements View.OnClickListener, FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
    private ReporteAdapter adapter;
    private RecyclerView mReporteList;
    private FloatingActionButton fab_tramo, fab_subtramo, fab_seccion, fab_area;
    private FloatingActionButton fab_search, fab_new;
    private FloatingActionsMenu menu;

    View root;

    Interpolator interpolador;

    ArrayList<Reporte> reportes = new ArrayList<>();
    ArrayList<Tramo> tramos = new ArrayList<>();
    ArrayList<Subtramo> subtramos = new ArrayList<>();
    ArrayList<Seccion> secciones = new ArrayList<>();
    ArrayList<Area> areas = new ArrayList<>();

    CharSequence[] items_tramos, items_subtramos, items_secciones, items_areas;

    int[] op = {0, 0, 0, 0};
    int[] pos = {-1, -1, -1, -1};

    String item = "";
    Cursor cursor;
    int i = 0;

    PaisDbHelper paisDbHelper;
    TramoDbHelper tramoDbHelper;
    SubtramoDbHelper subtramoDbHelper;
    SeccionDbHelper seccionDbHelper;
    AreaDbHelper areaDbHelper;

    private String cod_mon = "", pais_mon = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ReporteAdapter(getActivity());

        Intent intent = getActivity().getIntent();
        cod_mon = intent.getStringExtra("MONITOR");
        pais_mon = intent.getStringExtra("PAIS");

        paisDbHelper = new PaisDbHelper(getActivity());
        tramoDbHelper = new TramoDbHelper(getActivity());
        subtramoDbHelper = new SubtramoDbHelper(getActivity());
        seccionDbHelper = new SeccionDbHelper(getActivity());
        areaDbHelper = new AreaDbHelper(getActivity());
        int i = 0;

        cursor = tramoDbHelper.generateConditionalQuery(new String[]{pais_mon}, TramoEntry.PAIS);
        if (cursor.moveToFirst()) {
            items_tramos = new CharSequence[cursor.getCount()];
            do {
                tramos.add(new Tramo(cursor.getInt(1), cursor.getString(2), cursor.getString(3)));
                items_tramos[i] = cursor.getString(2);
                i++;
            } while (cursor.moveToNext());
        }
    }

    //==============================================================================================
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_monitoring_list, container, false);
        mReporteList = (RecyclerView) root.findViewById(R.id.monitoring_list);
        setupReporteList();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        fab_new = (FloatingActionButton) getActivity().findViewById(R.id.fab_new);
        fab_search = (FloatingActionButton) getActivity().findViewById(R.id.fab_search);

        fab_new.setScaleX(0);
        fab_search.setScaleX(0);

        fab_new.setScaleY(0);
        fab_search.setScaleY(0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            interpolador = AnimationUtils.loadInterpolator(getActivity().getBaseContext(),
                    android.R.interpolator.fast_out_slow_in);
        }

        fab_tramo = (FloatingActionButton) getActivity().findViewById(R.id.fab_tramo);
        fab_subtramo = (FloatingActionButton) getActivity().findViewById(R.id.fab_subtramo);
        fab_seccion = (FloatingActionButton) getActivity().findViewById(R.id.fab_seccion);
        fab_area = (FloatingActionButton) getActivity().findViewById(R.id.fab_area);
        menu = (FloatingActionsMenu) getActivity().findViewById(R.id.menu_fab);

        fab_subtramo.setEnabled(false);
        fab_seccion.setEnabled(false);
        fab_area.setEnabled(false);

        fab_tramo.setOnClickListener(this);
        fab_subtramo.setOnClickListener(this);
        fab_seccion.setOnClickListener(this);
        fab_area.setOnClickListener(this);
        menu.setOnFloatingActionsMenuUpdateListener(this);
        fab_search.setOnClickListener(this);
    }

    public AlertDialog createSimpleDialog(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        });

        return builder.create();
    }

    public AlertDialog createRadioListDialog(final CharSequence[] items, final String title, final int zn) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title)
                .setSingleChoiceItems(items, pos[zn - 1], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (zn) {
                            case 1:
                                item = items_tramos[which].toString();
                                cursor = tramoDbHelper.generateConditionalQuery(new String[]{item}, TramoEntry.DESCRIPCION);
                                cursor.moveToFirst();

                                op[0] = cursor.getInt(1);
                                pos[0] = which;
                                for (i = 1; i < op.length; i++) {
                                    pos[i] = -1;
                                    op[i] = 0;
                                }

                                items_subtramos = null;
                                items_secciones = null;
                                items_areas = null;

                                i = 0;
                                cursor = subtramoDbHelper.generateConditionalQuery(new String[]{op[0] + ""}, SubtramoEntry.TRAMO);
                                if (cursor.moveToFirst()) {
                                    items_subtramos = new CharSequence[cursor.getCount()];
                                    do {
                                        subtramos.add(new Subtramo(cursor.getInt(1), cursor.getString(2), cursor.getInt(3)));
                                        items_subtramos[i] = cursor.getString(2);
                                        i++;
                                    } while (cursor.moveToNext());
                                }

                                fab_subtramo.setEnabled(true);
                                fab_seccion.setEnabled(false);
                                fab_area.setEnabled(false);

                                break;
                            case 2:
                                item = items_subtramos[which].toString();
                                cursor = subtramoDbHelper.generateConditionalQuery(new String[]{item}, SubtramoEntry.DESCRIPCION);
                                cursor.moveToFirst();

                                op[1] = cursor.getInt(1);
                                pos[1] = which;
                                for (i = 2; i < op.length; i++) {
                                    op[i] = 0;
                                    pos[i] = -1;
                                }

                                items_secciones = null;
                                items_areas = null;

                                i = 0;
                                cursor = seccionDbHelper.generateConditionalQuery(new String[]{op[1] + ""}, SeccionEntry.SUBTRAMO);
                                if (cursor.moveToFirst()) {
                                    items_secciones = new CharSequence[cursor.getCount()];
                                    do {
                                        secciones.add(new Seccion(cursor.getInt(1), cursor.getString(2), cursor.getInt(3)));
                                        items_secciones[i] = cursor.getString(2);
                                        i++;
                                    } while (cursor.moveToNext());
                                }

                                fab_seccion.setEnabled(true);
                                fab_area.setEnabled(false);

                                break;
                            case 3:
                                item = items_secciones[which].toString();
                                cursor = seccionDbHelper.generateConditionalQuery(new String[]{item}, SeccionEntry.DESCRIPCION);
                                cursor.moveToFirst();

                                op[2] = cursor.getInt(1);
                                pos[2] = which;
                                for (i = 3; i < op.length; i++) {
                                    op[i] = 0;
                                    pos[i] = -1;
                                }

                                items_areas = null;

                                i = 0;
                                cursor = areaDbHelper.generateConditionalQuery(new String[]{op[2] + ""}, AreaEntry.SECCION);
                                if (cursor.moveToFirst()) {
                                    items_areas = new CharSequence[cursor.getCount()];
                                    do {
                                        areas.add(new Area(cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4)));
                                        items_areas[i] = cursor.getString(3);
                                        i++;
                                    } while (cursor.moveToNext());
                                }

                                fab_area.setEnabled(true);

                                break;
                            case 4:
                                item = items_areas[which].toString();
                                cursor = areaDbHelper.generateConditionalQuery(new String[]{item}, AreaEntry.PROPIEDAD_NOMINADA);
                                cursor.moveToFirst();

                                op[3] = cursor.getInt(1);
                                pos[3] = which;
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

    //==============================================================================================
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.fab_tramo:
                createRadioListDialog(items_tramos, "TRAMOS", 1).show();
                break;
            case R.id.fab_subtramo:
                createRadioListDialog(items_subtramos, "SUBTRAMOS", 2).show();
                break;
            case R.id.fab_seccion:
                createRadioListDialog(items_secciones, "SECCIONES", 3).show();
                break;
            case R.id.fab_area:
                createRadioListDialog(items_areas, "AREAS", 4).show();
                break;
            case R.id.fab_search:
                menu.collapse();

                Call<ReporteResponse> call = AylluApiAdapter.getApiService("REPORTE").getReporte(op[0], op[1], op[2], op[3]);
                call.enqueue(new Callback<ReporteResponse>() {
                    @Override
                    public void onResponse(Call<ReporteResponse> call, Response<ReporteResponse> response) {
                        if (response.isSuccessful()) {
                            reportes = response.body().getReportes();
                            adapter.addAll(reportes);
                            new HackingBackgroundTask().execute();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReporteResponse> call, Throwable t) {
                    }
                });
                break;
            default:
                break;
        }
    }

    //==============================================================================================
    private void setupReporteList() {
        mReporteList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReporteList.setAdapter(adapter);
    }

    //==============================================================================================
    @Override
    public void onMenuExpanded() {
        animationButton(fab_new, 1, 1);
        animationButton(fab_search, 1, 1);
    }

    //==============================================================================================
    @Override
    public void onMenuCollapsed() {
        animationButton(fab_new, 0, 0);
        animationButton(fab_search, 0, 0);
    }

    //==============================================================================================
    public void animationButton(FloatingActionButton fb, float x, float y) {
        fb.animate()
                .scaleX(x)
                .scaleY(y)
                .setInterpolator(interpolador)
                .setDuration(50)
                .setStartDelay(500);
    }

    //==============================================================================================
    private class HackingBackgroundTask extends AsyncTask<Void, Void, ArrayList<Reporte>> {

        static final int DURACION = 2 * 1000; // 3 segundos de carga

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
        protected void onPostExecute(ArrayList result) {
            super.onPostExecute(result);

            // Limpiar elementos antiguos
            adapter.clear();

            // Añadir elementos nuevos
            adapter.addAll(result);
        }

    }
}
