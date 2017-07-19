package com.example.edwin.ayllu;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.edwin.ayllu.io.RestClient;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InstitutionalEvaluationFormFragment extends Fragment implements View.OnClickListener{

    private Spinner eva, per, tie, pre, con,rec;
    String cod_mon = "";
    String [] eval, res;
    String pa,fm;
    LinearLayout lyPrincipal;
    ImageView ivType;
    TextView tvTitle, tvDescription;
    FloatingActionButton fabReg;
    MonitoringInfoFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //se reciben los parametros recibidos por la actividad anterior
        Bundle bundle = getActivity().getIntent().getExtras();
        pa = bundle.getString("pa");
        fm = bundle.getString("fm");

        //------------------------------------------------------------------------------------------
        //Obtenemos el codigo del monitor del usuario en sesión
        AdminSQLite admin = new AdminSQLite(getActivity().getApplicationContext(), "login", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        //Prepara la sentencia SQL para la consulta en la Tabla de usuarios
        Cursor cursor = bd.rawQuery("SELECT codigo, pais FROM login LIMIT 1", null);
        cursor.moveToFirst();
        cod_mon = cursor.getString(0);
        cursor.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_institutional_evaluation_form, container, false);

        lyPrincipal = (LinearLayout) view.findViewById(R.id.ly_principal);
        ivType = (ImageView) view.findViewById(R.id.iv_type_record);
        tvTitle = (TextView) view.findViewById(R.id.tv_title_record);
        tvDescription = (TextView) view.findViewById(R.id.tv_description_record);

        //se instancian los elementos de la vista
        eva = (Spinner) view.findViewById(R.id.spinner_eva);
        per = (Spinner) view.findViewById(R.id.spinner_per);
        tie = (Spinner) view.findViewById(R.id.spinner_tie);
        pre = (Spinner) view.findViewById(R.id.spinner_pre);
        con = (Spinner) view.findViewById(R.id.spinner_con);
        rec = (Spinner) view.findViewById(R.id.spinner_rec);

        fabReg = (FloatingActionButton) view.findViewById(R.id.fab_reg);
        fabReg.setOnClickListener(this);

        //se crean los elementos del spiner de evaluacion
        //se crean los elementos de los spinners de personal,tiempo,presupuesto,conocimeintos y recursos
        eval = getResources().getStringArray(R.array.list_general_evaluation);
        res = getResources().getStringArray(R.array.list_specific_evaluation);


        //se crean los adapters para los spinners
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, eval);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eva.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, res);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //se crea el spinner con los adapters
        per.setAdapter(dataAdapter1);
        tie.setAdapter(dataAdapter1);
        pre.setAdapter(dataAdapter1);
        con.setAdapter(dataAdapter1);
        rec.setAdapter(dataAdapter1);

        return view;
    }

    //metodo que se encarga de registrar una respuesta institucional
    public void registrar(View v){
        String evaluacion;

        //se realiza la conversion numeria dependiendo de la opcion que el monitor seleccione en la evaluacion
        String aux = String.valueOf(eva.getSelectedItem());
        if (aux.equals(eval[0])) evaluacion="1";
        else if (aux.equals(eval[1])) evaluacion = "2";
        else evaluacion = "3";

        //se obtiene el valor dependiendo de lo que el monitor selecciono y se la convierte a numeros para poder inserta en la base de datos
        String personal = (String.valueOf(per.getSelectedItem()));
        String tiempo = (String.valueOf(tie.getSelectedItem()));
        String presupuesto = (String.valueOf(pre.getSelectedItem()));
        String conocimiento = (String.valueOf(con.getSelectedItem()));
        String recursos = (String.valueOf(rec.getSelectedItem()));

        if(!per.getSelectedItem().equals(""))personal = comparar(personal);
        if(!tie.getSelectedItem().equals(""))tiempo = comparar(tiempo);
        if(!pre.getSelectedItem().equals(""))presupuesto = comparar(presupuesto);
        if(!con.getSelectedItem().equals(""))conocimiento = comparar(conocimiento);
        if(!rec.getSelectedItem().equals(""))recursos = comparar(recursos);

        //se realiza la peticion del registro de la respuesta institucional al servidor
        final ProgressDialog loading = ProgressDialog.show(getActivity(),getResources().getString(R.string.institutional_response_form_process_message_upload),getResources().getString(R.string.institutional_response_form_process_message),false,false);
        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<String>> requestUser = service.addRespuesta(pa,fm, evaluacion,personal,tiempo,presupuesto,recursos,conocimiento,cod_mon);
        requestUser.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                loading.dismiss();
                if(response.isSuccessful()){
                    if(response.body().get(0).equals("1")){
                        fragment = new MonitoringInfoFragment();
                        Bundle params = new Bundle();
                        params.putString("RESULT","OK");
                        params.putString("TIPO","EVALUATION");
                        fragment.setArguments(params);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.institutional_form_principal_context, fragment)
                                .commit();
                    }
                    else{
                        fragment = new MonitoringInfoFragment();
                        Bundle params = new Bundle();
                        params.putString("RESULT","ERROR");
                        params.putString("TIPO","EVALUATION");
                        fragment.setArguments(params);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.institutional_form_principal_context, fragment)
                                .commit();
                    }
                }
                else {
                    fragment = new MonitoringInfoFragment();
                    Bundle params = new Bundle();
                    params.putString("RESULT","ERROR");
                    params.putString("TIPO","EVALUATION");
                    fragment.setArguments(params);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .add(R.id.institutional_form_principal_context, fragment)
                            .commit();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                loading.dismiss();
                fragment = new MonitoringInfoFragment();
                Bundle params = new Bundle();
                params.putString("RESULT","ERROR");
                params.putString("TIPO","EVALUATION");
                fragment.setArguments(params);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.institutional_form_principal_context, fragment)
                        .commit();
            }
        });
    }

    //metodo encargador de convertir el resultado del spiner a un valir numerico
    private String comparar(String cad){
        String aux;
        if(cad.equals(res[0])) aux = "1";
        else if (cad.equals(res[1])) aux = "2";
        else if (cad.equals(res[2])) aux = "3";
        else if (cad.equals(res[3])) aux = "4";
        else aux ="1";
        return aux;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_reg:
                registrar(v);
                break;
        }
    }
}
