package com.qhapaq.nan.ayllu.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qhapaq.nan.ayllu.R;
import com.qhapaq.nan.ayllu.domain.factor.FactorContract;
import com.qhapaq.nan.ayllu.domain.factor.FactorDbHelper;
import com.qhapaq.nan.ayllu.domain.usuario.UsuarioDbHelper;
import com.qhapaq.nan.ayllu.domain.variable.VariableContract;
import com.qhapaq.nan.ayllu.domain.variable.VariableDbHelper;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.qhapaq.nan.ayllu.ui.utilities.DialogUtility;
import com.qhapaq.nan.ayllu.ui.utilities.ToolbarUtility;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

import static android.app.Activity.RESULT_OK;

public class MonitoringRegistrationFormFragment extends Fragment implements VerticalStepperForm {

    // Códigos de petición
    private static final int MY_WRITE_EXTERNAL_STORAGE = 123;

    //VARIABLES: Componetes del Formulario de Registro
    Button btnVar, btnFac, btnPor, btnFre;
    RadioGroup rgRep1, rgRep2, rgOrg;
    TextView tvVar, tvFac, tvPor, tvFre, tvLat, tvLong, tvImage1, tvImage2, tvImage3;
    TextInputLayout tilAltitude;
    EditText etAltitude;
    TextView tvTechnicalConcept;
    FloatingActionButton fabLat, fabLong, fabCamera1, fabCamera2, fabCamera3;

    MonitoringSummaryFragment fragment;

    //VARIABLES: Control y Procesamiento de datos del formulario
    String punto_afectacion = "", area = "", monitor = "", op_reg;
    String origen = "10";
    String fecha = "";
    String longitud = "", latitud = "", altitud = "0.0";
    int[] repercusiones = {1, 0, 0, 1};
    String factor = "", variable = "", lat = "", logt = "";
    String technicalConcept = "";

    //VARIABLES: Control de la selección de opciones
    CharSequence[] items_factores, items_variables;
    private String[] opciones = {"", ""};
    private int[] pos = {-1, -1};
    String item = "";
    int i = 0;

    //VARIABLES: Control y Procesamiento de datos de la camara
    String foto = "";
    File file = null;
    private ArrayList<String> fotos = new ArrayList<>();
    private ArrayList<File> files = new ArrayList<>();
    private boolean f1 = false;
    private boolean f2 = false;
    private boolean f3 = false;
    private boolean state = false;
    private int position = 0;
    private String name_change = "";

    //VARIABLES: Control de datos fijos
    private FactorDbHelper factorDbHelper;
    private VariableDbHelper variableDbHelper;
    private UsuarioDbHelper usuarioDbHelper;
    Cursor cursor;

    //VARIABLES: Control de datos para el (PORCENTAJE DE APARICIÓN) y (FRECUENCIA DE APARICIÓN)
    private String[] items_porcentaje, items_frecuencia;
    private int[] seleccion_items = {0, 0};
    private int[] pos_seleccion = {-1, -1};

    private VerticalStepperFormLayout verticalStepperForm;
    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        items_porcentaje = getResources().getStringArray(R.array.listPorcentaje);
        items_frecuencia = getResources().getStringArray(R.array.listFrecuencia);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_monitoring_registration_form, container, false);
        ToolbarUtility.showToolbar(activity,view,getResources().getString(R.string.titleFormRegistrationMonitoring),false);

        fecha = DateFormat.getDateInstance().format(new Date(System.currentTimeMillis()));

        //------------------------------------------------------------------------------------------
        //Obtenemos el codigo del monitor y el pais del usuario en sesión
        usuarioDbHelper = new UsuarioDbHelper(getActivity());
        Cursor cursor = usuarioDbHelper.generateQuery("SELECT * FROM ");
        if (cursor.moveToFirst()) monitor = cursor.getString(1);

        //------------------------------------------------------------------------------------------
        //Se obtiene los parametros enviados por el Motor de busqueda basico

        op_reg = getArguments() != null ? getArguments().getString("OPCION") : null;
        String[] mySteps;

        assert op_reg != null;
        if (op_reg.equals("M")) {
            factor = getArguments().getString("FACTOR_NAME");
            variable = getArguments().getString("VAR_NAME");
            lat = getArguments().getString("LATITUD");
            logt = getArguments().getString("LONGITUD");
            altitud = getArguments().getString("ALTITUD");

            ToolbarUtility.showToolbar(activity,view, getResources().getString(R.string.titleFormMonitoring),false);

            punto_afectacion = getArguments().getString("PUNTO");
            mySteps = getResources().getStringArray(R.array.registration_form_monitoring);
        } else {
            area = getArguments().getString("AREA");
            mySteps = getResources().getStringArray(R.array.registration_form_new);
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

        //------------------------------------------------------------------------------------------
        //Se establece el menu Vertical
        verticalStepperForm = view.findViewById(R.id.vertical_stepper_form);

        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, mySteps, this, getActivity())
                .primaryColor(getResources().getColor(R.color.colorPrimary))
                .primaryDarkColor(getResources().getColor(R.color.colorPrimaryDark))
                .init();

        return view;
    }

    /**
     * =============================================================================================
     * METODO: Restauramos los datos del formulario al regresar al fragmento
     **/
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        //------------------------------------------------------------------------------------------
        //Cargamos el Factor y la Variable
        if (op_reg.equals("N") && pos[0] != -1) {
            tvFac.setText(items_factores[pos[0]]);
            tvVar.setText(items_variables[pos[1]]);

            tvLat.setText(latitud);
            tvLong.setText(longitud);

            btnVar.setEnabled(true);
            btnVar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent));
        }

        if (files.size() == 1) changeStateImage(tvImage1);
        else if (files.size() == 2) {
            changeStateImage(tvImage1);
            changeStateImage(tvImage2);
        } else if (files.size() == 3) {
            changeStateImage(tvImage1);
            changeStateImage(tvImage2);
            changeStateImage(tvImage3);
        }

        //------------------------------------------------------------------------------------------
        //Cargamos el porcentaje y la frecuencia de aparición

        if (pos_seleccion[0] != -1 && pos_seleccion[1] != -1) {
            tvPor.setText(items_porcentaje[pos_seleccion[0]]);
            tvFre.setText(items_frecuencia[pos_seleccion[1]]);
        }

        //------------------------------------------------------------------------------------------
        //Cargamos las repercusiones

        if (repercusiones[0] == 1) rgRep1.check(R.id.rb_positive);
        else rgRep1.check(R.id.rb_negative);

        if (repercusiones[2] == 1) rgRep2.check(R.id.rb_current);
        else rgRep2.check(R.id.rb_potencial);

        //------------------------------------------------------------------------------------------
        //Cargamos el origen
        if (origen.charAt(0) == '1') rgOrg.check(R.id.rb_interno);
        else rgOrg.check(R.id.rb_externo);
    }


    /**
     * =============================================================================================
     * METODO: Recolecta la prueba (Fotografia) del monitoreo y la almacena en un archivo
     **/
    public void getCamara1() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Ayllu");

        imagesFolder.mkdirs();
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());
        foto = format + ".jpg";
        if (state) foto = name_change;
        file = new File(imagesFolder, foto);

        String filePath = Environment.getExternalStorageDirectory() + "Ayllu" + foto;
        Uri imageUri;

        //Validación de acuerdo al OS.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = Uri.parse(filePath);
        } else {
            imageUri = Uri.fromFile(file);
        }

        //Uri uriSavedImage = Uri.fromFile(file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, 1);
    }

    /**
     * =============================================================================================
     * METODO: Muestra la Imagen en pantalla
     **/
    protected void bitMapImg(File file, ImageView img) {
        int heightDp = getResources().getDisplayMetrics().heightPixels/2;
        int widthDp = getResources().getDisplayMetrics().widthPixels;

        Picasso.get()
                .load(file)
                .resize(widthDp,heightDp)
                .into(img);
    }

    public boolean resizeImage(File file, String name_file, TextView tvState) {
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 10;
        Bitmap mBitmap = BitmapFactory.decodeFile(file.getPath());

        if (mBitmap.getWidth() <= 1024) {
            if (state) {
                files.set(position, file);
                fotos.set(position, name_file);
                return false;
            } else {
                files.add(file);
                fotos.add(name_file);
                changeStateImage(tvState);
                return true;
            }
        }

        float newWidth = 1024;
        float newHeigth;
        //calculamos el alto ideal
        newHeigth = (newWidth * mBitmap.getHeight()) / mBitmap.getWidth();

        //Redimensionamos
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        //Obtenemos la escala
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeigth) / height;

        // creamos una matrix para la manipulación
        Matrix matrix = new Matrix();
        // establecemos la escala para la matrix
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
        File imageFile = new File(file.getPath());

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();

            if (state) {
                files.set(position, imageFile);
                fotos.set(position, name_file);
                return false;
            } else {
                files.add(imageFile);
                fotos.add(name_file);
                changeStateImage(tvState);
                return true;
            }

        } catch (Exception e) {
            return state;
        }
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
            if (!state) {
                if (!f1) {
                    f1 = resizeImage(file, foto, tvImage1);
                } else if (!f2) {
                    f2 = resizeImage(file, foto, tvImage2);
                } else {
                    f3 = resizeImage(file, foto, tvImage3);
                }
            } else {
                state = resizeImage(file, foto, tvImage1);
                if (!state) position = 0;
            }

            if (op_reg.equals("M")) onStepOpening(0);
            else onStepOpening(2);
        }
    }

    /**
     * =============================================================================================
     * METODO: Genera un Dialogo basico en pantalla
     **/
    public AlertDialog createSimpleDialog(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_monitoring_info, null);
        builder.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title_dialog);
        TextView tvDescription = (TextView) v.findViewById(R.id.tv_description_dialog);

        tvTitle.setText(titulo);
        tvDescription.setText(mensaje);

        builder.setPositiveButton(getResources().getString(R.string.registration_form_dialog_option_positive),
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
                        opciones[1] = "";
                        pos[1] = -1;

                        items_variables = null;

                        i = 0;
                        cursor = variableDbHelper.generateConditionalQuery(new String[]{opciones[0]}, VariableContract.VariableEntry.FACTOR);
                        if (cursor.moveToFirst()) {
                            items_variables = new CharSequence[cursor.getCount()];
                            do {
                                items_variables[i] = cursor.getString(2);
                                i++;
                            } while (cursor.moveToNext());
                        }

                        tvFac.setText(item);
                        tvVar.setText("");

                        btnVar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent));
                        btnVar.setEnabled(true);
                        break;
                    //------------------------------------------------------------------------------
                    //DIALOGO: Se crea una lista de RadioButtons de las Variables
                    case 2:
                        item = items_variables[which].toString();
                        cursor = variableDbHelper.generateConditionalQuery(new String[]{item}, VariableContract.VariableEntry.NOMBRE);
                        cursor.moveToFirst();

                        opciones[1] = cursor.getString(1);
                        pos[1] = which;

                        tvVar.setText(item);
                        break;
                    default:
                        break;
                }

                onStepOpening(0);
            }
        })
                .setNegativeButton(getResources().getString(R.string.registration_form_dialog_option_ok),
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
     * METODO: Genera un Dialogo con las opciones de la Porcentaje y Frecuencia de aparición
     **/
    public AlertDialog createSelectorDialog(final CharSequence[] items, String title, final int opn) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title);
        builder.setSingleChoiceItems(items, pos_seleccion[opn - 1], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (opn) {
                    //------------------------------------------------------------------------------
                    //DIALOGO: Se crea una lista de RadioButtons del Porcentaje
                    case 1:
                        seleccion_items[0] = which + 1;
                        pos_seleccion[0] = which;
                        tvPor.setText(items_porcentaje[which]);
                        btnPor.setText(getResources().getString(R.string.titlebuttonCambiar));
                        if (op_reg.equals("M")) onStepOpening(1);
                        else onStepOpening(3);
                        break;
                    //------------------------------------------------------------------------------
                    //DIALOGO: Se crea una lista de RadioButtons de la Frecuencia
                    case 2:
                        seleccion_items[1] = which + 1;
                        pos_seleccion[1] = which;
                        tvFre.setText(items_frecuencia[which]);
                        btnFre.setText(getResources().getString(R.string.titlebuttonCambiar));
                        if (op_reg.equals("M")) onStepOpening(2);
                        else onStepOpening(4);
                        break;
                    default:
                        break;
                }
            }
        })
                .setNegativeButton(getResources().getString(R.string.registration_form_dialog_option_ok),
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
     * METODO: Mostrar imagenes ampliadas
     **/
    public AlertDialog createDialogImage(final File file, int pos) {
        final int aux = pos;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_image, null);
        builder.setView(v);

        ImageView img = v.findViewById(R.id.dialogImage);
        bitMapImg(file, img);

        builder.setPositiveButton(getResources().getString(R.string.titlebuttonCambiar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        state = true;
                        position = aux;
                        name_change = file.getName();
                        getCamara1();
                        builder.create().dismiss();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.registration_form_dialog_option_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.create().dismiss();
                    }
                });

        return builder.create();
    }

    /*----------------------------------------------------------------------------------------------
    * Crea un dialogo para el concepto técnico del monitor
    *
    * @return <code>AlertDialog</code> : Dialogo para el item*/
    private AlertDialog createDialogTechnicalConcept() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_technical_concept, null);
        builder.setView(v);

        final TextInputLayout tilTechnicalConcept = v.findViewById(R.id.tilTechnicalConcept);
        final EditText etTechnicalConcept = v.findViewById(R.id.etTechnicalConcept);

        etTechnicalConcept.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > tilTechnicalConcept.getCounterMaxLength())
                    tilTechnicalConcept.setError(getResources().getString(R.string.technicalConceptStepError2));
                else tilTechnicalConcept.setError(null);
            }
        });

        String tvText = tvTechnicalConcept.getText().toString();
        etTechnicalConcept.setText(tvText);


        builder.setPositiveButton(getResources().getString(R.string.info_dialog_option_accept),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        technicalConcept = etTechnicalConcept.getText().toString();
                        tvTechnicalConcept.setText(technicalConcept);

                        InputMethodManager manager = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(etTechnicalConcept.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                        if (op_reg.equals("M")) onStepOpening(5);
                        else onStepOpening(7);

                        builder.create().dismiss();
                    }
                });

        builder.setNegativeButton(getResources().getString(R.string.info_dialog_option_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager manager = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(etTechnicalConcept.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        builder.create().dismiss();
                    }
                });

        return builder.create();
    }

    /**
     * =============================================================================================
     **/

    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        assert op_reg != null;
        if (op_reg.equals("M")) {
            switch (stepNumber) {
                case 0:
                    view = createPruebasView();
                    break;
                case 1:
                    view = createPorcentajeView();
                    break;
                case 2:
                    view = createFrecuenciaView();
                    break;
                case 3:
                    view = createOrigenView();
                    break;
                case 4:
                    view = createRepercusionesView();
                    break;
                case 5:
                    view = createStepTechnicalCocept();
                    break;
            }
        } else {
            switch (stepNumber) {
                case 0:
                    view = createFactorVariable();
                    break;
                case 1:
                    view = createCoordenadasView();
                    break;
                case 2:
                    view = createPruebasView();
                    break;
                case 3:
                    view = createPorcentajeView();
                    break;
                case 4:
                    view = createFrecuenciaView();
                    break;
                case 5:
                    view = createOrigenView();
                    break;
                case 6:
                    view = createRepercusionesView();
                    break;
                case 7:
                    view = createStepTechnicalCocept();
                    break;
            }
        }

        return view;
    }

    /**
     * =============================================================================================
     **/

    @Override
    public void onStepOpening(int stepNumber) {
        if (op_reg.equals("M")) {
            switch (stepNumber) {
                case 0:
                    if (files.size() > 0) verticalStepperForm.setStepAsCompleted(0);
                    else {
                        String errorMessage = getResources().getString(R.string.descriptionFormRegistrationMonitoringCameraDialog);
                        verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
                    }
                    break;
                case 1:
                    if (seleccion_items[0] != 0) verticalStepperForm.setStepAsCompleted(1);
                    else {
                        String errorMessage = getResources().getString(R.string.descriptionFormRegistrationMonitoringPorcentajeError);
                        verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
                    }
                    break;
                case 2:
                    if (seleccion_items[1] != 0) verticalStepperForm.setStepAsCompleted(2);
                    else {
                        String errorMessage = getResources().getString(R.string.descriptionFormRegistrationMonitoringFrecuenciaError);
                        verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
                    }
                    break;
                case 3:
                    verticalStepperForm.setStepAsCompleted(3);
                    break;
                case 4:
                    verticalStepperForm.setStepAsCompleted(4);
                    break;
                case 5:
                    int sizeText = technicalConcept.length();
                    String errorMessage = getResources().getString(R.string.technicalConceptStepError1);

                    if (sizeText > 0 && sizeText < 250) verticalStepperForm.setStepAsCompleted(5);
                    else {
                        if (sizeText > 250) errorMessage = getResources().getString(R.string.technicalConceptStepError2);
                        verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
                    }
                    break;
            }
        } else {
            String errorMessage;
            switch (stepNumber) {
                case 0:
                    if (!opciones[1].equals("")) verticalStepperForm.setStepAsCompleted(0);
                    else {
                        errorMessage = getResources().getString(R.string.descriptionFormRegistrationMonitoringVariableDialog);
                        verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
                    }
                    break;
                case 1:
                    errorMessage = getResources().getString(R.string.descriptionFormRegistrationMonitoringCoordenadasError);
                    float valueAltitude = Float.parseFloat(altitud);
                    if (!latitud.equals("") && !longitud.equals("") && valueAltitude < 10000f)
                        verticalStepperForm.setStepAsCompleted(1);
                    else {
                        if (valueAltitude > 10000f) errorMessage = getResources().getString(R.string.altitudeStepError);
                        verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
                    }
                    break;
                case 2:
                    if (files.size() > 0) verticalStepperForm.setStepAsCompleted(2);
                    else {
                        errorMessage = getResources().getString(R.string.descriptionFormRegistrationMonitoringCameraDialog);
                        verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
                    }
                    break;
                case 3:
                    if (seleccion_items[0] != 0) verticalStepperForm.setStepAsCompleted(3);
                    else {
                        errorMessage = getResources().getString(R.string.descriptionFormRegistrationMonitoringPorcentajeError);
                        verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
                    }
                    break;
                case 4:
                    if (seleccion_items[1] != 0) verticalStepperForm.setStepAsCompleted(4);
                    else {
                        errorMessage = getResources().getString(R.string.descriptionFormRegistrationMonitoringFrecuenciaError);
                        verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
                    }
                    break;
                case 5:
                    verticalStepperForm.setStepAsCompleted(5);
                    break;
                case 6:
                    verticalStepperForm.setStepAsCompleted(6);
                    break;
                case 7:
                    int sizeText = technicalConcept.length();
                    errorMessage = getResources().getString(R.string.technicalConceptStepError1);

                    if (sizeText > 0 && sizeText < 250) verticalStepperForm.setStepAsCompleted(7);
                    else {
                        if (sizeText > 250) errorMessage = getResources().getString(R.string.technicalConceptStepError2);
                        verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
                    }
                    break;
            }
        }
    }

    @Override
    public void sendData() {
        fragment = new MonitoringSummaryFragment();
        Bundle params = new Bundle();

        comprobarOrigen();
        comprobarRepercusiones1();
        comprobarRepercusiones2();

        String rep = "";
        for (int i = 0; i < 4; i++) rep += "" + repercusiones[i];

        params.putString("MONITOR", monitor);
        params.putString("FECHA", fecha);
        params.putString("REPERCUSIONES", rep);
        params.putString("ORIGEN", origen);
        params.putString("POR_NAME", items_porcentaje[pos_seleccion[0]]);
        params.putString("POR_NUMBER", seleccion_items[0] + "");
        params.putString("FRE_NAME", items_frecuencia[pos_seleccion[1]]);
        params.putString("FRE_NUMBER", seleccion_items[1] + "");
        params.putString("FILES_NUMBER", files.size() + "");
        params.putString("CONCEPT", technicalConcept);
        params.putString("ALTITUD", altitud);


        if (files.size() >= 1) params.putString("PRUEBA1", files.get(0).getName());
        if (files.size() >= 2) params.putString("PRUEBA2", files.get(1).getName());
        if (files.size() == 3) params.putString("PRUEBA3", files.get(2).getName());

        if (op_reg.equals("N")) {
            if (!opciones[1].equals("")) {
                params.putString("VAR_COD", opciones[1]);
                params.putString("VAR_NAME", items_variables[pos[1]].toString());
                params.putString("FACTOR_NAME", items_factores[pos[0]].toString());
                params.putString("AREA", area);
                params.putString("LATITUD", latitud);
                params.putString("LATITUD_NUMBER", processCoordinate(latitud, "LATITUD"));
                params.putString("LONGITUD", longitud);
                params.putString("LONGITUD_NUMBER", processCoordinate(longitud, "LONGITUD"));
                params.putString("TYPE_UPLOAD", "NEW");

                fragment.setArguments(params);

                //Inflamos el layout para el Fragmento MonitoringListFragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack("MonitoringRegistration")
                        .replace(R.id.monitoring_principal_context, fragment)
                        .commit();
            } else
                createSimpleDialog(
                        getResources().getString(R.string.descriptionFormRegistrationMonitoringVariableDialog),
                        getResources().getString(R.string.titleFormRegistrationMonitoringDialog)).show();
        } else {
            params.putString("VAR_NAME", variable);
            params.putString("FACTOR_NAME", factor);
            params.putString("LATITUD", lat);
            params.putString("LATITUD_NUMBER", "0");
            params.putString("LONGITUD", logt);
            params.putString("LONGITUD_NUMBER", "0");
            params.putString("PUNTO_AFECTACION", punto_afectacion);
            params.putString("TYPE_UPLOAD", "MONITORING");

            fragment.setArguments(params);

            //Inflamos el layout para el Fragmento MonitoringListFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.monitoring_principal_context, fragment)
                    .commit();
        }
    }

    /**
     * =============================================================================================
     * METODO: CREA LA INTERFAZ PARA EL FACTOR Y LA VARIABLE EN EL FORMULARIO
     **/
    private View createFactorVariable() {
        LayoutInflater inflater = LayoutInflater.from(activity.getBaseContext());
        LinearLayout factorVariableLayoutContent = (LinearLayout) inflater.inflate(R.layout.item_registration_form_factor_variable, null, false);

        btnFac = factorVariableLayoutContent.findViewById(R.id.btn_factor);
        btnVar = factorVariableLayoutContent.findViewById(R.id.btn_variable);
        tvFac = factorVariableLayoutContent.findViewById(R.id.tv_factor);
        tvVar = factorVariableLayoutContent.findViewById(R.id.tv_variable);

        btnVar.setEnabled(false);

        btnFac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRadioListDialog(items_factores, getResources().getString(R.string.registration_form_dialog_title_factors), 1).show();
            }
        });
        btnVar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRadioListDialog(items_variables, getResources().getString(R.string.registration_form_dialog_title_variables), 2).show();
            }
        });

        return factorVariableLayoutContent;
    }

    /**
     * =============================================================================================
     * METODO: CREA LA INTERFAZ PARA LAS COORDENADAS
     **/
    private View createCoordenadasView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity().getBaseContext());
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.item_registration_form_coordenadas, null, false);

        fabLat = layout.findViewById(R.id.fab_latitud);
        fabLong = layout.findViewById(R.id.fab_longitud);
        tvLat = layout.findViewById(R.id.tv_latitud);
        tvLong = layout.findViewById(R.id.tv_longitud);
        etAltitude = layout.findViewById(R.id.etAltitude);

        etAltitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("")) altitud = editable.toString();
                onStepOpening(1);
            }
        });

        fabLat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String opcard1 = "10";
                String cadnum;
                int opdeg = 0;
                int opmin = 0;
                int opsec = 0;

                if (!latitud.equals("")) {
                    String cadLat = processCoordinate(latitud, "LATITUD");
                    if (cadLat.charAt(0) == 'S') opcard1 = "01";
                    //Obtenemos los grados
                    cadnum = cadLat.charAt(1) + "" + cadLat.charAt(2);
                    opdeg = Integer.parseInt(cadnum);
                    //Obtenemos los minutos
                    cadnum = cadLat.charAt(3) + "" + cadLat.charAt(4);
                    opmin = Integer.parseInt(cadnum);
                    //Obtenemos los segundos
                    cadnum = cadLat.charAt(5) + "" + cadLat.charAt(6);
                    opsec = Integer.parseInt(cadnum);
                }

                createCoordinatesDialog("LATITUD", opcard1, opdeg, opmin, opsec).show();
            }
        });

        fabLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String opcard2 = "10";
                String cadnum;
                int opdeg = 0;
                int opmin = 0;
                int opsec = 0;

                if (!longitud.equals("")) {
                    String cadLong = processCoordinate(longitud, "LONGITUD");
                    if (cadLong.charAt(0) == 'W') opcard2 = "01";
                    //Obtenemos los grados
                    cadnum = cadLong.charAt(1) + "" + cadLong.charAt(2) + "" + cadLong.charAt(3);
                    opdeg = Integer.parseInt(cadnum);
                    //Obtenemos los minutos
                    cadnum = cadLong.charAt(4) + "" + cadLong.charAt(5);
                    opmin = Integer.parseInt(cadnum);
                    //Obtenemos los segundos
                    cadnum = cadLong.charAt(6) + "" + cadLong.charAt(7);
                    opsec = Integer.parseInt(cadnum);
                }

                createCoordinatesDialog("LONGITUD", opcard2, opdeg, opmin, opsec).show();
            }
        });

        return layout;
    }

    /**
     * =============================================================================================
     * METODO: CREA LA INTERFAZ PARA LAS PRUEBAS
     **/

    private View createPruebasView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity().getBaseContext());
        LinearLayout pruebasLayoutContent = (LinearLayout) inflater.inflate(R.layout.item_registration_form_pruebas, null, false);

        fabCamera1 = pruebasLayoutContent.findViewById(R.id.fab_camera1);
        fabCamera2 = pruebasLayoutContent.findViewById(R.id.fab_camera2);
        fabCamera3 = pruebasLayoutContent.findViewById(R.id.fab_camera3);

        tvImage1 = pruebasLayoutContent.findViewById(R.id.tv_image1);
        tvImage2 = pruebasLayoutContent.findViewById(R.id.tv_image2);
        tvImage3 = pruebasLayoutContent.findViewById(R.id.tv_image3);

        fabCamera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (files.size() == 0) checkPermission();
                else if (files.size() > 0) {
                    createDialogImage(files.get(0), 0).show();
                }
            }
        });

        fabCamera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (files.size() == 1) getCamara1();
                else if (files.size() > 1) {
                    createDialogImage(files.get(1), 1).show();
                }
            }
        });

        fabCamera3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (files.size() == 2) getCamara1();
                else if (files.size() > 2) {
                    createDialogImage(files.get(2), 2).show();
                }
            }
        });

        return pruebasLayoutContent;
    }

    /**
     * =============================================================================================
     * METODO: CREA LA INTERFAZ PARA EL PORCENTAJE DE APARICIÓN
     **/

    private View createPorcentajeView() {
        LayoutInflater inflater = LayoutInflater.from(activity.getBaseContext());
        LinearLayout porcentajeLayoutContent = (LinearLayout) inflater.inflate(R.layout.item_registration_form_porcentaje, null, false);

        btnPor = porcentajeLayoutContent.findViewById(R.id.btn_porcentaje);
        tvPor = porcentajeLayoutContent.findViewById(R.id.tv_porcentaje);

        btnPor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSelectorDialog(items_porcentaje, getResources().getString(R.string.registration_form_dialog_title_percentage), 1).show();
            }
        });

        return porcentajeLayoutContent;
    }

    /**
     * =============================================================================================
     * METODO: CREA LA INTERFAZ PARA LA FRECUENCIA DE APARICIÓN
     **/
    private View createFrecuenciaView() {
        LayoutInflater inflater = LayoutInflater.from(activity.getBaseContext());
        LinearLayout frecuenciaLayoutContent = (LinearLayout) inflater.inflate(R.layout.item_registration_form_frecuencia, null, false);

        btnFre = frecuenciaLayoutContent.findViewById(R.id.btn_frecuencia);
        tvFre = frecuenciaLayoutContent.findViewById(R.id.tv_frecuencia);

        btnFre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSelectorDialog(items_frecuencia, getResources().getString(R.string.registration_form_dialog_title_frequency), 2).show();
            }
        });

        return frecuenciaLayoutContent;
    }

    /**
     * =============================================================================================
     * METODO: CREA LA INTERFAZ PARA EL ORIGEN
     **/
    private View createOrigenView() {
        LayoutInflater inflater = LayoutInflater.from(activity.getBaseContext());
        RelativeLayout origenLayoutContent = (RelativeLayout) inflater.inflate(R.layout.item_registration_form_origen, null, false);

        rgOrg = origenLayoutContent.findViewById(R.id.rg_origen);

        return origenLayoutContent;
    }

    /**
     * =============================================================================================
     * METODO: CREA LA INTERFAZ PARA LAS REPERCUSIONES
     **/
    private View createRepercusionesView() {
        LayoutInflater inflater = LayoutInflater.from(activity.getBaseContext());
        LinearLayout repercusionesLayoutContent = (LinearLayout) inflater.inflate(R.layout.item_registration_form_repercusiones, null, false);

        rgRep1 = repercusionesLayoutContent.findViewById(R.id.rg_repercusiones1);
        rgRep2 = repercusionesLayoutContent.findViewById(R.id.rg_repercusiones2);

        return repercusionesLayoutContent;
    }

    /*----------------------------------------------------------------------------------------------
    * Crea la Interfaz para el concepto técnico
    *
    * @param <code>View view</code> : Vista creada para el formulario*/
    private View createStepTechnicalCocept() {
        LayoutInflater inflater = LayoutInflater.from(activity.getBaseContext());
        LinearLayout technicalConceptStep = (LinearLayout) inflater.inflate(R.layout.step_technical_concept, null, false);

        Button btnEdit = technicalConceptStep.findViewById(R.id.btnEdit);
        tvTechnicalConcept = technicalConceptStep.findViewById(R.id.tvTechnicalConcept);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialogTechnicalConcept().show();
            }
        });

        return technicalConceptStep;
    }

    /**
     * =============================================================================================
     * METODO: Valida la selección del Checbox Repercusiones (Positivas/Negativas)
     **/
    public void comprobarRepercusiones1() {
        switch (rgRep1.getCheckedRadioButtonId()) {
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
        switch (rgRep2.getCheckedRadioButtonId()) {
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
        switch (rgOrg.getCheckedRadioButtonId()) {
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
     * METODO: Presenta en Interfaz un mensaje Tipo Dialog para las Coordenadas
     **/
    public AlertDialog createCoordinatesDialog(final String titulo, String op_check, int op_deg, int op_min, int op_sec) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_coordinates, null);
        builder.setView(v);

        TextView tvTitle = v.findViewById(R.id.tv_title_dialog);
        final RadioButton diag1 = v.findViewById(R.id.rb_card1);
        RadioButton diag2 = v.findViewById(R.id.rb_card2);
        final NumberPicker npDegrees = v.findViewById(R.id.np_degrees);
        final NumberPicker npMinutes = v.findViewById(R.id.np_minutes);
        final NumberPicker npSeconds = v.findViewById(R.id.np_seconds);

        npMinutes.setMaxValue(60);
        npMinutes.setMinValue(0);
        npSeconds.setMaxValue(60);
        npSeconds.setMinValue(0);
        npDegrees.setMinValue(0);

        if (op_check.charAt(0) == '1') diag1.setChecked(true);
        else diag2.setChecked(true);

        if (titulo.equals("LATITUD")) {
            npDegrees.setMaxValue(90);
            diag1.setButtonDrawable(getResources().getDrawable(R.drawable.rb_positive_north));
            diag2.setButtonDrawable(getResources().getDrawable(R.drawable.rb_negative_south));
            tvTitle.setText(getResources().getString(R.string.latitudePointDialog));
        } else {
            npDegrees.setMaxValue(180);
            diag1.setButtonDrawable(getResources().getDrawable(R.drawable.rb_positive_east));
            diag2.setButtonDrawable(getResources().getDrawable(R.drawable.rb_negative_west));
            tvTitle.setText(getResources().getString(R.string.lengthPointDialog));
        }

        npDegrees.setValue(op_deg);
        npMinutes.setValue(op_min);
        npSeconds.setValue(op_sec);

        builder.setPositiveButton(getResources().getString(R.string.titlebuttonFijar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (titulo.equals("LATITUD")) {
                            String punto_card, degrees, minutes, seconds;
                            //Comprobamos la selección del punto cardinal
                            if (diag1.isChecked()) punto_card = "N-";
                            else punto_card = "S-";
                            //Completamos los grados para valores de una cifra
                            if (npDegrees.getValue() < 10) degrees = "0" + npDegrees.getValue();
                            else degrees = "" + npDegrees.getValue();
                            //Completamos los minutos para valores de una cifra
                            if (npMinutes.getValue() < 10) minutes = "0" + npMinutes.getValue();
                            else minutes = "" + npMinutes.getValue();
                            //Completamos los segundos para valores de una cifra
                            if (npSeconds.getValue() < 10) seconds = "0" + npSeconds.getValue();
                            else seconds = "" + npSeconds.getValue();

                            latitud = punto_card + degrees + "°" + minutes + "'" + seconds + "''";
                            tvLat.setText(latitud);
                        } else {
                            String punto_card, degrees, minutes, seconds;
                            //Comprobamos la selección del punto cardinal
                            if (diag1.isChecked()) punto_card = "E-";
                            else punto_card = "W-";
                            //Completamos los grados para valores de una cifra
                            if (npDegrees.getValue() < 10) degrees = "00" + npDegrees.getValue();
                            else if (npDegrees.getValue() < 100)
                                degrees = "0" + npDegrees.getValue();
                            else degrees = "" + npDegrees.getValue();
                            //Completamos los minutos para valores de una cifra
                            if (npMinutes.getValue() < 10) minutes = "0" + npMinutes.getValue();
                            else minutes = "" + npMinutes.getValue();
                            //Completamos los segundos para valores de una cifra
                            if (npSeconds.getValue() < 10) seconds = "0" + npSeconds.getValue();
                            else seconds = "" + npSeconds.getValue();

                            longitud = punto_card + degrees + "°" + minutes + "'" + seconds + "''";
                            tvLong.setText(longitud);
                        }

                        onStepOpening(1);
                        builder.create().dismiss();
                    }
                });

        return builder.create();
    }

    /**
     * =============================================================================================
     * METODO: CHEQUEA LOS PERMISOS DEL DISPOSITIVO
     **/
    private void checkPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) getCamara1();
        else {
            int hasWriteContactsPermission = ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_WRITE_EXTERNAL_STORAGE);
            } else getCamara1();
        }
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (MY_WRITE_EXTERNAL_STORAGE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) getCamara1();
            else {
                Toast.makeText(getActivity(), getResources().getString(R.string.registration_message_permissions) + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    public String processCoordinate(String cad, String opc_coord) {
        String pc, minu, sec, deg, coordinate;
        pc = "" + cad.charAt(0);

        if (opc_coord.equals("LATITUD")) {
            deg = "" + cad.charAt(2) + "" + cad.charAt(3);
            minu = "" + cad.charAt(5) + "" + cad.charAt(6);
            sec = "" + cad.charAt(8) + "" + cad.charAt(9);
        } else {
            deg = "" + cad.charAt(2) + "" + cad.charAt(3) + "" + cad.charAt(4);
            minu = "" + cad.charAt(6) + "" + cad.charAt(7);
            sec = "" + cad.charAt(9) + "" + cad.charAt(10);
        }

        coordinate = pc + deg + minu + sec;
        return coordinate;
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    private void changeStateImage(TextView textView) {
        textView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundStatePositive));
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_registration, 0, 0, 0);
    }

}

