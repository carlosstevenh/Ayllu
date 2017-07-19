package com.example.edwin.ayllu.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.edwin.ayllu.io.RestClient;
import com.example.edwin.ayllu.domain.Usuario;
import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.ui.adapter.UsuariosAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        //opciones del submenu
        switch (item.getItemId()) {
            //opcion editar usuario
            case R.id.edit:
                //parametro que se envia con la informacion del monitor
                Bundle parametro = new Bundle();
                parametro.putInt("codigo", aux.getCodigo_usu());
                parametro.putString("iden", aux.getIdentificacion_usu());
                parametro.putString("nombre", aux.getNombre_usu());
                parametro.putString("apellido", aux.getApellido_usu());
                parametro.putString("tipo", aux.getTipo_usu());
                parametro.putString("con", aux.getContrasena_usu());
                parametro.putString("cla" , aux.getClave_api());
                parametro.putString("pais", aux.getPais_usu());

                //se llama a la actividad de editar el monitor
                Intent intent = new Intent(getActivity(),EditMonitorActivity.class);
                intent.putExtras(parametro);
                startActivity(intent);
                return true;
            //opcion que se encarga d deshabilitar un monitor
            case R.id.desHabilitar:
                //se crea el dialogo de confirmacion
                createSimpleDialog(getResources().getString(R.string.process_message_question),getResources().getString(R.string.process_message_title_alert),aux.getIdentificacion_usu()).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    //se crea el submenu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)menuInfo;

        Usuario aux = (Usuario) usuarios.getAdapter().getItem(info.position);

        menu.setHeaderTitle(
                "MONITOR"+": " +aux.getNombre_usu() + " "+aux.getApellido_usu());
        inflater.inflate(R.menu.menu_edit_deshabilitar_monitor, menu);
    }

    //se crea la vista del fragmento
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

        //metodo encargado de recargar la informacion de la lista
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

    //metodo encargado de reacargar la lista de los monitores
    public UsuariosAdapter cargarLista(){

        //se realiza la consulta a la base de datos del movil de los monitores
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
    //metodo encargado de crear los dialogos informativos dependiendo de lo que suceda durante la ejecucion de la actividad
    public AlertDialog createSimpleDialog(String mensaje, String titulo,String usuario) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String dh = usuario;
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("CONFIRMAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //se realiza la peticion al servidor para deshabilitar a un monitor
                                final ProgressDialog loading = ProgressDialog.show(getContext(),getResources().getString(R.string.process_message_disable_monitor),getResources().getString(R.string.process_message),false,false);
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
                                            //se eliminar el monitor de la base de datos del movil
                                            AdminSQLite admin1 = new AdminSQLite( getContext(), "login", null, 1);
                                            SQLiteDatabase bd1 = admin1.getWritableDatabase();
                                            bd1.delete(admin1.TABLENAME, admin1.IDE_USU +"='"+ dh +"'", null);
                                            bd1.close();
                                            Intent intent = new Intent(getContext(),AdministratorActivity.class);
                                            startActivity(intent);
                                            Toast login = Toast.makeText(getContext(),
                                                    getResources().getString(R.string.process_successful_message), Toast.LENGTH_LONG);
                                        }
                                        else{
                                            Toast login = Toast.makeText(getContext(),
                                                    getResources().getString(R.string.process_error_message), Toast.LENGTH_LONG);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                                        Toast login = Toast.makeText(getContext(),
                                                getResources().getString(R.string.process_message_server), Toast.LENGTH_LONG);
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
