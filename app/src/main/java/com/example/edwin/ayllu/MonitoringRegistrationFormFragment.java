package com.example.edwin.ayllu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.FactorContract;
import com.example.edwin.ayllu.domain.FactorDbHelper;
import com.example.edwin.ayllu.domain.VariableContract;
import com.example.edwin.ayllu.domain.VariableDbHelper;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class MonitoringRegistrationFormFragment extends Fragment implements
        View.OnClickListener, FloatingActionsMenu.OnFloatingActionsMenuUpdateListener{

    // Códigos de petición
    public static final int REQUEST_LOCATION = 1;

    //VARIABLES: Componetes del Formulario de Registro
    Button btn_presencia, btn_frecuencia;
    RadioGroup rg_reper1, rg_reper2, rg_origen;
    FloatingActionButton fab_regMon, fab_point, fab_camera;
    FloatingActionButton fab_factor, fab_variable;
    FloatingActionsMenu fab_menu, fab_menu2;
    private ImageView foto1, foto2, foto3;

    MonitoringSummaryFragment fragment;

    //VARIABLES: Control y Procesamiento de datos del formulario
    String punto_afectacion = "", area = "", monitor = "", op_reg, pais = "";
    String origen = "10";
    String fecha = "";
    String longitud = "0", latitud = "0";
    int[] repercusiones = {1, 0, 0, 1};

    //VARIABLES: Control de la selección de opciones
    CharSequence[] items_factores, items_variables;
    private String[] opciones = {"0", "0"};
    private int[] pos = {-1, -1};
    String item = "";
    int i = 0;

    //VARIABLES: Control y Procesamiento de datos de la camara
    private String foto;
    private File file = null;
    private ArrayList<String> fotos = new ArrayList<>();
    private ArrayList<File> files = new ArrayList<>();
    private boolean f1 = false;
    private boolean f2 = false;
    private boolean f3 = false;
    //VARIABLES: Animaciones de los botones
    Interpolator interpolador;

    //VARIABLES: Control de datos fijos
    private FactorDbHelper factorDbHelper;
    private VariableDbHelper variableDbHelper;
    Cursor cursor;

    //VARIABLES: Control de datos para el (PORCENTAJE DE APARICIÓN) y (FRECUENCIA DE APARICIÓN)
    private String[] items_porcentaje, items_frecuencia;
    private int[] seleccion_items = {1, 1};
    private int[] pos_seleccion = {0, 0};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        items_porcentaje = getResources().getStringArray(R.array.listPorcentaje);
        items_frecuencia = getResources().getStringArray(R.array.listFrecuencia);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_monitoring_registration_form, container, false);

        //------------------------------------------------------------------------------------------
        //Vincula los elementos de la interfaz del formulario con las variables
        btn_presencia = (Button) view.findViewById(R.id.btn_presencia);
        btn_frecuencia = (Button) view.findViewById(R.id.btn_frecuencia);

        rg_reper1 = (RadioGroup) view.findViewById(R.id.rg_repercusiones1);
        rg_reper2 = (RadioGroup) view.findViewById(R.id.rg_repercusiones2);
        rg_origen = (RadioGroup) view.findViewById(R.id.rg_origen);

        fab_regMon = (FloatingActionButton) view.findViewById(R.id.fab_regMon);
        fab_point = (FloatingActionButton) view.findViewById(R.id.fab_point);
        fab_camera = (FloatingActionButton) view.findViewById(R.id.fab_camera);
        fab_factor = (FloatingActionButton) view.findViewById(R.id.fab_factor);
        fab_variable = (FloatingActionButton) view.findViewById(R.id.fab_variable);
        fab_menu = (FloatingActionsMenu) view.findViewById(R.id.fab_menu);
        fab_menu2 = (FloatingActionsMenu) view.findViewById(R.id.fab_menu2);

        foto1 = (ImageView) view.findViewById(R.id.foto1);
        foto2 = (ImageView) view.findViewById(R.id.foto2);
        foto3 = (ImageView) view.findViewById(R.id.foto3);

        foto1.setOnClickListener(this);
        foto2.setOnClickListener(this);
        foto3.setOnClickListener(this);

        //------------------------------------------------------------------------------------------
        //Minimizamos los botones del menu
        fab_regMon.setScaleX(0);
        fab_regMon.setScaleY(0);
        fab_point.setScaleX(0);
        fab_point.setScaleY(0);
        fab_camera.setScaleX(0);
        fab_camera.setScaleY(0);

        fab_regMon.setEnabled(false);
        fab_point.setEnabled(false);
        fab_camera.setEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            interpolador = AnimationUtils.loadInterpolator(getActivity().getBaseContext(),
                    android.R.interpolator.fast_out_slow_in);
        }

        //------------------------------------------------------------------------------------------
        //Añadimos el escucha del evento OnClick a cada uno de los botones del formulario
        btn_presencia.setOnClickListener(this);
        btn_frecuencia.setOnClickListener(this);

        fab_regMon.setOnClickListener(this);
        fab_point.setOnClickListener(this);
        fab_camera.setOnClickListener(this);
        fab_factor.setOnClickListener(this);
        fab_variable.setOnClickListener(this);
        fab_menu.setOnFloatingActionsMenuUpdateListener(this);
        fab_menu2.setOnFloatingActionsMenuUpdateListener(this);

        fab_variable.setEnabled(false);

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        fecha = s.format(new Date());

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
        //Se obtiene los parametros enviados por el Motor de busqueda basico

        op_reg = getArguments().getString("OPCION");

        assert op_reg != null;
        if (op_reg.equals("M")) {
            punto_afectacion = getArguments().getString("PUNTO");

            fab_factor.setScaleX(0);
            fab_factor.setScaleY(0);
            fab_variable.setScaleX(0);
            fab_variable.setScaleY(0);
            fab_menu.setScaleX(0);
            fab_menu.setScaleY(0);

            fab_menu.setEnabled(false);
            fab_factor.setEnabled(false);
            fab_variable.setEnabled(false);
        } else {
            fab_menu2.setScaleX(0);
            fab_menu2.setScaleY(0);

            fab_menu2.setVisibility(View.GONE);

            area = getArguments().getString("AREA");
        }

        //------------------------------------------------------------------------------------------
        //Se obtiene todos los factores
        factorDbHelper = new FactorDbHelper(getActivity());
        variableDbHelper = new VariableDbHelper(getActivity());
        i = 0;

        cursor = factorDbHelper.generateQuery("SELECT * FROM ");
        if (cursor.moveToFirst()) {
            items_factores = new CharSequence[cursor.getCount()];
            do {
                items_factores[i] = cursor.getString(2);
                i++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        fab_menu.expand();

        if (files.size() >= 1)
            Picasso.with(getActivity()).load(files.get(0)).fit().centerCrop().into(foto1);
        if (files.size() >= 2)
            Picasso.with(getActivity()).load(files.get(1)).fit().centerCrop().into(foto2);
        if (files.size() == 3)
            Picasso.with(getActivity()).load(files.get(2)).fit().centerCrop().into(foto3);

    }

    /**
     * =============================================================================================
     * METODO: Recolecta la prueba (Fotografia) del monitoreo y la almacena en un archivo
     **/
    public void getCamara1() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Ayllu");

        imagesFolder.mkdirs();
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());
        foto = format + ".jpg";
        file = new File(imagesFolder, foto);

        Uri uriSavedImage = Uri.fromFile(file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(cameraIntent, 1);
    }

    /**
     * =============================================================================================
     * METODO: Muestra la Imagen en pantalla
     **/
    protected void bitMapImg(File file, ImageView img) {
        Bitmap bMap;
        bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/Ayllu/" + file.getName());
        img.setImageBitmap(bMap);
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Comprovamos que la foto se a realizado
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (f1) {
                fotos.set(0, foto);
                files.set(0, file);
                f1 = false;
                Picasso.with(getActivity()).load(files.get(0)).fit().centerCrop().into(foto1);
            } else if (f2) {
                fotos.set(1, foto);
                files.set(1, file);
                f2 = false;
                Picasso.with(getActivity()).load(files.get(0)).fit().centerCrop().into(foto2);
            } else if (f3) {
                fotos.set(2, foto);
                files.set(2, file);
                f3 = false;
                Picasso.with(getActivity()).load(files.get(0)).fit().centerCrop().into(foto3);
            } else {
                fotos.add(foto);
                files.add(file);
            }

            if (files.size() == 1)
                Picasso.with(getActivity()).load(files.get(0)).fit().centerCrop().into(foto1);
            else if (files.size() == 2)
                Picasso.with(getActivity()).load(files.get(1)).fit().centerCrop().into(foto2);
            else if (files.size() == 3)
                Picasso.with(getActivity()).load(files.get(2)).fit().centerCrop().into(foto3);
        }
    }

    /**
     * =============================================================================================
     * METODO: Valida la selección del Checbox Repercusiones (Positivas/Negativas)
     **/
    public void comprobarRepercusiones1() {
        switch (rg_reper1.getCheckedRadioButtonId()) {
            case R.id.rb_positive:
                repercusiones[0] = 1;
                repercusiones[1] = 0;
                break;
            case R.id.rb_negative:
                repercusiones[0] = 0;
                repercusiones[1] = 1;
                break;
        }
    }

    /**
     * =============================================================================================
     * METODO: Valida la selección del Checbox Repercusiones (Actuales/Potenciales)
     **/
    public void comprobarRepercusiones2() {
        switch (rg_reper2.getCheckedRadioButtonId()) {
            case R.id.rb_current:
                repercusiones[2] = 1;
                repercusiones[3] = 0;
                break;
            case R.id.rb_potencial:
                repercusiones[2] = 0;
                repercusiones[3] = 1;
                break;
        }
    }

    /**
     * =============================================================================================
     * METODO: Valida la selección del Checbox Origen
     **/
    public void comprobarOrigen() {
        switch (rg_origen.getCheckedRadioButtonId()) {
            case R.id.rb_interno:
                origen = "10";
                break;
            case R.id.rb_externo:
                origen = "01";
                break;
        }
    }

    /**
     * =============================================================================================
     * METODO: Administra los eventos de tipo onClick
     **/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //--------------------------------------------------------------------------------------
            //EVENTO: Registrar un Monitoreo
            case R.id.fab_regMon:
                //Comprobar la selección de los Checbox
                comprobarRepercusiones1();
                comprobarRepercusiones2();
                comprobarOrigen();

                //Comprobamos que se creo la imagen
                if (files.size() > 0) {
                    fragment = new MonitoringSummaryFragment();
                    Bundle params = new Bundle();

                    String rep = "";
                    for (int i = 0; i < 4; i++) rep += "" + repercusiones[i];

                    params.putString("MONITOR", monitor);
                    params.putString("FECHA", fecha);
                    params.putString("REPERCUSIONES", rep);
                    params.putString("ORIGEN", origen);
                    params.putString("POR_NAME", items_porcentaje[pos_seleccion[0]].toString());
                    params.putString("POR_NUMBER", seleccion_items[0] + "");
                    params.putString("FRE_NAME", items_frecuencia[pos_seleccion[1]].toString());
                    params.putString("FRE_NUMBER", seleccion_items[1] + "");
                    params.putString("FILES_NUMBER", files.size() + "");

                    if (files.size() >= 1) params.putString("PRUEBA1", files.get(0).getName());
                    if (files.size() >= 2) params.putString("PRUEBA2", files.get(1).getName());
                    if (files.size() == 3) params.putString("PRUEBA3", files.get(2).getName());

                    if (op_reg.equals("N")) {
                        if (!opciones[1].equals("0")) {
                            params.putString("VAR_COD", opciones[1]);
                            params.putString("VAR_NAME", items_variables[pos[1]].toString());
                            params.putString("AREA", area);
                            params.putString("LATITUD", latitud + "");
                            params.putString("LONGITUD", longitud + "");
                            params.putString("TYPE_UPLOAD", "NEW");

                            fragment.setArguments(params);

                            //Inflamos el layout para el Fragmento MonitoringListFragment
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .addToBackStack(null)
                                    .replace(R.id.monitoring_principal_context, fragment)
                                    .commit();
                        } else
                            createSimpleDialog(
                                    getResources().getString(R.string.descriptionFormRegistrationMonitoringVariableDialog),
                                    getResources().getString(R.string.titleFormRegistrationMonitoringDialog)).show();
                    } else {
                        params.putString("PUNTO_AFECTACION", punto_afectacion);
                        params.putString("TYPE_UPLOAD", "MONITORING");

                        fragment.setArguments(params);

                        //Inflamos el layout para el Fragmento MonitoringListFragment
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.monitoring_principal_context, fragment)
                                .commit();
                    }
                } else
                    createSimpleDialog(
                            getResources().getString(R.string.descriptionFormRegistrationMonitoringCameraDialog),
                            getResources().getString(R.string.titleFormRegistrationMonitoringDialog)).show();

                break;
            //--------------------------------------------------------------------------------------
            //EVENTO: Tomar fotografia
            case R.id.fab_camera:
                if (files.size() < 3) getCamara1();
                else {
                    Toast.makeText(getActivity(),
                            "No se puede tomar mas fotos",
                            Toast.LENGTH_LONG).show();
                }
                break;
            //--------------------------------------------------------------------------------------
            //EVENTO: Establecer las coordenadas geograficas
            case R.id.fab_point:
                createPointDialog(latitud, longitud).show();
                break;
            //--------------------------------------------------------------------------------------
            //EVENTO: Seleccionar un Factor
            case R.id.fab_factor:
                createRadioListDialog(items_factores, "FACTORES", 1).show();
                break;
            //--------------------------------------------------------------------------------------
            //EVENTO: Seleccionar una Variable
            case R.id.fab_variable:
                createRadioListDialog(items_variables, "VARIABLES", 2).show();
                break;
            //--------------------------------------------------------------------------------------
            //EVENTO: Seleccionar la Presencia de Aparición
            case R.id.btn_presencia:
                createSelectorDialog(items_porcentaje, "PRESENCIA DE APARICIÓN", 1).show();
                break;
            //--------------------------------------------------------------------------------------
            //EVENTO: Seleccionar la Frecuencia de Aparición
            case R.id.btn_frecuencia:
                createSelectorDialog(items_frecuencia, "FRECUENCIA DE APARICIÓN", 2).show();
                break;
            //--------------------------------------------------------------------------------------
            //EVENTO: Seleccionar la primera foto
            case R.id.foto1:
                Log.e("numero", "" + files.size());
                if (files.size() > 0) createDialogImage(files.get(0), 1).show();

                break;
            case R.id.foto2:
                Log.e("numero", "" + files.size());
                if (files.size() > 1) createDialogImage(files.get(1), 2).show();
                break;
            case R.id.foto3:
                Log.e("numero", "" + files.size());
                if (files.size() > 2) createDialogImage(files.get(2), 3).show();
                break;
            default:
                break;
        }
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

    /**
     * =============================================================================================
     * METODO: Genera un Dialogo con las opciones de las categorias (FACTOR/VARIABLE)
     **/
    public AlertDialog createRadioListDialog(final CharSequence[] items, String title, final int zn) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title);
        builder.setSingleChoiceItems(items, pos[zn - 1], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (zn) {
                    //------------------------------------------------------------------------------
                    //DIALOGO: Se crea una lista de RadioButtons de los Factores
                    case 1:
                        item = items_factores[which].toString();
                        cursor = factorDbHelper.generateConditionalQuery(new String[]{item}, FactorContract.FactorEntry.NOMBRE);
                        cursor.moveToFirst();

                        opciones[0] = cursor.getString(1);
                        pos[0] = which;
                        opciones[1] = "0";
                        pos[1] = -1;

                        items_variables = null;
                        fab_variable.setEnabled(true);

                        i = 0;
                        cursor = variableDbHelper.generateConditionalQuery(new String[]{opciones[0]}, VariableContract.VariableEntry.FACTOR);
                        if (cursor.moveToFirst()) {
                            items_variables = new CharSequence[cursor.getCount()];
                            do {
                                items_variables[i] = cursor.getString(2);
                                i++;
                            } while (cursor.moveToNext());
                        }
                        break;
                    //------------------------------------------------------------------------------
                    //DIALOGO: Se crea una lista de RadioButtons de las Variables
                    case 2:
                        item = items_variables[which].toString();
                        cursor = variableDbHelper.generateConditionalQuery(new String[]{item}, VariableContract.VariableEntry.NOMBRE);
                        cursor.moveToFirst();

                        opciones[1] = cursor.getString(1);
                        pos[1] = which;
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

    /**
     * =============================================================================================
     * METODO: Genera un Dialogo con las opciones de la Presencia y Frecuencia de aparición
     **/
    public AlertDialog createSelectorDialog(final CharSequence[] items, String title, final int opn) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title);
        builder.setSingleChoiceItems(items, pos_seleccion[opn - 1], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (opn) {
                    //------------------------------------------------------------------------------
                    //DIALOGO: Se crea una lista de RadioButtons de la Presencia
                    case 1:
                        seleccion_items[0] = which + 1;
                        pos_seleccion[0] = which;
                        btn_presencia.setText(items_porcentaje[which]);
                        break;
                    //------------------------------------------------------------------------------
                    //DIALOGO: Se crea una lista de RadioButtons de la Frecuencia
                    case 2:
                        seleccion_items[1] = which + 1;
                        pos_seleccion[1] = which;
                        btn_frecuencia.setText(items_frecuencia[which]);
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

    /**
     * =============================================================================================
     * METODO: Genera un Dialogo personalizado para obtener las Coordenadas geograficas del punto
     **/
    public AlertDialog createPointDialog(String l1, String l2) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_point, null);

        builder.setView(v);

        FloatingActionButton fab_manual = (FloatingActionButton) v.findViewById(R.id.fab_manual);
        FloatingActionButton fab_fijar = (FloatingActionButton) v.findViewById(R.id.fab_fijar);

        final EditText et_latitud = (EditText) v.findViewById(R.id.et_latitud);
        final EditText et_longitud = (EditText) v.findViewById(R.id.et_longitud);

        et_latitud.setText(String.format(Locale.getDefault(), "%s", l1));
        et_longitud.setText(String.format(Locale.getDefault(), "%s", l2));

        et_latitud.setEnabled(false);
        et_longitud.setEnabled(false);

        fab_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_latitud.setEnabled(true);
                et_longitud.setEnabled(true);
            }
        });

        fab_fijar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        builder.setPositiveButton("HECHO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!et_latitud.getText().toString().equals("") && !et_longitud.getText().toString().equals("")) {
                            latitud = et_latitud.getText().toString();
                            longitud = et_longitud.getText().toString();
                            builder.create().dismiss();
                        }
                    }
                });

        return builder.create();
    }

    /**
     * =============================================================================================
     * METODO: Mostrar imagenes ampliadas
     **/
    public AlertDialog createDialogImage(File file, int pos) {
        final int aux = pos;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_image, null);

        builder.setView(v);

        ImageView img = (ImageView) v.findViewById(R.id.dialogImage);

        bitMapImg(file, img);

        builder.setPositiveButton("CAMBIAR",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (aux == 1) f1 = true;
                        else if (aux == 2) f2 = true;
                        else if (aux == 3) f3 = true;
                        getCamara1();
                        builder.create().dismiss();
                    }
                });

        return builder.create();
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onMenuExpanded() {
        animationButton(fab_camera, 1, 1);
        animationButton(fab_regMon, 1, 1);

        fab_regMon.setEnabled(true);
        fab_camera.setEnabled(true);

        if (op_reg.equals("N")) {
            animationButton(fab_point, 1, 1);
            fab_point.setEnabled(true);
        }
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onMenuCollapsed() {
        fab_regMon.setEnabled(false);
        fab_camera.setEnabled(false);

        animationButton(fab_camera, 0, 0);
        animationButton(fab_regMon, 0, 0);

        if (op_reg.equals("N")) {
            animationButton(fab_point, 0, 0);
            fab_point.setEnabled(false);
        }
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
}
