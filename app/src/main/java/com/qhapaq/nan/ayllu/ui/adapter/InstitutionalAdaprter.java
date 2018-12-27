package com.qhapaq.nan.ayllu.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qhapaq.nan.ayllu.R;
import com.qhapaq.nan.ayllu.domain.monitoreo.Monitoreo;
import com.qhapaq.nan.ayllu.domain.usuario.UsuarioDbHelper;
import com.qhapaq.nan.ayllu.io.ApiConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by steven on 27/11/16.
 */

public class InstitutionalAdaprter extends RecyclerView.Adapter<InstitutionalAdaprter.MonitoreoHolder> implements View.OnClickListener {
    private ArrayList<Monitoreo> monitoreos;
    private View.OnClickListener listener;
    Context context;

    public InstitutionalAdaprter(Context context) {
        this.context = context;
        this.monitoreos = new ArrayList<>();
    }

    public InstitutionalAdaprter(ArrayList<Monitoreo> monitoreos) {
        this.monitoreos = monitoreos;
    }

    @Override
    public MonitoreoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_monitoring,parent,false);
        v.setOnClickListener(this);
        MonitoreoHolder holder = new MonitoreoHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MonitoreoHolder holder, int position) {
        holder.date.setText(monitoreos.get(position).getDate());
        holder.variable.setText(monitoreos.get(position).getVariable());
        holder.latitud.setText(reverseCoordinates(monitoreos.get(position).getLatitud(),"LATITUD"));
        holder.longitud.setText(reverseCoordinates(monitoreos.get(position).getLongitud(),"LONGITUD"));
        //TODO: AQUI CARGA LAS IMAGENES
        UsuarioDbHelper usuarioDbHelper = new UsuarioDbHelper(context);
        Cursor cursor = usuarioDbHelper.generateQuery("SELECT * FROM ");
        String pais= "";
        if (cursor.moveToFirst()) {
            pais = "0" + cursor.getString(7);
        }
        ApiConstants apiConstants = new ApiConstants();
        String URL = apiConstants.buildUrl(pais,"IMG");
        holder.setPrueba(URL+monitoreos.get(position).getPrueba());
        holder.propiedad.setText(monitoreos.get(position).getPropiedad());
    }

    @Override
    public int getItemCount() {
        return monitoreos.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null) listener.onClick(view);
    }


    public static class MonitoreoHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView propiedad;
        TextView variable;
        TextView latitud;
        TextView longitud;
        ImageView prueba;
        TextView pripiedad;

        public MonitoreoHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.tv_fecha);
            propiedad = (TextView) itemView.findViewById(R.id.tv_property);
            variable = (TextView) itemView.findViewById(R.id.tv_variable);
            latitud = (TextView) itemView.findViewById(R.id.tv_latitud);
            longitud = (TextView) itemView.findViewById(R.id.tv_longitud);
            prueba = (ImageView) itemView.findViewById(R.id.iv_prueba);
            propiedad = (TextView) itemView.findViewById(R.id.tv_property);
        }

        void setPrueba(String cad) { Picasso.get().load(cad).fit().centerCrop().into(prueba);}

    }

    //----------------------------------------------------------------------------------------------
    public void clear(){
        this.monitoreos.clear();
        notifyDataSetChanged();
    }

    public void addAll(@NonNull ArrayList<Monitoreo> monitoreos){
        if(monitoreos == null)
            throw new NullPointerException("Los items no pueden ser nulos");
        this.monitoreos.addAll(monitoreos);
        notifyItemRangeInserted(getItemCount()-1, monitoreos.size());
    }

    private String reverseCoordinates(String cad, String opc){
        String reverseCad;
        if (opc.equals("LATITUD")){
            reverseCad =    cad.charAt(0) +  "-" + cad.charAt(1) + cad.charAt(2) + "°" +
                    cad.charAt(3) + cad.charAt(4) + "'" +
                    cad.charAt(5) + cad.charAt(6) + "''";
        }
        else {
            reverseCad =    cad.charAt(0) +  "-" + cad.charAt(1) + cad.charAt(2) + cad.charAt(3) + "°" +
                    cad.charAt(4) + cad.charAt(5) + "'" +
                    cad.charAt(6) + cad.charAt(7) + "''";
        }

        return reverseCad;
    }
}
