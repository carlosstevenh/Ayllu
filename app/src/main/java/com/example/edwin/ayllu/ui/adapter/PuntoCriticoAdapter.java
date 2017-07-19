package com.example.edwin.ayllu.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.domain.PuntoCritico;

import java.util.ArrayList;

/**
 * Created by steven on 16/02/17.
 */

public class PuntoCriticoAdapter extends RecyclerView.Adapter<PuntoCriticoAdapter.pcHolder> implements View.OnClickListener{

    private ArrayList<PuntoCritico> puntosCriticos;
    private View.OnClickListener listener;

    public PuntoCriticoAdapter(ArrayList<PuntoCritico> puntosCriticos) {
        this.puntosCriticos = puntosCriticos;
    }

    @Override
    public pcHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_critical_point,parent,false);
        v.setOnClickListener(this);
        pcHolder holder = new pcHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(pcHolder holder, int position) {
        holder.pais.setText(puntosCriticos.get(position).getPais());
        holder.area.setText(puntosCriticos.get(position).getArea());
        holder.factor.setText(puntosCriticos.get(position).getFactor());
        holder.variable.setText(puntosCriticos.get(position).getVariable());
        holder.date.setText(puntosCriticos.get(position).getFecha());

        int por = Integer.parseInt(puntosCriticos.get(position).getPorcentaje());
        int fre = Integer.parseInt(puntosCriticos.get(position).getFrecuencia());

        float aux = (float) ((float)(por+fre)/2.0);
        if(aux > 3.1)holder.color.setBackgroundColor(Color.parseColor("#FF0000"));
        else if(aux >2.4 && aux < 3.1)holder.color.setBackgroundColor(Color.parseColor("#FFC300"));
        else if(aux < 2.4) holder.color.setBackgroundColor(Color.parseColor("#54EB40"));

    }

    @Override
    public int getItemCount() {
        return puntosCriticos.size();
    }
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
    @Override
    public void onClick(View view) {
        if(listener != null) listener.onClick(view);
    }

    public static class pcHolder extends RecyclerView.ViewHolder {
        TextView pais;
        TextView area;
        TextView factor;
        TextView variable;
        TextView date;
        TextView color;

        public pcHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.tv_date);
            pais = (TextView) itemView.findViewById(R.id.tv_country);
            area = (TextView) itemView.findViewById(R.id.tv_property);
            factor = (TextView) itemView.findViewById(R.id.tv_factor);
            variable = (TextView) itemView.findViewById(R.id.tv_variable);
            color = (TextView) itemView.findViewById(R.id.tv_state);
        }

    }
}
