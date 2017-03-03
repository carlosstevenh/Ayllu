package com.example.edwin.ayllu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MontoreosPuntosCriticos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        setContentView(R.layout.activity_montoreos_puntos_criticos);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.monitor_principal_context, new ListaMonitoreosFiltro())
                .commit();
    }
}
