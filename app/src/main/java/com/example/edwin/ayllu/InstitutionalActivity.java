package com.example.edwin.ayllu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.edwin.ayllu.ui.InstitutionalListFragment;

public class InstitutionalActivity extends AppCompatActivity{
    String cod_mon = "", pais_mon = "";

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Intent intent = new Intent(InstitutionalActivity.this, MonitorMenuActivity.class);
        //startActivity(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institutional);

        Intent intent = getIntent();
        cod_mon = intent.getStringExtra("MONITOR");
        pais_mon = intent.getStringExtra("PAIS");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.institutional_principal_context, new InstitutionalListFragment())
                .commit();
    }
}
