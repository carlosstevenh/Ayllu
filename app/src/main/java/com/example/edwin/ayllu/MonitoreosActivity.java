package com.example.edwin.ayllu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.edwin.ayllu.domain.Monitoreo;
import com.example.edwin.ayllu.domain.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonitoreosActivity extends AppCompatActivity {

    private ArrayList<Monitoreo> mon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoreos);

        Bundle bundle = getIntent().getExtras();
        String area = bundle.getString("AREA");
        Log.i("TAG", "area: " + area);

        RestClient service = RestClient.retrofit.create(RestClient.class);
        Call<ArrayList<Monitoreo>> requestUser = service.monitoreos(area);
        requestUser.enqueue(new Callback<ArrayList<Monitoreo>>() {
            @Override
            public void onResponse(Call<ArrayList<Monitoreo>> call, Response<ArrayList<Monitoreo>> response) {
                if (response.isSuccessful()) {
                    mon = response.body();
                    Log.i("DemoRecView", "Numero monitoreos  " + mon.size());

                    final RecyclerView rv = (RecyclerView) findViewById(R.id.recicler);
                    MonitoreosAdaprter ma = new MonitoreosAdaprter(mon);
                    ma.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Monitoreo m = mon.get(rv.getChildAdapterPosition(v));

                            Bundle parametro = new Bundle();
                            parametro.putString("pa", m.getCodigo());
                            parametro.putString("fm", m.getDate());

                            Intent intent = new Intent(MonitoreosActivity.this, FormRespuesta.class);
                            intent.putExtras(parametro);
                            //intent.putExtra("pa",m.getCodigo());
                            startActivity(intent);
                            Log.i("DemoRecView", "Pulsado el elemento " + m.getDate());
                        }
                    });

                    LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                    rv.setLayoutManager(llm);
                    rv.setAdapter(ma);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<Monitoreo>> call, Throwable t) {

            }
        });


    }
}
