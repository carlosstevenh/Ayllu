package com.example.edwin.ayllu.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity implements
        DeleteMonitorFragment.OnFragmentInteractionListener {
    private BottomNavigationView bnvAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        if (savedInstanceState == null){
            loadListEnabledFragment();
        }

    }

    /**
     * =============================================================================================
     * METODO: INICIA EL FRAGMENTO DE MONITORES HABILITADOS
     **/

    private void loadListEnabledFragment (){
        AdminListEnabledFragment fragmentEnabled = new AdminListEnabledFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_fragment, fragmentEnabled);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_administrador, menu);
        return true;
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
