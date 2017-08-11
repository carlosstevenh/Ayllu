package com.example.edwin.ayllu.ui;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.edwin.ayllu.AdminSQLite;
import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.domain.Usuario;
import com.example.edwin.ayllu.ui.adapter.UsuariosAdapter;

import java.util.ArrayList;

public class AdminListEnabledFragment extends Fragment implements View.OnClickListener {

    //Variables globales para el listado de usuarios
    private RecyclerView recycler;
    private UsuariosAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<Usuario> users;
    private Usuario user;
    private ImageButton ibNew;
    AdminSQLite admin;
    SQLiteDatabase bd;

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_list_enabled, container, false);

        //Obentemos el Recycler y el RefreshLayout
        recycler = (RecyclerView) view.findViewById(R.id.rv_users);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_container);

        // Seteamos los colores que se usarán a lo largo de la animación
        refreshLayout.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new UsuariosAdapter(getActivity(), cargarLista());
        recycler.setAdapter(adapter);

        // Iniciar la tarea asíncrona al revelar el indicador
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new HackingBackgroundTask().execute();
                    }
                }
        );

        ibNew = (ImageButton) view.findViewById(R.id.ib_new);
        ibNew.setOnClickListener(this);

        return view;
    }

    /**
     * =============================================================================================
     * METODO: CARGAR LA LISTA DE USUARIOS
     */
    public ArrayList<Usuario> cargarLista(){

        //se realiza la consulta a la base de datos del movil de los monitores
        admin = new AdminSQLite(getActivity(), "login", null, 1);
        bd = admin.getWritableDatabase();
        Cursor datos = bd.rawQuery(
                "select * from "+ admin.TABLENAME + " where "+ admin.TIP_USU + "='M'", null);

        users = new ArrayList<>(datos.getCount());

        if (datos.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                user = new Usuario();

                user.setCodigo_usu(datos.getInt(1));
                user.setIdentificacion_usu(datos.getString(2));
                user.setNombre_usu(datos.getString(3));
                user.setApellido_usu(datos.getString(4));
                user.setTipo_usu(datos.getString(5));
                user.setContrasena_usu(datos.getString(6));
                user.setClave_api(datos.getString(7));
                user.setPais_usu(datos.getString(8));

                users.add(user);

            } while(datos.moveToNext());
        }

        bd.close();
        return users;
    }

    /**
     * =============================================================================================
     * METODO:
     */
    private class HackingBackgroundTask extends AsyncTask<Void, Void, ArrayList<Usuario>> {

        static final int DURACION = 3 * 1000; // 3 segundos de carga

        @Override
        protected ArrayList<Usuario> doInBackground(Void... params) {
            // Simulación de la carga de items
            try {
                Thread.sleep(DURACION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Retornar en nuevos elementos para el adaptador
            return cargarLista();
        }

        @Override
        protected void onPostExecute(ArrayList<Usuario> result) {
            super.onPostExecute(result);

            // Limpiar elementos antiguos
            adapter.clear();

            // Añadir elementos nuevos
            adapter.addAll(result);

            // Parar la animación del indicador
            refreshLayout.setRefreshing(false);
        }

    }

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_new:
                Intent intent = new Intent(getActivity(), AdminUserTransactionActivity.class);
                intent.putExtra("TYPE","NEW");
                startActivity(intent);
                break;
        }
    }
}
