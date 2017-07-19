package com.example.edwin.ayllu;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.PuntoCritico;
import com.example.edwin.ayllu.io.RestClient;
import com.example.edwin.ayllu.ui.adapter.PuntoCriticoAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListaMonitoreosFiltro extends Fragment {

    private RecyclerView mReporteList;
    private ArrayList<PuntoCritico> monitoreos = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lista_monitoreos_filtro, container, false);
        mReporteList = (RecyclerView) root.findViewById(R.id.rvpc);

        //parametros que se obtienene cuando se llama a esta clase
        Bundle bundle = getActivity().getIntent().getExtras();

        //peticion al servidor para obtener los ultimos monitoreos de cada punto de afactacion
        final ProgressDialog loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.institutional_list_process_message_search),getResources().getString(R.string.institutional_list_process_message),false,false);

        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<PuntoCritico>> pc = service.getFiltro(bundle.getString("p1"),bundle.getString("p2"),bundle.getString("p3"),
                bundle.getString("p4"),bundle.getString("p5"),bundle.getString("p6"),bundle.getString("fi"),bundle.getString("ff"),
                bundle.getString("fac"),bundle.getString("var"));
        pc.enqueue(new Callback<ArrayList<PuntoCritico>>() {
            @Override
            public void onResponse(Call<ArrayList<PuntoCritico>> call, Response<ArrayList<PuntoCritico>> response) {
                loading.dismiss();
                if (response.isSuccessful()) {
                    monitoreos = response.body();

                    //invocacion al metodo setOnclickListener cuando el usuario clickea un elementod de la lista
                    PuntoCriticoAdapter ma = new PuntoCriticoAdapter(monitoreos);
                    ma.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PuntoCritico m = monitoreos.get(mReporteList.getChildAdapterPosition(view));
                            Bundle parametro = new Bundle();
                            parametro.putString("pa", m.getCodigo_paf());
                            parametro.putString("fm", m.getFecha());

                            Intent intent = new Intent(getActivity(), InformacionPuntoCritico.class);
                            intent.putExtras(parametro);
                            startActivity(intent);
                            Log.i("DemoRecView", "Pulsado el elemento " + m.getCodigo_paf());
                        }
                    });
                    LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                    mReporteList.setLayoutManager(llm);
                    mReporteList.setAdapter(ma);
                }
                else{
                    Toast.makeText(
                            getActivity(),
                            getResources().getString(R.string.institutional_response_form_process_message_negative),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PuntoCritico>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(
                        getActivity(),
                        getResources().getString(R.string.institutional_list_process_message_server),
                        Toast.LENGTH_SHORT)
                        .show();
                getActivity().finish();

            }
        });

        return root;
    }

}
