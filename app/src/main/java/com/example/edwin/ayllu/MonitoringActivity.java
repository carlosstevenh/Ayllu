package com.example.edwin.ayllu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.edwin.ayllu.ui.MonitoringListFragment;

public class MonitoringActivity extends AppCompatActivity {
    /**
     * =============================================================================================
     * METODO: Establece todas las acciones a realizar una vez creado el Activity
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        //Inflamos el layout para el Fragmento MonitoringListFragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.monitoring_principal_context, new MonitoringListFragment())
                .commit();
    }

    /**
     * =============================================================================================
     * METODO: Carga el Activity MonitorMenuActivity si se produce el evento de retroceso.
     **/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
