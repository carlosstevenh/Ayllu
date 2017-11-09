package com.qhapaq.nan.ayllu.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qhapaq.nan.ayllu.R;
import com.qhapaq.nan.ayllu.domain.pais.PaisContract;
import com.qhapaq.nan.ayllu.domain.usuario.UsuarioDbHelper;
import com.qhapaq.nan.ayllu.domain.area.AreaContract;
import com.qhapaq.nan.ayllu.domain.area.AreaDbHelper;
import com.qhapaq.nan.ayllu.domain.monitoreo.MonitoreoDbHelper;
import com.qhapaq.nan.ayllu.domain.pais.PaisDbHelper;
import com.qhapaq.nan.ayllu.domain.Reporte;
import com.qhapaq.nan.ayllu.domain.seccion.SeccionContract;
import com.qhapaq.nan.ayllu.domain.seccion.SeccionDbHelper;
import com.qhapaq.nan.ayllu.domain.subtramo.SubtramoContract;
import com.qhapaq.nan.ayllu.domain.subtramo.SubtramoDbHelper;
import com.qhapaq.nan.ayllu.domain.tramo.TramoContract;
import com.qhapaq.nan.ayllu.domain.tramo.TramoDbHelper;
import com.qhapaq.nan.ayllu.io.ApiConstants;
import com.qhapaq.nan.ayllu.io.AylluApiAdapter;
import com.qhapaq.nan.ayllu.io.AylluApiService;
import com.qhapaq.nan.ayllu.io.model.ReporteResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingsAppActivity extends AppCompatActivity implements View.OnClickListener {

    // Códigos de petición
    private static final int MY_WRITE_EXTERNAL_STORAGE = 123;

    LinearLayout opHabilitar, opElegir, opSalir, opAcerca, opAdministrar, opReporte;

    //VARIABLES DATOS TEMPORALES
    ArrayList<Reporte> reportes = new ArrayList<>();
    CharSequence[] items_tramos, items_subtramos, items_secciones, items_areas, items_tipos;
    int[] op = {0, 0, 0, 0};
    String opciones = "";
    String[] list_titles = new String[]{};
    ArrayList<String> list_zon = new ArrayList<>();

    String item = "";
    String monitor = "", pais = "", tipo = "", nombre_mon = "", nombre_pais = "";
    int i = 0;

    //VARIABLES CONTROL DE DATOS FIJOS
    PaisDbHelper paisDbHelper;
    TramoDbHelper tramoDbHelper;
    SubtramoDbHelper subtramoDbHelper;
    SeccionDbHelper seccionDbHelper;
    AreaDbHelper areaDbHelper;
    MonitoreoDbHelper monitoreoDbHelper;
    UsuarioDbHelper usuarioDbHelper;
    Cursor cursor;

    //Progresbar
    ProgressDialog loading;
    private int progressBarStatus = 0;
    private int sizeImg = 0;
    private int portj = 0;
    private int dif = 0;
    private Handler progressBarHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_app);

        paisDbHelper = new PaisDbHelper(this);
        tramoDbHelper = new TramoDbHelper(this);
        subtramoDbHelper = new SubtramoDbHelper(this);
        seccionDbHelper = new SeccionDbHelper(this);
        areaDbHelper = new AreaDbHelper(this);
        monitoreoDbHelper = new MonitoreoDbHelper(this);
        usuarioDbHelper = new UsuarioDbHelper(this);

        list_titles = new String[]{
                getResources().getString(R.string.info_critical_point_item_tramo),
                getResources().getString(R.string.info_critical_point_item_subtramo),
                getResources().getString(R.string.info_critical_point_item_seccion),
                getResources().getString(R.string.info_critical_point_item_propiedad)
        };

        int i = 0;
        //------------------------------------------------------------------------------------------
        //Obtenemos el codigo del monitor y el pais del usuario en sesión
        cursor = usuarioDbHelper.generateQuery("SELECT * FROM ");
        if (cursor.moveToFirst()) {
            monitor = cursor.getString(1);
            nombre_mon = cursor.getString(3);
            tipo = cursor.getString(5);
            pais = "0" + cursor.getString(7);
        }
        cursor.close();

        //------------------------------------------------------------------------------------------
        //Obtenemos el nombre del pais del monitor
        cursor = paisDbHelper.generateConditionalQuery(new String[]{pais}, PaisContract.PaisEntry.CODIGO);
        if (cursor.moveToFirst()) nombre_pais = cursor.getString(2);
        cursor.close();

        //------------------------------------------------------------------------------------------
        //Obtenemos los tramos correspondientes al pais del usuario actual
        cursor = tramoDbHelper.generateConditionalQuery(new String[]{pais}, TramoContract.TramoEntry.PAIS);
        if (cursor.moveToFirst()) {
            items_tramos = new CharSequence[cursor.getCount()];
            do {
                items_tramos[i] = cursor.getString(2);
                i++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        opHabilitar = (LinearLayout) findViewById(R.id.opcion_borrar);
        opElegir = (LinearLayout) findViewById(R.id.opcion_elegir);
        opSalir = (LinearLayout) findViewById(R.id.opcion_salir);
        opAcerca = (LinearLayout) findViewById(R.id.opcion_acerca);
        opAdministrar = (LinearLayout) findViewById(R.id.opcion_administrar);
        opReporte = (LinearLayout) findViewById(R.id.opcion_reporte);

        opHabilitar.setOnClickListener(this);
        opElegir.setOnClickListener(this);
        opAcerca.setOnClickListener(this);
        opAdministrar.setOnClickListener(this);
        opReporte.setOnClickListener(this);
        opSalir.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.opcion_borrar:
                createSimpleDialogBorrar(getResources().getString(R.string.borrarDatos), getResources().getString(R.string.title_warning)).show();
                break;
            case R.id.opcion_elegir:
                createRadioListDialog(items_tramos, getResources().getString(R.string.descriptionTramo), 1, "OFFLINE").show();
                break;
            case R.id.opcion_salir:
                createSimpleDialogSalir(getResources().getString(R.string.cerrarSesion), getResources().getString(R.string.title_warning)).show();
                break;
            case R.id.opcion_acerca:
                Intent in = new Intent(getApplicationContext(), AboutAppActivity.class);
                startActivity(in);
                finish();
                break;
            case R.id.opcion_administrar:
                if (tipo.equals("A")) {
                    Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                } else {
                    Toast login = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.noAdmin), Toast.LENGTH_SHORT);
                    login.show();
                }
                break;
            case R.id.opcion_reporte:
                createRadioListDialog(items_tramos, getResources().getString(R.string.descriptionTramo), 1, "REPORT").show();
                break;
        }
    }

    /**
     * =============================================================================================
     * METODO: Presenta en Interfaz un mensaje tipo dialog con los datos correspondientes a las
     * Zonas y asi aplicar los filtros correspondientes para la consulta de monitoreos
     **/
    public AlertDialog createRadioListDialog(final CharSequence[] items, final String title, final int zn, final String type) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (zn) {
                            //----------------------------------------------------------------------
                            case 1:
                                for (i = 0; i < 4; i++) op[i] = 0;
                                opciones = "";
                                list_zon.clear();

                                item = items_tramos[which].toString();
                                cursor = tramoDbHelper.generateConditionalQuery(new String[]{item}, TramoContract.TramoEntry.DESCRIPCION);
                                cursor.moveToFirst();
                                op[0] = cursor.getInt(1);
                                if (list_zon.size() == 1) list_zon.set(0, item);
                                else list_zon.add(item);

                                cursor = subtramoDbHelper.generateConditionalQuery(new String[]{op[0] + ""}, SubtramoContract.SubtramoEntry.TRAMO);
                                items_subtramos = dataFilter(cursor, 2);
                                break;
                            //----------------------------------------------------------------------
                            case 2:
                                for (i = 1; i < 4; i++) op[i] = 0;

                                item = items_subtramos[which].toString();
                                cursor = subtramoDbHelper.generateConditionalQuery(new String[]{item}, SubtramoContract.SubtramoEntry.DESCRIPCION);
                                cursor.moveToFirst();
                                op[1] = cursor.getInt(1);
                                if (list_zon.size() == 2) list_zon.set(1, item);
                                else list_zon.add(item);

                                cursor = seccionDbHelper.generateConditionalQuery(new String[]{op[1] + ""}, SeccionContract.SeccionEntry.SUBTRAMO);
                                items_secciones = dataFilter(cursor, 2);
                                break;
                            //----------------------------------------------------------------------
                            case 3:
                                for (i = 2; i < 4; i++) op[i] = 0;

                                item = items_secciones[which].toString();
                                cursor = seccionDbHelper.generateConditionalQuery(new String[]{item}, SeccionContract.SeccionEntry.DESCRIPCION);
                                cursor.moveToFirst();
                                op[2] = cursor.getInt(1);
                                if (list_zon.size() == 3) list_zon.set(2, item);
                                else list_zon.add(item);

                                cursor = areaDbHelper.generateConditionalQuery(new String[]{op[2] + ""}, AreaContract.AreaEntry.SECCION);
                                items_areas = dataFilter(cursor, 3);
                                items_tipos = dataFilterTipos(cursor, 2, 3);
                                break;
                            //----------------------------------------------------------------------
                            case 4:
                                for (i = 3; i < 4; i++) op[i] = 0;

                                item = items_areas[which].toString();
                                cursor = areaDbHelper.generateConditionalQuery(new String[]{item}, AreaContract.AreaEntry.PROPIEDAD_NOMINADA);
                                cursor.moveToFirst();
                                op[3] = cursor.getInt(1);
                                if (list_zon.size() == 4) list_zon.set(3, item);
                                else list_zon.add(item);

                                break;
                            //----------------------------------------------------------------------
                            default:
                                break;
                        }

                    }
                })
                .setPositiveButton(getResources().getString(R.string.settings_app_dialog_option_continue),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();

                                switch (zn) {
                                    case 1:
                                        if (op[0] != 0)
                                            createRadioListDialog(items_subtramos, getResources().getString(R.string.descriptionSubtramo), 2, type).show();
                                        else for (i = 0; i < 4; i++) op[i] = 0;
                                        break;
                                    case 2:
                                        if (op[1] != 0)
                                            createRadioListDialog(items_secciones, getResources().getString(R.string.descriptionSeccion), 3, type).show();
                                        else for (i = 0; i < 4; i++) op[i] = 0;
                                        break;
                                    case 3:
                                        if (op[2] != 0)
                                            createRadioListDialog(items_tipos, getResources().getString(R.string.descriptionPropiedad), 4, type).show();
                                        else for (i = 0; i < 4; i++) op[i] = 0;
                                        break;
                                    case 4:
                                        if (op[3] != 0) {
                                            opciones = "";
                                            for (int k = 0; k < list_zon.size(); k++)
                                                opciones += list_titles[k] + "\n(" + list_zon.get(k) + ")\n\n";
                                            createSimpleDialog(opciones, getResources().getString(R.string.settings_app_dialog_title), type).show();
                                        } else for (i = 0; i < 4; i++) op[i] = 0;
                                        break;
                                }
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.settings_app_dialog_option_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        opciones = "";
                        for (int k = 0; k < list_zon.size(); k++)
                            opciones += list_titles[k] + "\n(" + list_zon.get(k) + ")\n\n";
                        createSimpleDialog(opciones, getResources().getString(R.string.settings_app_dialog_title), type).show();
                    }
                });
        return builder.create();
    }

    //metodo encargado de crear los dialogos informativos
    public AlertDialog createSimpleDialogSalir(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton(getResources().getString(R.string.settings_app_dialog_option_confirm),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Borramos los datos del usuario logueado
                                usuarioDbHelper.deleteDataBase();
                                usuarioDbHelper.close();

                                //Borramos los datos de funcionamiento
                                paisDbHelper.deleteDataBase();
                                tramoDbHelper.deleteDataBase();
                                subtramoDbHelper.deleteDataBase();
                                seccionDbHelper.deleteDataBase();
                                areaDbHelper.deleteDataBase();
                                monitoreoDbHelper.deleteDataBase();

                                paisDbHelper.close();
                                tramoDbHelper.close();
                                subtramoDbHelper.close();
                                seccionDbHelper.close();
                                areaDbHelper.close();
                                monitoreoDbHelper.close();

                                deleteCache(SettingsAppActivity.this);

                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                                builder.create().dismiss();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.info_dialog_option_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        });

        return builder.create();
    }

    //metodo encargado de crear los dialogos informativos
    public AlertDialog createSimpleDialogBorrar(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton(getResources().getString(R.string.settings_app_dialog_option_confirm),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Borramos el paquete de datos descargado
                                monitoreoDbHelper.deleteDataBase();
                                monitoreoDbHelper.close();
                                builder.create().dismiss();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.settings_app_dialog_option_cancel),
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
    public AlertDialog createSimpleDialog(String mensaje, String titulo, final String tipo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_monitoring_info, null);
        builder.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title_dialog);
        TextView tvDescription = (TextView) v.findViewById(R.id.tv_description_dialog);

        tvTitle.setText(titulo);
        tvDescription.setText(mensaje);

        if( tipo.equals("OFFLINE")) {
            loading = new ProgressDialog(SettingsAppActivity.this);
            loading.setMessage(getResources().getString(R.string.list_monitoring_process_message));
            loading.setTitle(getResources().getString(R.string.list_monitoring_process_message_search));
            loading.setProgress(0);
            loading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            loading.setMax(100);
            loading.setCancelable(false);
        }

        builder.setPositiveButton(getResources().getString(R.string.settings_app_dialog_option_download),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(tipo.equals("OFFLINE")) loading.show();
                        Call<ReporteResponse> call = AylluApiAdapter.getApiService("REPORTE").getReporte(op[0], op[1], op[2], op[3]);
                        call.enqueue(new Callback<ReporteResponse>() {
                            @Override
                            public void onResponse(Call<ReporteResponse> call, Response<ReporteResponse> response) {
                                if (response.isSuccessful()) {
                                    builder.create().dismiss();

                                    reportes = response.body().getReportes();

                                    if (reportes.size() > 0) {
                                        if (tipo.equals("OFFLINE")) {
                                            //Borrar todos los datos
                                            monitoreoDbHelper.deleteDataBase();
                                            monitoreoDbHelper.close();
                                            monitoreoDbHelper = new MonitoreoDbHelper(SettingsAppActivity.this);

                                            //Ingresar los datos en la tabla Monitoreos
                                            for (int i = 0; i < reportes.size(); i++)
                                                monitoreoDbHelper.saveMonitoreos(reportes.get(i));
                                            monitoreoDbHelper.close();

                                            //Descargar Imagenes del Servidor
                                            downloadImages();
                                        } else checkPermission();
                                    }
                                    if (reportes.size() == 0) {
                                        Toast.makeText(SettingsAppActivity.this, getResources().getString(R.string.settings_app_process_message_search_negative), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ReporteResponse> call, Throwable t) {
                                Toast.makeText(
                                        SettingsAppActivity.this,
                                        getResources().getString(R.string.general_statistical_graph_process_message_server),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }
                });

        return builder.create();
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

    @SuppressLint("StaticFieldLeak")
    public int downloadZipFile(final String img) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(ApiConstants.URL_IMG);
        Retrofit retrofit = builder.client(httpClient.build()).build();
        AylluApiService downloadService = retrofit.create(AylluApiService.class);
        Call<ResponseBody> call = downloadService.downloadImageByUrl(img);
        final ResponseBody res;

        try {
            res = call.execute().body();
            new AsyncTask<Void, Long, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    saveToDisk(res, img);
                    return null;
                }
            }.execute();

            return portj;
        } catch (IOException e) {
            e.printStackTrace();
            return portj;
        }

    }

    /**
     * =============================================================================================
     * METODO: Registrar Monitoreos en estado Offline
     */
    private void downloadImages (){
        //------------------------------------------------------------------------------------------
        //DESCARGA IMAGENES DEL PAQUETE DE DATOS
        //reset progress bar status
        progressBarStatus = 0;
        sizeImg = reportes.size();

        portj = 100/sizeImg;
        dif = 100 - (portj * sizeImg);

        new Thread(new Runnable() {
            public void run() {
                //----------------------------------------------------------------------------------
                //DESCARGAMOS LAS IMAGENES
                for (int i = 0; i < reportes.size(); i++){
                    progressBarStatus += downloadZipFile(reportes.get(i).getPrueba1());
                    if (i == reportes.size()-1) progressBarStatus += dif;
                    //Actualizar el progressBar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            loading.setProgress(progressBarStatus);
                        }
                    });
                }
                //----------------------------------------------------------------------------------
                // Terminamos el dialogo del progresbar
                if (progressBarStatus >= 100) {
                    //Dormir por dos segundos para mirar el 100%
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //Cerrar el progresBar
                    loading.dismiss();
                    //Toast.makeText(SettingsAppActivity.this, getResources().getString(R.string.settings_app_process_message_search_positive), Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    /**
     * =============================================================================================
     * METODO:
     **/

    public void saveToDisk(ResponseBody body, String name) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory(), "/Ayllu/Offline");
            folder.mkdirs();
            File destinationFile = new File(folder.getPath(), name);

            InputStream is = null;
            OutputStream os = null;

            try {
                is = body.byteStream();
                os = new FileOutputStream(destinationFile);

                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                while ((count = is.read(data)) != -1) {
                    os.write(data, 0, count);
                    progress += count;
                }
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) is.close();
                if (os != null) os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //==============================================================================================
    public void generateReport(ArrayList<Reporte> rp) {
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String name = "Reporte-" + s.format(new Date()) + ".xls";

        int dif = 4-list_zon.size();
        if (dif > 0) {
            for (int i=0; i < dif; i++) list_zon.add("");
        }

        s = new SimpleDateFormat("dd-MM-yyyy");
        ArrayList<String> data_doc = new ArrayList<>();

        data_doc.add(nombre_pais);
        for (int i = 0; i < list_zon.size(); i++) data_doc.add(list_zon.get(i));
        data_doc.add(nombre_mon);
        data_doc.add(s.format(new Date()));

        //------------------------------------------------------------------------------------------
        //Escribiendo en el archivo Excel
        try {
            InputStream editor = getResources().openRawResource(R.raw.plantilla);
            File folder = new File(Environment.getExternalStorageDirectory(), "/Ayllu/Reportes");
            folder.mkdirs();
            File imagesFolder = new File(folder.getPath(), name);

            FileOutputStream result = new FileOutputStream(imagesFolder);

            //Crear el objeto que tendra el libro de Excel
            HSSFWorkbook workbook = new HSSFWorkbook(editor);

            //1. Obtenemos la primera hoja del Excel
            //2. Llenamos la primera hoja del Excel
            HSSFSheet sheet = workbook.getSheetAt(0);
            editExcel(2, sheet, data_doc);
            escribirExcel(1, 12, sheet, rp);

            //1. Obtenemos la segunda hoja del Excel
            //2. Llenamos la segunda hoja del Excel
            sheet = workbook.getSheetAt(1);
            escribirExcel(2, 6, sheet, rp);

            //1. Obtenemos la tercera hoja del Excel
            //2. Llenamos la tercera hoja del Excel
            sheet = workbook.getSheetAt(2);
            escribirExcel(3, 6, sheet, rp);

            workbook.write(result);
            result.close();
            workbook.close();

            Toast.makeText(
                    SettingsAppActivity.this,
                    getResources().getString(R.string.settings_app_process_message_report_positive),
                    Toast.LENGTH_SHORT)
                    .show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //==============================================================================================
    //EDITAR EL ARCHIVO EXCELL
    private void editExcel(int pi, HSSFSheet sheet, ArrayList<String> dt_doc){
        for (int i = 0; i < dt_doc.size(); i++) {
            HSSFRow fila = sheet.getRow(pi);
            HSSFCell celda = fila.getCell(1);

            celda.setCellValue(dt_doc.get(i));
            pi++;
        }

    }

    //==============================================================================================
    private void escribirExcel(int cod_plan, int pf, HSSFSheet sheet, ArrayList<Reporte> rp) {
        //Estilo de celda basico
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.DASHED);
        style.setBorderTop(BorderStyle.DASHED);
        style.setBorderRight(BorderStyle.DASHED);
        style.setBorderLeft(BorderStyle.DASHED);

        //Estilo de celda para las Repercusiones
        CellStyle style2 = sheet.getWorkbook().createCellStyle();
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style2.setBorderBottom(BorderStyle.DASHED);
        style2.setBorderTop(BorderStyle.DASHED);
        style2.setBorderRight(BorderStyle.DASHED);
        style2.setBorderLeft(BorderStyle.DASHED);

        //Estilo de celda para el Origen
        CellStyle style3 = sheet.getWorkbook().createCellStyle();
        style3.setAlignment(HorizontalAlignment.CENTER);
        style3.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
        style3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style3.setBorderBottom(BorderStyle.DASHED);
        style3.setBorderTop(BorderStyle.DASHED);
        style3.setBorderRight(BorderStyle.DASHED);
        style3.setBorderLeft(BorderStyle.DASHED);

        //Variable para el punto de escritura
        int punto = pf;

        if (cod_plan == 3) rp = cleanNulls(rp);

        //------------------------------------------------------------------------------------------
        for (int i = 0; i < rp.size(); i++) {
            HSSFRow fila = sheet.createRow(punto);
            HSSFCell celda;

            String[] list_p = new String[]{};
            String[] list_f = new String[]{};

            if (cod_plan == 2) {
                list_p = getResources().getStringArray(R.array.listPorcentaje);
                list_f = getResources().getStringArray(R.array.listFrecuencia);
            } else if (cod_plan == 3) {
                list_p = getResources().getStringArray(R.array.list_general_evaluation);
                list_f = getResources().getStringArray(R.array.list_specific_evaluation);
            }

            ArrayList<String> info = rp.get(i).generarInfoPlantilla(cod_plan, list_p, list_f);
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

    private ArrayList<Reporte> cleanNulls(ArrayList<Reporte> rpts) {
        ArrayList<Reporte> list_new = new ArrayList<>();
        for (int i = 0; i < rpts.size(); i++) {
            if (!rpts.get(i).getFecha_res().equals("1111-11-11")) list_new.add(rpts.get(i));
        }

        return list_new;
    }

    /**
     * =============================================================================================
     * METODO: CHEQUEA LOS PERMISOS DEL DISPOSITIVO
     **/
    private void checkPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) generateReport(reportes);
        else {
            int hasWriteContactsPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_WRITE_EXTERNAL_STORAGE);
            } else generateReport(reportes);
        }
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (MY_WRITE_EXTERNAL_STORAGE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) generateReport(reportes);
            else {
                Toast.makeText(this, getResources().getString(R.string.registration_message_permissions) + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * =============================================================================================
     * METODO: ELIMINA LOS DATOS ALMACENADOS EN CACHE
     **/

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception ignored) {
        }
    }

    /**
     * =============================================================================================
     * METODO: ELIMINA EL DIRECTORIO DE CACHE
     **/
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }
}
