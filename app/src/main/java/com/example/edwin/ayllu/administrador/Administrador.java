package com.example.edwin.ayllu.administrador;

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
import com.example.edwin.ayllu.MainActivity;
import com.example.edwin.ayllu.MonitorMenuActivity;
import com.example.edwin.ayllu.R;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class Administrador extends AppCompatActivity implements AddMonitorFragment.OnFragmentInteractionListener,
        EditMonitorFragment.OnFragmentInteractionListener, DeleteMonitorFragment.OnFragmentInteractionListener {
    private Toolbar toolbar;//Declaramos el Toolbar
    private FrameLayout fl;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    //private static final String TAG = "ERRORES";
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
        //adapter.addFragment(new DeleteMonitorFragment(), getResources().getString(R.string.inicio));
        adapter.addFragment(new AddMonitorFragment(), getResources().getString(R.string.addMonitor));
        adapter.addFragment(new EditMonitorFragment(), getResources().getString(R.string.monitores));
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
                Intent intent = new Intent(Administrador.this, MonitorMenuActivity.class);
                startActivity(intent);
                finish();
                return true;

            //opcion de cerrar sesion
            case R.id.salir:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();

                AdminSQLite admin = new AdminSQLite(getApplicationContext(), "login", null, 1);
                SQLiteDatabase bd = admin.getWritableDatabase();
                bd.delete(admin.TABLENAME, null, null);
                bd.close();
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
}
