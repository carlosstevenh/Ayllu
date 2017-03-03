package com.example.edwin.ayllu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.edwin.ayllu.ui.MonitoringListFragment;
import com.example.edwin.ayllu.ui.ReportFragment;

public class RecordActivity extends AppCompatActivity {
    String cod_mon = "", pais_mon = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Intent intent = getIntent();
        cod_mon = intent.getStringExtra("MONITOR");
        pais_mon = intent.getStringExtra("PAIS");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.record_principal_context, new MonitoringListFragment())
                .commit();
    }
}
