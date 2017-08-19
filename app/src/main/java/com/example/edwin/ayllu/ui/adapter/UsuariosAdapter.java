package com.example.edwin.ayllu.ui.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.ayllu.domain.Usuario;
import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.ui.AdminUserTransactionActivity;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder> {

    private ArrayList<Usuario> usuarios;
    private Activity context;
    private String id = "", name = "", surname = "", estado;

    /**
     * =============================================================================================
     * METODO:
     */
    public UsuariosAdapter(Activity context, String estado) {
        this.context = context;
        this.usuarios = new ArrayList<>();
        this.estado = estado;
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
    public void onBindViewHolder(UsuarioViewHolder holder, final int position) {
        Usuario currentUser = usuarios.get(position);

        holder.tvName.setText(currentUser.getNombre_usu());
        holder.tvSurname.setText(currentUser.getApellido_usu());
        holder.tvID.setText(currentUser.getIdentificacion_usu());

        holder.ibMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = usuarios.get(position).getIdentificacion_usu();
                name = usuarios.get(position).getNombre_usu();
                surname = usuarios.get(position).getApellido_usu();

                if (estado.equals("H")) createSimpleDialog("LIST ENABLED").show();
                else if (estado.equals("D")) createSimpleDialog("LIST DISABLED").show();
            }
        });
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
    public void addAll(ArrayList<Usuario> lista) {
        this.usuarios.addAll(lista);
        notifyDataSetChanged();
    }

    /**
     * =============================================================================================
     * METODO:
     */
    public void clear() {
        this.usuarios.clear();
        notifyDataSetChanged();
    }

    /**
     * =============================================================================================
     * METODO:
     */
    public AlertDialog createSimpleDialog(String type_list) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_admin_menu_user, null);
        builder.setView(v);

        LinearLayout optionEdit = (LinearLayout) v.findViewById(R.id.option_edit);
        LinearLayout optionDisabled = (LinearLayout) v.findViewById(R.id.option_disabled);
        ImageView ivState = (ImageView) v.findViewById(R.id.iv_state);
        TextView tvState = (TextView) v.findViewById(R.id.tv_state);

        if (type_list.equals("LIST DISABLED")) {
            ivState.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_menu_user_enabled));
            tvState.setText("HABILITAR MONITOR");
        }
        else if ( type_list.equals("LIST ENABLED")){
            ivState.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_menu_user_disabled));
            tvState.setText("DESHABILITAR MONITOR");
        }

        optionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminUserTransactionActivity.class);
                intent.putExtra("TYPE", "UPDATE");
                intent.putExtra("ID", id);
                intent.putExtra("NAME", name);
                intent.putExtra("SURNAME", surname);
                context.startActivity(intent);
                builder.create().dismiss();
            }
        });

        return builder.create();
    }


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

}
