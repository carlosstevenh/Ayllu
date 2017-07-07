package com.example.edwin.ayllu.ui.adapter;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.domain.Reporte;
import com.example.edwin.ayllu.io.ApiConstants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class MonitoringAdapter extends RecyclerView.Adapter<MonitoringAdapter.ReporteViewHolder> implements View.OnClickListener{
    private View.OnClickListener listener;
    private ArrayList<Reporte> reportes;
    private Context context;

    public MonitoringAdapter(Context context) {
        this.context = context;
        this.reportes = new ArrayList<>();
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public ReporteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.card_view_monitoring,parent,false);

        itemView.setOnClickListener(this);
        return new ReporteViewHolder(itemView);
    }
    //----------------------------------------------------------------------------------------------
    //CONECTAR EL VIEWHOLDER CON LOS DATOS
    @Override
    public void onBindViewHolder(ReporteViewHolder holder, int position) {
        Reporte currentReporte = reportes.get(position);

        holder.setReporteVariable(currentReporte.getVariable());
        holder.setReporteLatitud(currentReporte.getLatitud());
        holder.setReporteLongitud(currentReporte.getLongitud());
        holder.setReporteFecha(currentReporte.getFecha_mon());
        if(currentReporte.getEstado().equals("ONLINE")) holder.setPruebaOnline(ApiConstants.URL_IMG + currentReporte.getPrueba1());
        else {
            File file = new File(Environment.getExternalStorageDirectory() + "/Ayllu/Offline/" +currentReporte.getPrueba1());
            holder.setPruebaOffline(file);
        }
    }
    //----------------------------------------------------------------------------------------------
    //RETORNA EL TAMAÑO DE CUANTOS ELEMENTOS HAY EN LA LISTA
    @Override
    public int getItemCount() {
        return reportes.size();
    }
    //----------------------------------------------------------------------------------------------
    public void addAll(@NonNull ArrayList<Reporte> reportes){
        this.reportes.addAll(reportes);
        notifyItemRangeInserted(getItemCount()-1, reportes.size());
    }
    //----------------------------------------------------------------------------------------------
    public void clear(){
        this.reportes.clear();
        notifyDataSetChanged();
    }
    //----------------------------------------------------------------------------------------------
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
    @Override
    public void onClick(View v) {
        if(listener != null)
            listener.onClick(v);
    }
    //----------------------------------------------------------------------------------------------
    class ReporteViewHolder extends RecyclerView.ViewHolder{

        TextView variable;
        TextView latitud;
        TextView longitud;
        TextView fecha;
        ImageView prueba;

        ReporteViewHolder(View itemView) {
            super(itemView);

            variable = (TextView) itemView.findViewById(R.id.tv_variable);
            latitud = (TextView) itemView.findViewById(R.id.tv_latitud);
            longitud = (TextView) itemView.findViewById(R.id.tv_longitud);
            fecha = (TextView) itemView.findViewById(R.id.tv_fecha);
            prueba = (ImageView) itemView.findViewById(R.id.iv_prueba);
        }

        void setReporteVariable(String cad){
            variable.setText(cad);
        }
        void setReporteLatitud(String cad){ latitud.setText(cad);}
        void setReporteLongitud(String cad) { longitud.setText(cad);}
        void setReporteFecha(String cad) { fecha.setText(cad);}
        void setPruebaOnline(String cad) {
            Picasso.with(itemView.getContext()).load(cad).fit().centerCrop().into(prueba);
        }
        void setPruebaOffline(File cad) {
            Picasso.with(itemView.getContext()).load(cad).fit().centerCrop().into(prueba);
        }
    }
}
