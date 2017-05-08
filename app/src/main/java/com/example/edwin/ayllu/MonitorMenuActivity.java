package com.example.edwin.ayllu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MonitorMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private int[] layouts;
    private Button btnSkip, btnMenu;

    String monitor = "", pais = "", tipo_usu = "A";

    /**
     * =============================================================================================
     * METODO: Establece todas las acciones a realizar una vez creado el Activity
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtiene el codigo y pais del monitor que ha ingresado
        AdminSQLite admin = new AdminSQLite(getApplicationContext(), "login", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        //Prepara la sentencia SQL para la consulta en la Tabla de usuarios
        Cursor cursor = bd.rawQuery("SELECT codigo, pais FROM login LIMIT 1", null);
        cursor.moveToFirst();
        monitor = cursor.getString(0);
        pais = cursor.getString(1);

        //Aplicando transparencia sobre el Toolbar
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_monitor_menu);

        //Referenciando los views en la interfaz
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnMenu = (Button) findViewById(R.id.btn_menu);


        //Creando un vector de IDs de los layouts para el menu del monitor
        layouts = new int[]{
                R.layout.slide_register_monitoring,
                R.layout.slide_update_monitoring,
                R.layout.slide_statistical_maps,
                R.layout.slide_visualize_qhapaq_nan};

        //Añadiendo los bottom dots
        addBottomDots(0);

        //Llamando al metodo para aplicar transparencia sobre el toolbar
        changeStatusBarColor();

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
    }

    /**
     * =============================================================================================
     * METODO: Añade los puntos al LinearLayout encargado de mostrar información de el Slide Actual
     **/
    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];

        //Limpiamos y recargamos todos lo views para los puntos
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            //Creamos un nuevo view para el punto con el caracter (.)
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorPrimary));
            dotsLayout.addView(dots[i]);
        }
        //Determinamos que punto esta activo segun el layout actual
        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.colorTextIcons));
    }

    /**
     * =============================================================================================
     * METODO: Obtenemos el Slide actual
     **/
    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    /**
     * =============================================================================================
     * METODO: Carga el Activity encargado de las estadisticas
     **/
    private void launchEstadisticas() {
        //Intent intent = new Intent(MonitorMenuActivity.this, FiltrarActividad.class);
        Intent intent = new Intent(MonitorMenuActivity.this, MenuGraficasEstadisticas.class);
        intent.putExtra("Monitor", monitor);
        startActivity(intent);
    }

    /**
     * =============================================================================================
     * METODO: Carga el Activity encargado de la respuesta institucional
     **/
    private void launchRespuestaInstitucional() {
        Intent intent = new Intent(MonitorMenuActivity.this, InstitutionalActivity.class);
        intent.putExtra("MONITOR", monitor);
        intent.putExtra("PAIS", pais);
        startActivity(intent);
        finish();
    }

    /**
     * =============================================================================================
     * METODO: Carga el Activity encargado del Monitoreo
     **/
    private void launchRegistroMonitoreo() {
        Intent intent = new Intent(MonitorMenuActivity.this, MonitoringActivity.class);
        intent.putExtra("MONITOR", monitor);
        intent.putExtra("PAIS", pais);
        startActivity(intent);
    }

    /**
     * =============================================================================================
     * METODO: Carga un mensaje en caso de seleccionar el boton de retroceso
     **/
    @Override
    public void onBackPressed() {
        createSimpleDialog("¿Quieres Salir?",
                getResources().getString(R.string.titleDetailMonitoringDialog)).show();
    }

    /**
     * =============================================================================================
     * METODO: Agrega el escucha al view encargado de los Slides con sus respectivos metodos
     **/
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    /**
     * =============================================================================================
     * METODO: Aplica transparencia sobre el Toolbar de la Activity
     **/
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * =============================================================================================
     * METODO: Decteca el evento onClick sobre los botones de la Interfaz
     **/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Lanza un menu en el caso de que quiera cerrar sesión o si es Administrador (regresar
            // a la interfaz administrativa).
            case R.id.btn_menu:
                //Cerrar Sesión
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();

                AdminSQLite admin = new AdminSQLite(getApplicationContext(), "login", null, 1);
                SQLiteDatabase bd = admin.getWritableDatabase();
                bd.delete(admin.TABLENAME, null, null);
                bd.close();
                break;
            //Lanza una activity en el caso en el que el usuario haya elegido un Slide
            case R.id.btn_skip:
                int current = getItem(0);
                if (current == 0) launchRegistroMonitoreo();
                else if (current == 1) launchRespuestaInstitucional();
                else if (current == 2) launchEstadisticas();
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Funcionalidad en Construccion", Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * =============================================================================================
     * METODO: Encargado de adaptar el Slide al view del Activity
     **/
    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
    /**
     * =============================================================================================
     * METODO: Genera un Dialogo basico en pantalla
     **/
    public AlertDialog createSimpleDialog(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_monitoring_info, null);
        builder.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title_dialog);
        TextView tvDescription = (TextView) v.findViewById(R.id.tv_description_dialog);

        tvTitle.setText(titulo);
        tvTitle.setCompoundDrawables(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_question), null, null, null);
        tvDescription.setText(mensaje);

        builder.setPositiveButton("ACEPTAR",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getIntent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
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
