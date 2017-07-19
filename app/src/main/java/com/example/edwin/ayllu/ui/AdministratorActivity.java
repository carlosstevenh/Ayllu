package com.example.edwin.ayllu.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.example.edwin.ayllu.AdminSQLite;
import com.example.edwin.ayllu.LoginActivity;
import com.example.edwin.ayllu.MonitorMenuActivity;
import com.example.edwin.ayllu.R;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class AdministratorActivity extends AppCompatActivity implements RegisterMonitorFragment.OnFragmentInteractionListener,
        EditMonitorFragment.OnFragmentInteractionListener, DeleteMonitorFragment.OnFragmentInteractionListener {
    private Toolbar toolbar;//Declaramos el Toolbar
    private FrameLayout fl;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        //se intancian los elementos de la vista
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    //metodo que se enscarga de crear las opciones del menu
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RegisterMonitorFragment(), getResources().getString(R.string.item_navbar_monitor_recoder));
        adapter.addFragment(new EditMonitorFragment(), getResources().getString(R.string.item_navbar_monitor_list));
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_administrador_, menu);
        return true;
    }

    //medoto encargada de crear el submenu de la actividad
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            //si eligio la opcion e monitorear lo dirige a la actividad de monitorear
            case R.id.moni:
                Intent intent = new Intent(AdministratorActivity.this, MonitorMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

            //opcion de cerrar sesion
            case R.id.salir:
                createSimpleDialogSalir(getResources().getString(R.string.cerrarSesion),getResources().getString(R.string.title_warning)).show();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //metodo encargado de crear los dialogos informativos
    public AlertDialog createSimpleDialogSalir(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("CONFIRMAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                AdminSQLite admin = new AdminSQLite(getApplicationContext(), "login", null, 1);
                                SQLiteDatabase bd = admin.getWritableDatabase();
                                bd.delete(admin.TABLENAME, null, null);
                                bd.close();

                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                                finish();
                                builder.create().dismiss();
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        });

        return builder.create();
    }
}
