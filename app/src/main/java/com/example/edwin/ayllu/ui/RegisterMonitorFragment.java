package com.example.edwin.ayllu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.edwin.ayllu.AdminSQLite;
import com.example.edwin.ayllu.domain.SHA1;
import com.example.edwin.ayllu.domain.Usuario;
import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.io.RestClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.regex.Pattern;

public class RegisterMonitorFragment extends Fragment {
    private EditText etI, etN, etA, etC,etC2;
    private Button bt;
    private ArrayList<String> res;
    private Activity activity;
    private String pais;
    private TextInputLayout tilNombre,tilApellido,tilIdentificacion,tilCon1,tilCon2;
    private static final String TAG = "ERRORES";
    private OnFragmentInteractionListener mListener;

    public RegisterMonitorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //se instancian los elemento de la vista
        View view = inflater.inflate(R.layout.fragment_register_monitor, container, false);
        etI = (EditText)view.findViewById(R.id.txtide);
        etN = (EditText)view.findViewById(R.id.txtname);
        etA = (EditText)view.findViewById(R.id.txtApe);
        etC = (EditText)view.findViewById(R.id.txtCon);
        etC2 = (EditText)view.findViewById(R.id.contraseña2);
        bt = (Button)view.findViewById(R.id.btnReg);

        tilIdentificacion = (TextInputLayout) view.findViewById(R.id.til_ide);
        tilNombre = (TextInputLayout) view.findViewById(R.id.til_nom);
        tilApellido = (TextInputLayout) view.findViewById(R.id.til_ape);
        tilCon1 = (TextInputLayout) view.findViewById(R.id.til_con);
        tilCon2 = (TextInputLayout) view.findViewById(R.id.til_con1);

        //se realiza la invocacion del metodo setOnclickListener que es el encargado de registrar un nuevo monitor
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ci = false;
                boolean i = esIdentificacionValido(tilIdentificacion.getEditText().getText().toString(),tilIdentificacion);
                Log.e("tamaño",""+tilIdentificacion.getEditText().getText().toString().length());
                boolean n = esNombreValido(tilNombre.getEditText().getText().toString(),tilNombre);
                boolean a = esNombreValido(tilApellido.getEditText().getText().toString(),tilApellido);
                boolean c = esContrasenaValido(tilCon1.getEditText().getText().toString(),tilCon1);
                boolean c1 = esContrasenaValido(tilCon2.getEditText().getText().toString(),tilCon2);

                if(c && c1) ci = esContrasenaIguales();

                if(i && n && a && ci ){

                    //se realiza una consulta a la base de datos del movil para obtener el pais del administrador
                    AdminSQLite admin = new AdminSQLite(getContext(),"login", null, 1);
                    SQLiteDatabase bd = admin.getReadableDatabase();
                    Cursor datos = bd.rawQuery(
                            "select "+admin.PAI_USU+ " from "+ admin.TABLENAME + " where "+ admin.TIP_USU + "='A'", null);

                    Log.i("TAG", "error " + datos.getCount());
                    if (datos.moveToFirst()) {
                        //Recorremos el cursor hasta que no haya más registros
                        do {
                            pais = datos.getString(0);

                        } while(datos.moveToNext());
                    }
                    bd.close();

                    //se valida si las 2 contraseñas ingresa son iguales
                    if(etC.getText().toString().equals(etC2.getText().toString())){
                        //String encrip = new String(DigestUtils.md5Hex(etC.getText().toString()));
                        //se realiza la peticion al servidor para el registro del monitor
                        final ProgressDialog loading = ProgressDialog.show(getContext(),getResources().getString(R.string.registration_form_process_shipping_status),getResources().getString(R.string.registration_form_process_message),false,false);
                        String pass = SHA1.getHash(etC.getText().toString(),"SHA1");
                        RestClient service = RestClient.retrofit.create(RestClient.class);
                        Call<ArrayList<String>> requestAdd = service.addUsuario(etI.getText().toString(),etN.getText().toString(),etA.getText().toString(),"M",pass,pais);
                        requestAdd.enqueue(new Callback<ArrayList<String>>() {
                            @Override
                            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                                loading.dismiss();
                                if (response.isSuccessful()) {
                                    res = response.body();
                                    if(res.get(0).equals("1")){
                                        Toast login = Toast.makeText(getContext(),
                                                getResources().getString(R.string.registration_form_process_successful_message), Toast.LENGTH_LONG);
                                        login.show();

                                        //peticion al servidor que se encarga de obtener el monitor registrado para actualizar la base de datos del movil
                                        RestClient service = RestClient.retrofit.create(RestClient.class);
                                        Call<ArrayList<Usuario>> requestAdd = service.update(etI.getText().toString());
                                        requestAdd.enqueue(new Callback<ArrayList<Usuario>>() {
                                            @Override
                                            public void onResponse(Call<ArrayList<Usuario>> call, Response<ArrayList<Usuario>> response) {
                                                ArrayList<Usuario> up = response.body();
                                                activity = getActivity();
                                                AdminSQLite al = new AdminSQLite(activity, "login", null, 1);
                                                SQLiteDatabase bd1 = al.getWritableDatabase();
                                                ContentValues monitores = new ContentValues();

                                                monitores.put(al.COD_USU, up.get(0).getCodigo_usu());
                                                monitores.put(al.IDE_USU, etI.getText().toString());
                                                monitores.put(al.NOM_USU, etN.getText().toString());
                                                monitores.put(al.APE_USU, etA.getText().toString());
                                                monitores.put(al.TIP_USU, "M");
                                                monitores.put(al.CON_USU, etC.getText().toString());
                                                monitores.put(al.CLA_API, up.get(0).getClave_api());
                                                monitores.put(al.PAI_USU, pais);
                                                bd1.insert(al.TABLENAME, null, monitores);

                                                bd1.close();

                                                etI.setText("");
                                                etN.setText("");
                                                etA.setText("");
                                                etC.setText("");
                                                etC2.setText("");

                                            }

                                            @Override
                                            public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {

                                            }
                                        });
                                        Log.i("TAG", "error "+ res.get(0) );
                                    }
                                    else{
                                        Toast login = Toast.makeText(getContext(),
                                                getResources().getString(R.string.registration_form_process_error_message), Toast.LENGTH_SHORT);
                                        login.show();
                                    }

                                } else {
                                    Toast login = Toast.makeText(getContext(),
                                            getResources().getString(R.string.registration_form_process_error_message), Toast.LENGTH_SHORT);
                                    login.show();
                                    int statusCode = response.code();
                                    Log.i("TAG", "error " + response.code());

                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                                loading.dismiss();
                                Toast prueba = Toast.makeText(getContext(), getResources().getString(R.string.registration_form_process_message_server), Toast.LENGTH_LONG);
                                prueba.show();
                            }
                        });

                    }
                    else{
                        Toast prueba = Toast.makeText(getContext(), getResources().getString(R.string.registration_form_alert_different_passwords), Toast.LENGTH_LONG);
                        prueba.show();
                    }
                }

            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //METODOS: validan los datos que se introducen en las cajas de texto (nombre, apellido,contraseñas)

    private boolean esNombreValido(String nombre,TextInputLayout til) {
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        if (!patron.matcher(nombre).matches() || nombre.length() > 30) {
            til.setError(getResources().getString(R.string.registration_form_alert_invalid_field));
            return false;
        } else {
            til.setError(null);
        }

        return true;
    }

    private boolean esIdentificacionValido(String nombre,TextInputLayout til) {
        if(nombre.length()<=0) til.setError(getResources().getString(R.string.registration_form_alert_invalid_field));
        //Pattern patron = Pattern.compile("(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$");
        else {
            til.setError(null);
        }

        return true;
    }

    private boolean esContrasenaValido(String nombre, TextInputLayout til) {
        Pattern patron = Pattern.compile("(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$");
        if (!patron.matcher(nombre).matches() || nombre.length() > 30) {
            til.setError(getResources().getString(R.string.registration_form_alert_password));
            return false;
        } else {
            til.setError(null);
        }

        return true;
    }
    private boolean esContrasenaIguales() {
        if (!etC.getText().toString().equals(etC2.getText().toString())) {
            tilCon1.setError(getResources().getString(R.string.registration_form_alert_different_passwords));
            tilCon2.setError(getResources().getString(R.string.registration_form_alert_different_passwords));
            return false;
        } else {
            tilCon1.setError(null);
            tilCon2.setError(null);
        }

        return true;
    }
}
