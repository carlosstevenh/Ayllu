package com.qhapaq.nan.ayllu.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qhapaq.nan.ayllu.R;
import com.qhapaq.nan.ayllu.domain.usuario.Usuario;
import com.qhapaq.nan.ayllu.domain.usuario.UsuarioDbHelper;
import com.qhapaq.nan.ayllu.io.AylluApiAdapter;
import com.qhapaq.nan.ayllu.io.model.UsuarioResponse;
import com.qhapaq.nan.ayllu.ui.adapter.UsuariosAdapter;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminListUsersFragment extends Fragment implements View.OnClickListener {
    //Definimos las variables globales
    private UsuariosAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<Usuario> users = new ArrayList<>();
    private UsuarioDbHelper usuarioDbHelper;
    private RecyclerView recycler;
    private TextView tvTitle;
    String estado;

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usuarioDbHelper = new UsuarioDbHelper(getActivity());
        estado = getArguments() != null ? getArguments().getString("ESTADO") : "E";
        adapter = new UsuariosAdapter(getActivity(), estado);
    }

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_list_users, container, false);

        //Obentemos el Recycler y el RefreshLayout
        recycler = view.findViewById(R.id.rv_users);
        refreshLayout = view.findViewById(R.id.srl_container);

        // Seteamos los colores que se usarán a lo largo de la animación
        refreshLayout.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );

        // Usar un administrador para LinearLayout
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(lManager);

        // Iniciar la tarea asíncrona al revelar el indicador
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        cargarLista();
                    }
                }
        );

        ImageButton ibNew = view.findViewById(R.id.ib_new);
        tvTitle = view.findViewById(R.id.tv_title);

        if (estado.equals("D")){
            ibNew.setScaleX(0);
            ibNew.setScaleY(0);
            ibNew.setEnabled(false);

            tvTitle.setText(getResources().getString(R.string.list_admin_monitors_title_disabled));
        }
        else {
            ibNew.setOnClickListener(this);
            tvTitle.setText(getResources().getString(R.string.list_admin_monitors_title_enabled));
        }

        recycler.setAdapter(adapter);
        return view;
    }

    /**
     * =============================================================================================
     * METODO: CARGAR LA LISTA DE USUARIOS
     */
    public void cargarLista(){
        String pais;

        //Obtenemos el pais del administrador actual
        Cursor cursor = usuarioDbHelper.generateQuery("SELECT * FROM ");
        if (cursor.moveToFirst() && !estado.equals("E")){
            pais = "0" + cursor.getString(7);

            Call<UsuarioResponse> callUser = AylluApiAdapter.getApiService("USUARIOS").getUsuarios(pais, estado);
            callUser.enqueue(new Callback<UsuarioResponse>() {
                @Override
                public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {
                    if (response.isSuccessful()){
                        users = response.body().getUsuarios();
                    }
                    new HackingBackgroundTask().execute();
                }

                @Override
                public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                    new HackingBackgroundTask().execute();
                }
            });
        }
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
            return users;
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
