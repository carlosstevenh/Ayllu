package com.example.edwin.ayllu.Adiminstrador;

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
EditMonitorFragment.OnFragmentInteractionListener, DeleteMonitorFragment.OnFragmentInteractionListener{
    private Toolbar toolbar;//Declaramos el Toolbar
    private FrameLayout fl;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //private static final String TAG = "ERRORES";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AddMonitorFragment(), "Registrar monitor");
        adapter.addFragment(new EditMonitorFragment(), "Editar/eliminar Monitor");
        adapter.addFragment(new DeleteMonitorFragment(), "Editar información");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_administrador_,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.admin) {
            fragmentAdministrador();
            return true;
        }*/
        switch (id) {
            case R.id.moni:
                Intent intent = new Intent(Administrador.this, MonitorMenuActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.salir:
                Intent i	=	new	Intent(this, MainActivity.class);
                startActivity(i);
                AdminSQLite admin = new AdminSQLite(getApplicationContext(),"login", null, 1);
                SQLiteDatabase bd = admin.getWritableDatabase();
                bd.delete(admin.TABLENAME,null,null);
                //bd.execSQL("TRUNCATE TABLE "+admin.TABLENAME, null);
                bd.close();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    /*
    private void fragmentAdministrador(){
        //if(mf!=null) mf.onDestroyView();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        //ft.add(R.id.layoutFragment, new AdministradorFragment(),
          //      "administrador");
        af = new AdministradorFragment();
        ft.add(R.id.layoutFragment,af);

        ft.commit();
        //af.onStop();


    }

    private void fragmentMonitor(){
        //f(af!=null)Log.i("TAG", "nnn "+af.isRemoving());
        //af.i
        //Log.i("TAG", "tag:  "+af.getTag());
        //Log.i("TAG", "id: "+af.getId());
        //if(!af.isRemoving()) af.onStop();
        fl.destroyDrawingCache();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        //ft.add(R.id.layoutFragment, new MonitorFragment(),
        //        "monitor");
        mf = new MonitorFragment();
        ft.add(R.id.layoutFragment,mf);

        ft.commit();
    }*/

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
