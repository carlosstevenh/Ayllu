package com.example.edwin.ayllu.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.support.v4.app.FragmentManager;
import com.example.edwin.ayllu.AdminSQLite;
import com.example.edwin.ayllu.LoginActivity;
import com.example.edwin.ayllu.R;
import android.content.Intent;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private BottomNavigationView bnvAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        if (savedInstanceState == null){
            loadListEnabledFragment();
        }

        bnvAdmin = (BottomNavigationView) findViewById(R.id.bnv_admin);

        bnvAdmin.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;
                Bundle bundle = new Bundle();

                switch (item.getItemId()){
                    case R.id.action_enabled:
                        bundle.putString("ESTADO","H");
                        fragment = new AdminListUsersFragment();
                        fragment.setArguments(bundle);
                        break;
                    case R.id.action_disabled:
                        bundle.putString("ESTADO","D");
                        fragment = new AdminListUsersFragment();
                        fragment.setArguments(bundle);
                        break;
                }

                replaceFragment(fragment);
                return true;
            }
        });

    }

    /**
     * =============================================================================================
     * METODO: INICIA EL FRAGMENTO DE MONITORES HABILITADOS
     **/

    private void loadListEnabledFragment (){
        Bundle bundle = new Bundle();
        bundle.putString("ESTADO","H");

        AdminListUsersFragment fragmentEnabled = new AdminListUsersFragment();
        fragmentEnabled.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_fragment, fragmentEnabled);
        transaction.commit();
    }

    /**
     * =============================================================================================
     * METODO: INICIA EL FRAGMENTO SELECCIONADO
     **/
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_fragment, fragment);
        fragmentTransaction.commit();
    }
}
