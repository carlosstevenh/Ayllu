package com.example.edwin.ayllu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.edwin.ayllu.ui.ReportFragment;

public class MonitorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.monitor_principal_context, new ReportFragment())
                .commit();
    }
}
