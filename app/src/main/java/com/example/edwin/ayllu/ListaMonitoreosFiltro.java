package com.example.edwin.ayllu;


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

        Bundle bundle = getActivity().getIntent().getExtras();

        RestClient service = RestClient.retrofit.create(RestClient.class);

        Toast.makeText(
                getActivity(),
                ""+bundle.getString("p1")+"__"+bundle.getString("p2")+"__"+bundle.getString("p3")+"__"+
                        bundle.getString("p4")+"__"+bundle.getString("p5")+"__"+bundle.getString("p6")+"__"+bundle.getString("fi")+"__"+bundle.getString("ff")+"__"+
                        bundle.getString("fac")+"__"+bundle.getString("var"),
                Toast.LENGTH_LONG)
                .show();

        Call<ArrayList<PuntoCritico>> pc = service.getFiltro(bundle.getString("p1"),bundle.getString("p2"),bundle.getString("p3"),
                bundle.getString("p4"),bundle.getString("p5"),bundle.getString("p6"),bundle.getString("fi"),bundle.getString("ff"),
                bundle.getString("fac"),bundle.getString("var"));
        pc.enqueue(new Callback<ArrayList<PuntoCritico>>() {
            @Override
            public void onResponse(Call<ArrayList<PuntoCritico>> call, Response<ArrayList<PuntoCritico>> response) {
                if (response.isSuccessful()) {
                    monitoreos = response.body();

                    Toast.makeText(
                            getActivity(),
                            "Fecha => "+monitoreos.size(),
                            Toast.LENGTH_SHORT)
                            .show();
                    //final RecyclerView rv = (RecyclerView) View.findViewById(R.id.reciclerPuntosCriticos);
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
                            //intent.putExtra("pa",m.getCodigo());
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
                            "por el no => "+monitoreos.size(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PuntoCritico>> call, Throwable t) {
                Toast.makeText(
                        getActivity(),
                        "nada de nada => "+monitoreos.size(),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        return root;
    }

}
