package com.example.edwin.ayllu.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.edwin.ayllu.R;

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(MontoreosPuntosCriticos.this,FilterMonitoringActivity.class);
        startActivity(intent);
        finish();
    }
}
