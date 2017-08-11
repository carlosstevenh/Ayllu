package com.example.edwin.ayllu.ui.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.Usuario;
import com.example.edwin.ayllu.R;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class UsuariosAdapter  extends RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder> {

    private ArrayList<Usuario> usuarios;
    private Activity context;

    /**
     * =============================================================================================
     * METODO:
     */
    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        //Campos del usuario
        TextView tvName;
        TextView tvSurname;
        TextView tvID;
        ImageView ivUser;
        ImageButton ibMenu;

        UsuarioViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvSurname = (TextView) itemView.findViewById(R.id.tv_surname);
            tvID = (TextView) itemView.findViewById(R.id.tv_id);
            ivUser = (ImageView) itemView.findViewById(R.id.iv_user);
            ibMenu = (ImageButton) itemView.findViewById(R.id.ib_menu);
        }
    }

    /**
     * =============================================================================================
     * METODO:
     */
    public UsuariosAdapter (Activity context, ArrayList<Usuario> users) {
        this.context = context;
        this.usuarios = users;
    }

    /**
     * =============================================================================================
     * METODO:
     */
    public void addAll(ArrayList<Usuario> lista){
        usuarios.addAll(lista);
        notifyDataSetChanged();
    }

    /**
     * =============================================================================================
     * METODO:
     */
    public void clear(){
        usuarios.clear();
        notifyDataSetChanged();
    }

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    public UsuarioViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.card_view_user, viewGroup, false);
        return new UsuarioViewHolder(v);
    }

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    public void onBindViewHolder(UsuarioViewHolder holder, int position) {
        Usuario currentUser = usuarios.get(position);

        holder.tvName.setText(currentUser.getNombre_usu());
        holder.tvSurname.setText(currentUser.getApellido_usu());
        holder.tvID.setText(currentUser.getIdentificacion_usu());

        holder.ibMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSimpleDialog("Hola, esto es una prueba :)","PRUEBA").show();
            }
        });
    }
    /**
     * =============================================================================================
     * METODO:
     */
    public AlertDialog createSimpleDialog(String mensaje, String titulo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_monitoring_info, null);
        builder.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title_dialog);
        TextView tvDescription = (TextView) v.findViewById(R.id.tv_description_dialog);

        tvTitle.setText(titulo);
        tvDescription.setText(mensaje);

        builder.setPositiveButton(context.getResources().getString(R.string.registration_form_dialog_option_positive),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.create().dismiss();
                    }
                });

        return builder.create();
    }

}
