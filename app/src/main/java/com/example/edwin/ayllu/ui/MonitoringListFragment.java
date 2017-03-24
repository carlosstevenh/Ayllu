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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.ActividadEstadisticaPuntoAfactacion;
import com.example.edwin.ayllu.MonitoringRegistrationFormActivity;
import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.domain.AreaDbHelper;
import com.example.edwin.ayllu.domain.PaisDbHelper;
import com.example.edwin.ayllu.domain.Reporte;
import com.example.edwin.ayllu.domain.SeccionDbHelper;
import com.example.edwin.ayllu.domain.SubtramoDbHelper;
import com.example.edwin.ayllu.domain.TramoDbHelper;
import com.example.edwin.ayllu.io.AylluApiAdapter;
import com.example.edwin.ayllu.io.model.ReporteResponse;
import com.example.edwin.ayllu.ui.adapter.ReporteAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

import static com.example.edwin.ayllu.domain.TramoContract.TramoEntry;
import static com.example.edwin.ayllu.domain.SubtramoContract.SubtramoEntry;
import static com.example.edwin.ayllu.domain.SeccionContract.SeccionEntry;
import static com.example.edwin.ayllu.domain.AreaContract.AreaEntry;

import java.io.FileNotFoundException;
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
    private ReporteAdapter adapter;
    private RecyclerView mReporteList;
    private FloatingActionButton fab_tramo, fab_subtramo, fab_seccion, fab_area;
    private FloatingActionButton fab_search, fab_new, fab_report;
    private FloatingActionsMenu menu;
    private LinearLayout areaInfo;
    private TextView tvInfo;

    //VARIABLES DATOS TEMPORALES
    ArrayList<Reporte> reportes = new ArrayList<>();
    CharSequence[] items_tramos, items_subtramos, items_secciones, items_areas;
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
        adapter = new ReporteAdapter(getActivity());

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
        cursor = tramoDbHelper.generateConditionalQuery(new String[]{pais_mon}, TramoEntry.PAIS);
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
        View root = inflater.inflate(R.layout.fragment_monitoring_list, container, false);
        mReporteList = (RecyclerView) root.findViewById(R.id.monitoring_list);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reportes.size() > 0) {
                    Reporte reporte = reportes.get(mReporteList.getChildAdapterPosition(view));
                    createMonitoringDialog(reporte).show();
                }
            }
        });

        setupReporteList();
        return root;
    }

    /**=============================================================================================
     * METODO: Encargado de de redirigir al usuario al Formulario de Registro con la información del
     * punto seleccionado
     **/


    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onResume() {
        super.onResume();

        fab_report = (FloatingActionButton) getActivity().findViewById(R.id.fab_reporte);
        fab_new = (FloatingActionButton) getActivity().findViewById(R.id.fab_new);
        fab_search = (FloatingActionButton) getActivity().findViewById(R.id.fab_search);
        fab_tramo = (FloatingActionButton) getActivity().findViewById(R.id.fab_tramo);
        fab_subtramo = (FloatingActionButton) getActivity().findViewById(R.id.fab_subtramo);
        fab_seccion = (FloatingActionButton) getActivity().findViewById(R.id.fab_seccion);
        fab_area = (FloatingActionButton) getActivity().findViewById(R.id.fab_area);
        menu = (FloatingActionsMenu) getActivity().findViewById(R.id.menu_fab);

        areaInfo = (LinearLayout) getActivity().findViewById(R.id.area_info);
        tvInfo = (TextView) getActivity().findViewById(R.id.tv_info);

        fab_new.setScaleX(0);
        fab_search.setScaleX(0);
        fab_new.setScaleY(0);
        fab_search.setScaleY(0);
        fab_report.setScaleY(0);
        fab_report.setScaleY(0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            interpolador = AnimationUtils.loadInterpolator(getActivity().getBaseContext(),
                    android.R.interpolator.fast_out_slow_in);
        }


        fab_subtramo.setEnabled(false);
        fab_seccion.setEnabled(false);
        fab_area.setEnabled(false);
        fab_report.setEnabled(false);
        fab_new.setEnabled(false);
        fab_search.setEnabled(false);

        fab_tramo.setOnClickListener(this);
        fab_subtramo.setOnClickListener(this);
        fab_seccion.setOnClickListener(this);
        fab_area.setOnClickListener(this);
        menu.setOnFloatingActionsMenuUpdateListener(this);
        fab_search.setOnClickListener(this);
        fab_new.setOnClickListener(this);
        fab_report.setOnClickListener(this);
    }

    /**
     * =============================================================================================
     * METODO: Presenta en Interfaz un dialogo para Monitorear un Punto de Afectación
     **/
    public AlertDialog createMonitoringDialog(final Reporte currentReport) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("MONITOREAR UN PUNTO")
                .setMessage("¿Quiere monitorear el punto de afectación seleccionado?")
                .setPositiveButton("ACEPTAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getActivity(), MonitoringRegistrationFormActivity.class);
                                intent.putExtra("MONITOR", cod_mon + "");
                                intent.putExtra("PAIS", pais_mon);
                                intent.putExtra("PUNTO", currentReport.getCod_paf() + "");
                                intent.putExtra("OPCION", "M");
                                startActivity(intent);
                                getActivity().finish();
                            }
                        })
                .setNegativeButton("CANCELAR",
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
     * METODO: Procesa todos los eventos de tipo onClick de cada uno de los botones de la Interfaz
     **/
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
            case R.id.fab_new:
                if (op[3] != 0) {
                    Intent intent = new Intent(getActivity(), MonitoringRegistrationFormActivity.class);
                    intent.putExtra("MONITOR", cod_mon + "");
                    intent.putExtra("PAIS", pais_mon);
                    intent.putExtra("AREA", op[3] + "");
                    intent.putExtra("OPCION", "N");
                    startActivity(intent);
                    getActivity().finish();
                } else
                    createSimpleDialog("Seleciona un área para registrar un Monitoreos", "INFORMACIÓN").show();
                break;
            case R.id.fab_search:
                menu.collapse();

                Call<ReporteResponse> call = AylluApiAdapter.getApiService("REPORTE").getReporte(op[0], op[1], op[2], op[3]);
                call.enqueue(new Callback<ReporteResponse>() {
                    @Override
                    public void onResponse(Call<ReporteResponse> call, Response<ReporteResponse> response) {
                        if (response.isSuccessful()) {
                            reportes = response.body().getReportes();
                            if (reportes.size() > 0) areaInfo.setVisibility(View.INVISIBLE);
                            new HackingBackgroundTask().execute();
                            if(reportes.size() == 0){
                                tvInfo.setText("No hay monitoreos para la seleción actual.");
                                areaInfo.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ReporteResponse> call, Throwable t) {
                        Toast.makeText(
                                getActivity(),
                                getResources().getString(R.string.noSePudoConectarServidor),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                break;
            case R.id.fab_report:
                generateReport(reportes);
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
        animationButton(fab_report, 1, 1);

        fab_new.setEnabled(true);
        fab_search.setEnabled(true);
        fab_report.setEnabled(true);
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onMenuCollapsed() {
        fab_new.setEnabled(false);
        fab_search.setEnabled(false);
        fab_report.setEnabled(false);

        animationButton(fab_new, 0, 0);
        animationButton(fab_search, 0, 0);
        animationButton(fab_report, 0, 0);
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
    public void generateReport(ArrayList<Reporte> rp) {

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        String format = s.format(new Date());
        s = new SimpleDateFormat("HH:mm:ss");
        format += "[" + s.format(new Date()) + "]";
        //------------------------------------------------------------------------------------------
        //Escribiendo en el archivo Excel
        try {
            InputStream editor = getResources().openRawResource(R.raw.plantilla);
            FileOutputStream result = new FileOutputStream("/storage/sdcard0/Qhapaq-Ñan" + format + ".xls");

            //Crear el objeto que tendra el libro de Excel
            HSSFWorkbook workbook = new HSSFWorkbook(editor);
            HSSFWorkbook hssfWorkbookNew = new HSSFWorkbook();

            //1. Obtenemos la primera hoja del Excel
            //2. Llenamos la primera hoja del Excel
            HSSFSheet sheet = workbook.getSheetAt(0);
            escribirExcel(1, 12, sheet);

            //1. Obtenemos la segunda hoja del Excel
            //2. Llenamos la segunda hoja del Excel
            sheet = workbook.getSheetAt(1);
            escribirExcel(2, 6, sheet);

            //1. Obtenemos la tercera hoja del Excel
            //2. Llenamos la tercera hoja del Excel
            sheet = workbook.getSheetAt(2);
            escribirExcel(3, 6, sheet);

            workbook.write(result);
            result.close();
            workbook.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //==============================================================================================
    private void escribirExcel(int cod_plan, int pf, HSSFSheet sheet) {
        //Estilo de celda basico
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.DASHED);
        style.setBorderTop(BorderStyle.DASHED);
        style.setBorderRight(BorderStyle.DASHED);
        style.setBorderLeft(BorderStyle.DASHED);

        //Estilo de celda para Repercusiones y Origenes
        CellStyle style2 = sheet.getWorkbook().createCellStyle();
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        style2.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(BorderStyle.DASHED);
        style2.setBorderTop(BorderStyle.DASHED);
        style2.setBorderRight(BorderStyle.DASHED);
        style2.setBorderLeft(BorderStyle.DASHED);

        //Estilo de celda para Repercusiones y Origenes
        CellStyle style3 = sheet.getWorkbook().createCellStyle();
        style3.setAlignment(HorizontalAlignment.CENTER);
        style3.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
        style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style3.setBorderBottom(BorderStyle.DASHED);
        style3.setBorderTop(BorderStyle.DASHED);
        style3.setBorderRight(BorderStyle.DASHED);
        style3.setBorderLeft(BorderStyle.DASHED);

        //Variable para el punto de escritura
        int punto = pf;

        //------------------------------------------------------------------------------------------
        for (int i = 0; i < reportes.size(); i++) {
            HSSFRow fila = sheet.createRow(punto);
            HSSFCell celda;
            ArrayList<String> info = reportes.get(i).generarInfoPlantilla(cod_plan);
            punto++;
            //--------------------------------------------------------------------------------------
            for (int j = 0; j < info.size(); j++) {
                celda = fila.createCell(j);
                if (cod_plan == 1 && j > 6 && j < 11) {
                    if (info.get(j).equals("1")) celda.setCellStyle(style2);
                    else celda.setCellStyle(style);
                } else if (cod_plan == 1 && j > 10) {
                    if (info.get(j).equals("1")) celda.setCellStyle(style3);
                    else celda.setCellStyle(style);
                } else {
                    celda.setCellValue(info.get(j));
                    celda.setCellStyle(style);
                }
            }
        }
    }
}
