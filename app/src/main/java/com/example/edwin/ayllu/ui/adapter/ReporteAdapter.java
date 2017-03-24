package com.example.edwin.ayllu.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.domain.Reporte;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ReporteViewHolder> implements View.OnClickListener{
    private View.OnClickListener listener;
    ArrayList<Reporte> reportes;
    Context context;

    public ReporteAdapter(Context context) {
        this.context = context;
        this.reportes = new ArrayList<>();
    }
    //----------------------------------------------------------------------------------------------
    @Override
    public ReporteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_report,parent,false);

        itemView.setOnClickListener(this);
        return new ReporteViewHolder(itemView);
    }
    //----------------------------------------------------------------------------------------------
    //CONECTAR EL VIEWHOLDER CON LOS DATOS
    @Override
    public void onBindViewHolder(ReporteViewHolder holder, int position) {
        Reporte currentReporte = reportes.get(position);

        holder.setReporteVariable(currentReporte.getVariable());
        holder.setReporteArea(currentReporte.getArea());
        holder.setReporteLatitud(currentReporte.getLatitud());
        holder.setReporteLongitud(currentReporte.getLongitud());
        holder.setReporteFecha(currentReporte.getFecha_mon());
        holder.setReporteMonitor(currentReporte.getUsuario());

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = s.format(new Date());

        if(currentReporte.getFecha_mon().equals(fecha)) holder.setDisabledCardView();
    }
    //----------------------------------------------------------------------------------------------
    //RETORNA EL TAMAÑO DE CUANTOS ELEMENTOS HAY EN LA LISTA
    @Override
    public int getItemCount() {
        return reportes.size();
    }
    //----------------------------------------------------------------------------------------------
    public void addAll(@NonNull ArrayList<Reporte> reportes){
        if(reportes == null)
            throw new NullPointerException("Los items no pueden ser nulos");
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
    public class ReporteViewHolder extends RecyclerView.ViewHolder{

        TextView variable;
        TextView area;
        TextView latitud;
        TextView longitud;
        TextView fecha;
        TextView monitor;
        RelativeLayout principal;

        public ReporteViewHolder(View itemView) {
            super(itemView);

            variable = (TextView) itemView.findViewById(R.id.tv_variable);
            area = (TextView) itemView.findViewById(R.id.tv_area);
            latitud = (TextView) itemView.findViewById(R.id.tv_latitud);
            longitud = (TextView) itemView.findViewById(R.id.tv_longitud);
            fecha = (TextView) itemView.findViewById(R.id.tv_fecha);
            monitor = (TextView) itemView.findViewById(R.id.tv_monitor);
            principal = (RelativeLayout) itemView.findViewById(R.id.area_principal);
        }

        public void setReporteVariable (String cad){
            variable.setText(cad);
        }
        public void setReporteArea (String cad){ area.setText(cad);}
        public void setReporteLatitud (String cad){ latitud.setText(cad);}
        public void setReporteLongitud (String cad) { longitud.setText(cad);}
        public void setReporteFecha (String cad) { fecha.setText(cad);}
        public void setReporteMonitor (String cad) { monitor.setText(cad);}
        public void setDisabledCardView () { principal.setBackgroundColor(principal.getResources().getColor(R.color.colorItemSecondaryCardView));}
    }
}
