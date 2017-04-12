package com.example.edwin.ayllu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
    String result = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = getArguments().getString("RESULT");
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
                tvDescription.setText(getResources().getString(R.string.successfulRecordMonitoringDescription));
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
                ivType.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
                ivType.setContentDescription(getResources().getString(R.string.failedRecordImageDescription));
                tvTitle.setText(getResources().getString(R.string.failedRecordTitle));
                tvDescription.setText(getResources().getString(R.string.failedRecordMonitoringDescription));
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
    }
}
