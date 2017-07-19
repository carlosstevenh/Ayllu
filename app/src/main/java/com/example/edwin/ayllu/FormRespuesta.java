package com.example.edwin.ayllu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FormRespuesta extends AppCompatActivity {

    /**
     * =============================================================================================
     * METODO: Establece todas las acciones a realizar una vez creado el Activity
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_respuesta);

        //Inflamos el layout para el Fragmento MonitoringListFragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.institutional_form_principal_context, new InstitutionalEvaluationFormFragment())
                .commit();
    }
}
