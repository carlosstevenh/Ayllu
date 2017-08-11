package com.example.edwin.ayllu.ui;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.edwin.ayllu.AdminSQLite;
import com.example.edwin.ayllu.domain.SHA1;
import com.example.edwin.ayllu.domain.Usuario;
import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.io.RestClient;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.regex.Pattern;

public class AdminUserFormFragment extends Fragment implements View.OnClickListener {
    private EditText etID, etName, etSurname, etPassword, etConfirmation;
    private TextInputLayout tilID, tilName, tilSurname, tilPassword, tilConfirmation;

    private String transaction_type;

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        transaction_type = intent.getExtras().getString("TYPE");
    }

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_user_form, container, false);

        etID = (EditText) view.findViewById(R.id.et_id);
        etName = (EditText) view.findViewById(R.id.et_name);
        etSurname = (EditText) view.findViewById(R.id.et_surname);
        etPassword = (EditText) view.findViewById(R.id.et_psw);
        etConfirmation = (EditText) view.findViewById(R.id.et_conf_psw);

        tilID = (TextInputLayout) view.findViewById(R.id.til_id);
        tilName = (TextInputLayout) view.findViewById(R.id.til_name);
        tilSurname = (TextInputLayout) view.findViewById(R.id.til_surname);
        tilPassword = (TextInputLayout) view.findViewById(R.id.til_psw);
        tilConfirmation = (TextInputLayout) view.findViewById(R.id.til_conf_psw);

        FloatingActionButton fbTransaction = (FloatingActionButton) view.findViewById(R.id.fab_transaction);
        if (transaction_type.equals("UPDATE")) fbTransaction.setIcon(R.drawable.ic_refresh);
        fbTransaction.setOnClickListener(this);

        return view;
    }

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_transaction:
                if (!transaction_type.equals("UPDATE")) registerUser();
                break;
        }
    }

    /**
     * =============================================================================================
     * METODO:
     */
    private void registerUser() {
        if (isValidFields()) {
            //--------------------------------------------------------------------------------------
            //se realiza una consulta a la base de datos del movil para obtener el pais del
            //administrador
            String pais = "";
            AdminSQLite admin = new AdminSQLite(getActivity(), "login", null, 1);
            SQLiteDatabase bd = admin.getReadableDatabase();
            Cursor datos = bd.rawQuery(
                    "select " + AdminSQLite.PAI_USU + " from " + AdminSQLite.TABLENAME + " where " + AdminSQLite.TIP_USU + "='A'", null);

            if (datos.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    pais = datos.getString(0);

                } while (datos.moveToNext());
            }
            bd.close();
            datos.close();
            //--------------------------------------------------------------------------------------
            //se realiza la peticion al servidor para el registro del monitor
            final ProgressDialog loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.registration_form_process_shipping_status), getResources().getString(R.string.registration_form_process_message), false, false);
            String pass = SHA1.getHash(etPassword.getText().toString(), "SHA1");

            RestClient service = RestClient.retrofit.create(RestClient.class);
            Call<ArrayList<String>> requestAdd = service.addUsuario(
                    etID.getText().toString(),
                    etName.getText().toString(),
                    etSurname.getText().toString(),
                    "M", pass, pais);
            final String finalPais = pais;
            requestAdd.enqueue(new Callback<ArrayList<String>>() {
                @Override
                public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                    loading.dismiss();
                    if (response.isSuccessful()) {
                        ArrayList<String> res = response.body();
                        if (res.get(0).equals("1")) {
                            Toast login = Toast.makeText(getActivity(),
                                    getResources().getString(R.string.registration_form_process_successful_message), Toast.LENGTH_LONG);
                            login.show();

                            //peticion al servidor que se encarga de obtener el monitor registrado para actualizar la base de datos del movil
                            RestClient service = RestClient.retrofit.create(RestClient.class);
                            Call<ArrayList<Usuario>> requestAdd = service.update(etID.getText().toString());
                            requestAdd.enqueue(new Callback<ArrayList<Usuario>>() {
                                @Override
                                public void onResponse(Call<ArrayList<Usuario>> call, Response<ArrayList<Usuario>> response) {
                                    ArrayList<Usuario> up = response.body();
                                    AdminSQLite al = new AdminSQLite(getActivity(), "login", null, 1);
                                    SQLiteDatabase bd1 = al.getWritableDatabase();
                                    ContentValues monitores = new ContentValues();

                                    monitores.put(AdminSQLite.COD_USU, up.get(0).getCodigo_usu());
                                    monitores.put(AdminSQLite.IDE_USU, etID.getText().toString());
                                    monitores.put(AdminSQLite.NOM_USU, etName.getText().toString());
                                    monitores.put(AdminSQLite.APE_USU, etSurname.getText().toString());
                                    monitores.put(AdminSQLite.TIP_USU, "M");
                                    monitores.put(AdminSQLite.CON_USU, etPassword.getText().toString());
                                    monitores.put(AdminSQLite.CLA_API, up.get(0).getClave_api());
                                    monitores.put(AdminSQLite.PAI_USU, finalPais);
                                    bd1.insert(AdminSQLite.TABLENAME, null, monitores);

                                    bd1.close();

                                    etID.setText("");
                                    etName.setText("");
                                    etSurname.setText("");
                                    etPassword.setText("");
                                    etConfirmation.setText("");

                                }

                                @Override
                                public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {

                                }
                            });
                            Log.i("TAG", "error " + res.get(0));
                        } else {
                            Toast login = Toast.makeText(getActivity(),
                                    getResources().getString(R.string.registration_form_process_error_message), Toast.LENGTH_SHORT);
                            login.show();
                        }

                    } else {
                        Toast login = Toast.makeText(getActivity(),
                                getResources().getString(R.string.registration_form_process_error_message), Toast.LENGTH_SHORT);
                        login.show();

                    }
                }

                @Override
                public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                    loading.dismiss();
                    Toast prueba = Toast.makeText(getActivity(), getResources().getString(R.string.registration_form_process_message_server), Toast.LENGTH_LONG);
                    prueba.show();
                }
            });
        }
    }

    /**
     * =============================================================================================
     * METODO:
     */
    private void updateUser() {

    }

    /**
     * =============================================================================================
     * METODO:
     */
    private boolean isValidFields() {
        boolean equals_passwords = false;
        boolean id, name, surname, password, confirmation;

        id = isValidID(etID.getText().toString(), tilID);
        name = isValidText(etName.getText().toString(), tilName);
        surname = isValidText(etSurname.getText().toString(), tilSurname);
        password = isValidPassword(etPassword.getText().toString(), tilPassword);
        confirmation = isValidPassword(etConfirmation.getText().toString(), tilConfirmation);

        if (password && confirmation) equals_passwords = isSamePasswords();
        return id && name && surname && equals_passwords;
    }

    /**
     * =============================================================================================
     * METODO: Valida los datos de las cajas de texto (Nombre, Apellido, Contraseñas)
     */
    private boolean isValidText(String texto, TextInputLayout til) {
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");

        if (!patron.matcher(texto).matches() || texto.length() > 30) {
            til.setError(getResources().getString(R.string.registration_form_alert_invalid_field));
            return false;
        } else {
            til.setError(null);
        }

        return true;
    }

    /**
     * =============================================================================================
     * METODO: Valida si una identificación es correcta
     */
    private boolean isValidID(String texto, TextInputLayout til) {
        if (texto.length() <= 0)
            til.setError(getResources().getString(R.string.registration_form_alert_invalid_field));
            //Pattern patron = Pattern.compile("(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$");
        else {
            til.setError(null);
        }

        return true;
    }

    /**
     * =============================================================================================
     * METODO: Valida si una contraseña es correcta
     */
    private boolean isValidPassword(String texto, TextInputLayout til) {
        Pattern patron = Pattern.compile("(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$");
        if (!patron.matcher(texto).matches() || texto.length() > 30) {
            til.setError(getResources().getString(R.string.registration_form_alert_password));
            return false;
        } else {
            til.setError(null);
        }

        return true;
    }

    /**
     * =============================================================================================
     * METODO: Valida si las contraseñas ingresadas son iguales
     */
    private boolean isSamePasswords() {
        if (!etPassword.getText().toString().equals(etConfirmation.getText().toString())) {
            tilPassword.setError(getResources().getString(R.string.registration_form_alert_different_passwords));
            tilConfirmation.setError(getResources().getString(R.string.registration_form_alert_different_passwords));
            return false;
        } else {
            tilPassword.setError(null);
            tilConfirmation.setError(null);
        }

        return true;
    }
}
