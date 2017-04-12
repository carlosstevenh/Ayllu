package com.example.edwin.ayllu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PrestentationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestentation);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.presentation_principal_context, new MonitoringSummaryFragment())
                .commit();
    }
}
