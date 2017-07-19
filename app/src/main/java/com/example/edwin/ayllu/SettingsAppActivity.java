package com.example.edwin.ayllu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.edwin.ayllu.domain.AreaContract;
import com.example.edwin.ayllu.domain.AreaDbHelper;
import com.example.edwin.ayllu.domain.MonitoreoDbHelper;
import com.example.edwin.ayllu.domain.PaisDbHelper;
import com.example.edwin.ayllu.domain.Reporte;
import com.example.edwin.ayllu.domain.SeccionContract;
import com.example.edwin.ayllu.domain.SeccionDbHelper;
import com.example.edwin.ayllu.domain.SubtramoContract;
import com.example.edwin.ayllu.domain.SubtramoDbHelper;
import com.example.edwin.ayllu.domain.TramoContract;
import com.example.edwin.ayllu.domain.TramoDbHelper;
import com.example.edwin.ayllu.io.ApiConstants;
import com.example.edwin.ayllu.io.AylluApiAdapter;
import com.example.edwin.ayllu.io.AylluApiService;
import com.example.edwin.ayllu.io.model.ReporteResponse;
import com.example.edwin.ayllu.ui.AdministratorActivity;

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

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingsAppActivity extends AppCompatActivity implements View.OnClickListener{

    // Códigos de petición
    private static final int MY_WRITE_EXTERNAL_STORAGE = 123;

    LinearLayout opHabilitar, opElegir, opSalir, opAcerca, opAdministrar, opReporte;

    //VARIABLES DATOS TEMPORALES
    ArrayList<Reporte> reportes = new ArrayList<>();
    CharSequence[] items_tramos, items_subtramos, items_secciones, items_areas, items_tipos;;
    int[] op = {0, 0, 0, 0};
    String opciones = "";

    String item = "";
    String monitor = "", pais = "", tipo = "";
    int i = 0;

    //VARIABLES CONTROL DE DATOS FIJOS
    PaisDbHelper paisDbHelper;
    TramoDbHelper tramoDbHelper;
    SubtramoDbHelper subtramoDbHelper;
    SeccionDbHelper seccionDbHelper;
    AreaDbHelper areaDbHelper;
    MonitoreoDbHelper monitoreoDbHelper;
    Cursor cursor;

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

        int i = 0;
        //------------------------------------------------------------------------------------------
        //Obtenemos el codigo del monitor y el pais del usuario en sesión
        AdminSQLite admin = new AdminSQLite(getApplicationContext(), "login", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        //Prepara la sentencia SQL para la consulta en la Tabla de usuarios
        Cursor cursor = bd.rawQuery("SELECT codigo, tipo, pais FROM login LIMIT 1", null);
        cursor.moveToFirst();
        monitor = cursor.getString(0);
        tipo = cursor.getString(1);
        pais = cursor.getString(2);
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
        switch (v.getId()){
            case R.id.opcion_borrar:
                createSimpleDialogBorrar(getResources().getString(R.string.borrarDatos),getResources().getString(R.string.title_warning)).show();
                break;
            case R.id.opcion_elegir:
                createRadioListDialog(items_tramos, getResources().getString(R.string.descriptionTramo), 1, "OFFLINE").show();
                break;
            case R.id.opcion_salir:
                createSimpleDialogSalir(getResources().getString(R.string.cerrarSesion),getResources().getString(R.string.title_warning)).show();
                break;
            case R.id.opcion_acerca:
                Intent in = new Intent(getApplicationContext(), AboutAppActivity.class);
                startActivity(in);
                finish();
                break;
            case R.id.opcion_administrar:
                if(tipo.equals("A")){
                    Intent i = new Intent(getApplicationContext(), AdministratorActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
                else{
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

                                item = items_tramos[which].toString();
                                cursor = tramoDbHelper.generateConditionalQuery(new String[]{item}, TramoContract.TramoEntry.DESCRIPCION);
                                cursor.moveToFirst();
                                op[0] = cursor.getInt(1);
                                opciones = "";
                                opciones += getResources().getString(R.string.info_critical_point_item_tramo)+"\n(" + item + ")";

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
                                opciones += "\n\n" + getResources().getString(R.string.info_critical_point_item_subtramo) + "\n(" + item + ")";
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
                                opciones += " \n\n"+ getResources().getString(R.string.info_critical_point_item_seccion)+"\n(" + item + ")";

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
                                opciones += "\n\n" + getResources().getString(R.string.info_critical_point_item_propiedad) + "\n("+ items_tipos[which].toString() + ")";

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

                                switch (zn){
                                    case 1:
                                        if(op[0] != 0) createRadioListDialog(items_subtramos, getResources().getString(R.string.descriptionSubtramo), 2, type).show();
                                        else for (i = 0; i < 4; i++) op[i] = 0;
                                        break;
                                    case 2:
                                        if(op[1] != 0) createRadioListDialog(items_secciones, getResources().getString(R.string.descriptionSeccion), 3, type).show();
                                        else for (i = 0; i < 4; i++) op[i] = 0;
                                        break;
                                    case 3:
                                        if(op[2] != 0) createRadioListDialog(items_tipos, getResources().getString(R.string.descriptionPropiedad), 4, type).show();
                                        else for (i = 0; i < 4; i++) op[i] = 0;
                                        break;
                                    case 4:
                                        if(op[3] != 0) createSimpleDialog(opciones, getResources().getString(R.string.settings_app_dialog_title), type).show();
                                        else for (i = 0; i < 4; i++) op[i] = 0;
                                        break;
                                }
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.settings_app_dialog_option_confirm), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

                                AdminSQLite admin = new AdminSQLite(getApplicationContext(), "login", null, 1);
                                SQLiteDatabase bd = admin.getWritableDatabase();
                                bd.delete(admin.TABLENAME, null, null);
                                bd.close();

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

        builder.setPositiveButton(getResources().getString(R.string.settings_app_dialog_option_download),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog loading = new ProgressDialog(SettingsAppActivity.this);
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
                                    loading.dismiss();
                                    builder.create().dismiss();

                                    reportes = response.body().getReportes();

                                    if (reportes.size() > 0){
                                        if (tipo.equals("OFFLINE")){
                                            //Borrar todos los datos
                                            monitoreoDbHelper.deleteDataBase();
                                            monitoreoDbHelper.close();
                                            monitoreoDbHelper = new MonitoreoDbHelper(SettingsAppActivity.this);

                                            //Ingresar los datos en la tabla Monitoreos
                                            for (int i = 0; i<reportes.size(); i++) monitoreoDbHelper.saveMonitoreos(reportes.get(i));
                                            monitoreoDbHelper.close();

                                            //Descargar Imagenes del Servidor
                                            for (int i = 0; i<reportes.size(); i++) downloadZipFile(reportes.get(i).getPrueba1());
                                            Toast.makeText(SettingsAppActivity.this,getResources().getString(R.string.settings_app_process_message_search_positive), Toast.LENGTH_SHORT).show();
                                        }
                                        else checkPermission();
                                    }
                                    if (reportes.size() == 0) {
                                        Toast.makeText(SettingsAppActivity.this,getResources().getString(R.string.settings_app_process_message_search_negative), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ReporteResponse> call, Throwable t) {
                                loading.dismiss();
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

    public void downloadZipFile(final String img) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(ApiConstants.URL_IMG);
        Retrofit retrofit = builder.client(httpClient.build()).build();
        AylluApiService downloadService = retrofit.create(AylluApiService.class);
        Call<ResponseBody> call = downloadService.downloadImageByUrl(img);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    new AsyncTask<Void, Long, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            saveToDisk(response.body(), img);
                            return null;
                        }
                    }.execute();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    /**
     * =============================================================================================
     * METODO:
     **/

    public void saveToDisk(ResponseBody body, String name) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory(),"/Ayllu/Offline");
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
                    progress +=count;
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
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        String name = "Reporte-"+s.format(new Date());
        //s = new SimpleDateFormat("HH:mm:ss");
        //name += "-"+s.format(new Date())+".xls";
        name = "reporte.xls";
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
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style2.setBorderBottom(BorderStyle.DASHED);
        style2.setBorderTop(BorderStyle.DASHED);
        style2.setBorderRight(BorderStyle.DASHED);
        style2.setBorderLeft(BorderStyle.DASHED);

        //Estilo de celda para Repercusiones y Origenes
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

    /**
     * =============================================================================================
     * METODO: CHEQUEA LOS PERMISOS DEL DISPOSITIVO
     **/
    private void checkPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) generateReport(reportes);
        else {
            int hasWriteContactsPermission = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_WRITE_EXTERNAL_STORAGE);
            }
            else generateReport(reportes);
        }
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(MY_WRITE_EXTERNAL_STORAGE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) generateReport(reportes);
            else {
                Toast.makeText(this, getResources().getString(R.string.registration_message_permissions) + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
