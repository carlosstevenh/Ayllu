package com.example.edwin.ayllu.Adiminstrador;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.edwin.ayllu.AdminSQLite;
import com.example.edwin.ayllu.Domain.Usuario;
import com.example.edwin.ayllu.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditMonitorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EditMonitorFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<Usuario> listaUsuarios;
    private Usuario user;
    private ListView usuarios;
    private Activity activity;
    private Boolean ban= false;
    private ArrayAdapter<String> items;
    private SwipeRefreshLayout swipeContainer;
    private View view;

    public EditMonitorFragment() {
        //getUsuarios("01");
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_monitor, container, false);
        activity = getActivity();
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srlContainer);



        UsuariosAdapter ua = cargarLista();

        /*items = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                nombres);*/
        usuarios = (ListView)view.findViewById(R.id.listUser);
        usuarios.setFastScrollEnabled(true);
        usuarios.setAdapter(ua);

        usuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle parametro = new Bundle();
                Usuario aux = (Usuario) usuarios.getItemAtPosition(i);
                parametro.putInt("codigo", aux.getCodigo_usu());
                parametro.putString("iden", aux.getIdentificacion_usu());
                parametro.putString("nombre", aux.getNombre_usu());
                parametro.putString("apellido", aux.getApellido_usu());
                parametro.putString("tipo", aux.getTipo_usu());
                parametro.putString("con", aux.getContrasena_usu());
                parametro.putString("cla" , aux.getClave_api());
                parametro.putString("pais", aux.getPais_usu());

                Intent intent = new Intent(getActivity(),EditMonitor.class);
                intent.putExtras(parametro);
                startActivity(intent);

                /*Toast.makeText(getActivity(),
                        "Iniciar screen de detalle para: \n" + usuarios.getItemAtPosition(i).toString(),
                        Toast.LENGTH_SHORT).show();*/
            }
        });
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UsuariosAdapter ua = cargarLista();
                        usuarios = (ListView)view.findViewById(R.id.listUser);
                        usuarios.setFastScrollEnabled(true);
                        usuarios.setAdapter(ua);
                        swipeContainer.setRefreshing(false);
                    }
                }, 3000);

            }
        });
        return view;
    }
    public UsuariosAdapter cargarLista(){
        AdminSQLite admin = new AdminSQLite(activity,"login", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor datos = bd.rawQuery(
                "select * from "+ admin.TABLENAME + " where "+ admin.TIP_USU + "='M'", null);

        Log.i("TAG", "registro=> " + datos.getCount());
        String[] nombres = new String[datos.getCount()];
        listaUsuarios = new ArrayList<Usuario>(datos.getCount());
        int i = 0;
        String pais = "";
        if (datos.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                user = new Usuario();
                Log.i("TAG", "cpdigo::> " + datos.getInt(1));
                user.setCodigo_usu(datos.getInt(1));
                user.setIdentificacion_usu(datos.getString(2));
                user.setNombre_usu(datos.getString(3));
                user.setApellido_usu(datos.getString(4));
                user.setTipo_usu(datos.getString(5));
                user.setContrasena_usu(datos.getString(6));
                user.setClave_api(datos.getString(7));
                user.setPais_usu(datos.getString(8));

                listaUsuarios.add(user);

                String nombre = datos.getString(0)+" "+datos.getString(1);
                nombres[i] = nombre;
                i++;
                pais = datos.getString(2);

            } while(datos.moveToNext());
        }

        bd.close();
        UsuariosAdapter ua = new UsuariosAdapter(getActivity(),listaUsuarios);
        return ua;
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStop() {
        Log.i("TAG", "Esta en stop> " );
        super.onStop();

    }

    @Override
    public void onPause() {
        Log.i("TAG", "Esta en pause> " );
        super.onPause();
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

}
