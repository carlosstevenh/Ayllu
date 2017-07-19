package com.example.edwin.ayllu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class MenuGraficasEstadisticas extends AppCompatActivity {
    CardView mg,me;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_graficas_estadisticas);
        mg = (CardView) findViewById(R.id.cardMG);
        me = (CardView) findViewById(R.id.cardME);

    }

    public void graficasGenerales(View v){
        Intent intent = new Intent(MenuGraficasEstadisticas.this, ActivitySeleccionTramoFiltro.class);
        startActivity(intent);
    }

    public void graficasEspecificas(View v){
        Intent intent = new Intent(MenuGraficasEstadisticas.this, FilterMonitoringActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(MenuGraficasEstadisticas.this,MonitorMenuActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }
}
