package com.example.edwin.ayllu.Adiminstrador;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.edwin.ayllu.AdminSQLite;
import com.example.edwin.ayllu.MonitoringRegistrationForm1Activity;
import com.example.edwin.ayllu.MonitoringRegistrationForm2Activity;
import com.example.edwin.ayllu.RestClient;
import com.example.edwin.ayllu.domain.Usuario;
import com.example.edwin.ayllu.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditMonitorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EditMonitorFragment extends Fragment  {

    private OnFragmentInteractionListener mListener;
    private ArrayList<Usuario> listaUsuarios;
    private Usuario user;
    private ListView usuarios;
    private Activity activity;
    private Boolean ban= false;
    private ArrayAdapter<String> items;
    private SwipeRefreshLayout swipeContainer;
    private View view;
    private ArrayList<String> res;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Usuario aux = (Usuario) usuarios.getAdapter().getItem(info.position);

        switch (item.getItemId()) {
            case R.id.edit:

                Bundle parametro = new Bundle();
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
                return true;
            case R.id.desHabilitar:
                createSimpleDialog("Esta seguro que desea deshabilitar el monitor?","Advertencia!!",aux.getIdentificacion_usu()).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)menuInfo;

        Usuario aux = (Usuario) usuarios.getAdapter().getItem(info.position);

        menu.setHeaderTitle(
                "Monitor: "+aux.getNombre_usu() + " "+aux.getApellido_usu());
        inflater.inflate(R.menu.menu_edit_deshabilitar_monitor, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_monitor, container, false);
        activity = getActivity();
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srlContainer);



        UsuariosAdapter ua = cargarLista();

        usuarios = (ListView)view.findViewById(R.id.listUser);
        usuarios.setFastScrollEnabled(true);
        usuarios.setAdapter(ua);

        registerForContextMenu(usuarios);

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

                Intent intent = new Intent(getActivity(),InformacionGeneralMonitor.class);
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
    public AlertDialog createSimpleDialog(String mensaje, String titulo,String usuario) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String dh = usuario;
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog loading = ProgressDialog.show(getContext(),"Deshabilitando monitor","Por favor espere...",false,false);
                                RestClient service = RestClient.retrofit.create(RestClient.class);
                                Call<ArrayList<String>> requestDelete = service.deleteUsuario(dh);
                                requestDelete.enqueue(new Callback<ArrayList<String>>() {
                                    @Override
                                    public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                                        loading.dismiss();
                                        res = response.body();
                                        //String ide = aux.getIdentificacion_usu();
                                        String aux1 = res.get(0);
                                        if(aux1.equals("1")){
                                            AdminSQLite admin1 = new AdminSQLite( getContext(), "login", null, 1);
                                            SQLiteDatabase bd1 = admin1.getWritableDatabase();
                                            bd1.delete(admin1.TABLENAME, admin1.IDE_USU +"='"+ dh +"'", null);
                                            bd1.close();
                                            Intent intent = new Intent(getContext(),Administrador.class);
                                            startActivity(intent);
                                            Toast login = Toast.makeText(getContext(),
                                                    "Acción completada", Toast.LENGTH_LONG);
                                        }
                                        else{
                                            Toast login = Toast.makeText(getContext(),
                                                    "No se pudo Deshabilitar", Toast.LENGTH_LONG);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                                        Toast login = Toast.makeText(getContext(),
                                                "No se pudo Deshabilitar", Toast.LENGTH_LONG);
                                        loading.dismiss();
                                    }
                                });
                                builder.create().dismiss();
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
}
