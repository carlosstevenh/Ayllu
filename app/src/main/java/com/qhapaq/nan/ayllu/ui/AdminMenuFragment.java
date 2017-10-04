package com.qhapaq.nan.ayllu.ui;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qhapaq.nan.ayllu.R;
import com.qhapaq.nan.ayllu.domain.monitoreo.MonitoreoDbHelper;
import com.qhapaq.nan.ayllu.domain.pais.PaisDbHelper;
import com.qhapaq.nan.ayllu.domain.seccion.SeccionDbHelper;
import com.qhapaq.nan.ayllu.domain.subtramo.SubtramoDbHelper;
import com.qhapaq.nan.ayllu.domain.tramo.TramoDbHelper;
import com.qhapaq.nan.ayllu.domain.usuario.UsuarioDbHelper;
import com.qhapaq.nan.ayllu.domain.area.AreaDbHelper;

import java.io.File;

public class AdminMenuFragment extends Fragment implements View.OnClickListener{

    //VARIABLES CONTROL DE DATOS FIJOS
    PaisDbHelper paisDbHelper;
    TramoDbHelper tramoDbHelper;
    SubtramoDbHelper subtramoDbHelper;
    SeccionDbHelper seccionDbHelper;
    AreaDbHelper areaDbHelper;
    MonitoreoDbHelper monitoreoDbHelper;
    UsuarioDbHelper usuarioDbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paisDbHelper = new PaisDbHelper(getActivity());
        tramoDbHelper = new TramoDbHelper(getActivity());
        subtramoDbHelper = new SubtramoDbHelper(getActivity());
        seccionDbHelper = new SeccionDbHelper(getActivity());
        areaDbHelper = new AreaDbHelper(getActivity());
        monitoreoDbHelper = new MonitoreoDbHelper(getActivity());
        usuarioDbHelper = new UsuarioDbHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_menu, container, false);

        LinearLayout optionMonitor = (LinearLayout) view.findViewById(R.id.option_monitoring);
        LinearLayout optionExit = (LinearLayout) view.findViewById(R.id.option_exit);

        optionMonitor.setOnClickListener(this);
        optionExit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.option_monitoring:

                Intent intent = new Intent(getActivity().getApplicationContext(), MonitorMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();

                break;
            case R.id.option_exit:
                createSimpleDialogSalir(getResources().getString(R.string.cerrarSesion),getResources().getString(R.string.title_warning)).show();
                break;
        }
    }

    //metodo encargado de crear los dialogos informativos
    public AlertDialog createSimpleDialogSalir(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton(getResources().getString(R.string.settings_app_dialog_option_confirm),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Borramos los datos del usuario logueado
                                usuarioDbHelper.deleteDataBase();
                                usuarioDbHelper.close();

                                //Borramos los datos de funcionamiento
                                paisDbHelper.deleteDataBase();
                                tramoDbHelper.deleteDataBase();
                                subtramoDbHelper.deleteDataBase();
                                seccionDbHelper.deleteDataBase();
                                areaDbHelper.deleteDataBase();

                                paisDbHelper.close();
                                tramoDbHelper.close();
                                subtramoDbHelper.close();
                                seccionDbHelper.close();
                                areaDbHelper.close();

                                deleteCache(getActivity());

                                Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                getActivity().finish();
                                builder.create().dismiss();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.info_dialog_option_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        });

        return builder.create();
    }

    /**
     * =============================================================================================
     * METODO: ELIMINA LOS DATOS ALMACENADOS EN CACHE
     **/

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    /**
     * =============================================================================================
     * METODO: ELIMINA EL DIRECTORIO DE CACHE
     **/
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        else if(dir!= null && dir.isFile()) return dir.delete();
        else return false;
    }
}
