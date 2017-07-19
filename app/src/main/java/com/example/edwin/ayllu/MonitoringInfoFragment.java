package com.example.edwin.ayllu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MonitoringInfoFragment extends Fragment {
    LinearLayout lyPrincipal;
    ImageView ivType;
    TextView tvTitle, tvDescription;
    String result = "", tipo = "", area = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = getArguments().getString("RESULT");
        tipo = getArguments().getString("TIPO");

        if(tipo != null){
            if(tipo.equals("NEW") && (result.equals("OK") || result.equals("OFFLINE")))
                area = getArguments().getString("AREA");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_monitoring_info, container, false);

        lyPrincipal = (LinearLayout) view.findViewById(R.id.ly_principal);
        ivType = (ImageView) view.findViewById(R.id.iv_type_record);
        tvTitle = (TextView) view.findViewById(R.id.tv_title_record);
        tvDescription = (TextView) view.findViewById(R.id.tv_description_record);

        switch (result){
            case "OK":
                lyPrincipal.setBackgroundColor(getResources().getColor(R.color.colorSplashBackground));
                ivType.setImageDrawable(getResources().getDrawable(R.drawable.ic_positive));
                ivType.setContentDescription(getResources().getString(R.string.successfulRecordImageDescription));
                tvTitle.setText(getResources().getString(R.string.successfulRecordTitle));
                if(tipo != null){
                    if(tipo.equals("EVALUATION")) tvDescription.setText(getResources().getString(R.string.successfulRecordResponseDescription));
                    else tvDescription.setText(getResources().getString(R.string.successfulRecordMonitoringDescription));
                }
                break;
            case "OFFLINE":
                lyPrincipal.setBackgroundColor(getResources().getColor(R.color.colorSplashBackground));
                ivType.setImageDrawable(getResources().getDrawable(R.drawable.ic_offline));
                ivType.setContentDescription(getResources().getString(R.string.offlineRecordImageDescription));
                tvTitle.setText(getResources().getString(R.string.offlineRecordTitle));
                tvDescription.setText(getResources().getString(R.string.offlineRecordMonitoringDescription));
                break;
            case "ERROR":
                lyPrincipal.setBackgroundColor(getResources().getColor(R.color.colorSplashBackgroundFailed));
                ivType.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_icono));
                ivType.setContentDescription(getResources().getString(R.string.failedRecordImageDescription));
                tvTitle.setText(getResources().getString(R.string.failedRecordTitle));
                if (tipo != null){
                    if (tipo.equals("EVALUATION")) tvDescription.setText(getResources().getString(R.string.failedRecordResponseDescription));
                    else tvDescription.setText(getResources().getString(R.string.failedRecordMonitoringDescription));
                }
                break;
            default:
                break;
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(result.equals("ERROR")){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getFragmentManager().popBackStack();
                }
            },2000);
        }
        else {
            if (area.equals("")){
                if(tipo.equals("EVALUATION")){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().finish();
                        }
                    },2000);
                }
                else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getActivity(), MonitorMenuActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    },2000);
                }
            } else  createSimpleDialog(getResources().getString(R.string.info_dialog_description),getResources().getString(R.string.info_dialog_title)).show();
        }
    }

    /**
     * =============================================================================================
     * METODO: Genera un Dialogo basico en pantalla
     **/
    public AlertDialog createSimpleDialog(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_monitoring_info, null);
        builder.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title_dialog);
        TextView tvDescription = (TextView) v.findViewById(R.id.tv_description_dialog);

        tvTitle.setText(titulo);
        tvTitle.setCompoundDrawables(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_question), null, null, null);
        tvDescription.setText(mensaje);

        builder.setPositiveButton(getResources().getString(R.string.info_dialog_option_accept),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MonitoringRegistrationFormFragment fragment = new MonitoringRegistrationFormFragment();
                                Bundle params = new Bundle();
                                params.putString("AREA", area);
                                params.putString("OPCION", "N");
                                fragment.setArguments(params);

                                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.monitoring_principal_context, fragment)
                                        .commit();
                            }
                        },1000);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.info_dialog_option_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getActivity(), MonitorMenuActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                },1000);
                            }
                        });

        return builder.create();
    }
}
