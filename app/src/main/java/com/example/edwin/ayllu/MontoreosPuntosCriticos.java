package com.example.edwin.ayllu;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.edwin.ayllu.Adiminstrador.AddMonitorFragment;
import com.example.edwin.ayllu.Adiminstrador.Administrador;
import com.example.edwin.ayllu.Adiminstrador.DeleteMonitorFragment;
import com.example.edwin.ayllu.Adiminstrador.EditMonitorFragment;
import com.example.edwin.ayllu.domain.Monitoreo;
import com.example.edwin.ayllu.domain.PuntoCritico;
import com.example.edwin.ayllu.ui.ReportFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
