package com.qhapaq.nan.ayllu.ui.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qhapaq.nan.ayllu.domain.Mensaje;
import com.qhapaq.nan.ayllu.domain.usuario.Usuario;
import com.qhapaq.nan.ayllu.R;
import com.qhapaq.nan.ayllu.domain.usuario.UsuarioDbHelper;
import com.qhapaq.nan.ayllu.io.ApiConstants;
import com.qhapaq.nan.ayllu.io.AylluApiAdapter;
import com.qhapaq.nan.ayllu.io.AylluApiService;
import com.qhapaq.nan.ayllu.ui.AdminUserTransactionActivity;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder> {

    private ArrayList<Usuario> usuarios;
    private Activity context;
    private String id = "", name = "", surname = "", email = "", work = "", codigo = "", estado;
    private UsuarioDbHelper usuarioDbHelper;
    String monitor = "", pais = "";
    Cursor cursor;

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
        usuarioDbHelper = new UsuarioDbHelper(context);
        Cursor cursor = usuarioDbHelper.generateQuery("SELECT * FROM ");
        if (cursor.moveToFirst()){
            monitor = cursor.getString(1);
            pais = "0"+cursor.getString(7);
        }
        cursor.close();

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
                email = usuarios.get(position).getEmail();
                work = usuarios.get(position).getWork();
                codigo = usuarios.get(position).getCodigo_usu();

                if (estado.equals("H")) createMenuDialog("LIST ENABLED", position);
                else if (estado.equals("D")) createMenuDialog("LIST DISABLED", position);
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

    private void changeList(ArrayList<Usuario> lista){
        this.usuarios = lista;
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
    private void createMenuDialog(final String type_list, final int pos) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_admin_menu_user, null);
        builder.setView(v);

        LinearLayout optionEdit = (LinearLayout) v.findViewById(R.id.option_edit);
        LinearLayout optionState = (LinearLayout) v.findViewById(R.id.option_state);
        ImageView ivState = (ImageView) v.findViewById(R.id.iv_state);
        TextView tvState = (TextView) v.findViewById(R.id.tv_state);

        if (type_list.equals("LIST DISABLED")) {
            ivState.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_menu_user_enabled));
            tvState.setText(context.getResources().getString(R.string.list_admin_dialog_option_enabled));
        } else if (type_list.equals("LIST ENABLED")) {
            ivState.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_menu_user_disabled));
            tvState.setText(context.getResources().getString(R.string.list_admin_dialog_option_disabled));
        }

        final AlertDialog alertDialog = builder.create();

        optionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usuarios.get(pos).getTipo_usu().equals("E")){
                    alertDialog.dismiss();
                    createSimpleDialog(context.getResources().getString(R.string.list_admin_message_edit),context.getResources().getString(R.string.list_admin_message_title),"EDIT",pos).show();
                }
                else {
                    Intent intent = new Intent(context, AdminUserTransactionActivity.class);
                    intent.putExtra("TYPE", "UPDATE");
                    intent.putExtra("ID", id);
                    intent.putExtra("NAME", name);
                    intent.putExtra("SURNAME", surname);
                    intent.putExtra("CODIGO", codigo);
                    intent.putExtra("EMAIL", email);
                    intent.putExtra("WORK", work);
                    context.startActivity(intent);

                    usuarios.get(pos).setTipo_usu("E");

                    alertDialog.dismiss();
                }
            }
        });


        optionState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                String msj = context.getResources().getString(R.string.list_admin_disabled_text);
                String tit = context.getResources().getString(R.string.list_admin_disabled_title);

                if (type_list.equals("LIST DISABLED")){
                    msj = context.getResources().getString(R.string.list_admin_enabled_text);
                    tit = context.getResources().getString(R.string.list_admin_enabled_title);
                }

                createSimpleDialog(msj+" "+usuarios.get(pos).getNombre_usu()+"?",tit,"DISABLED",pos).show();
            }
        });

        alertDialog.show();
    }

    /**
     * =============================================================================================
     * METODO: Genera un Dialogo basico en pantalla
     **/
    private AlertDialog createSimpleDialog(String mensaje, String titulo, final String type, final int post) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_monitoring_info, null);
        builder.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tv_title_dialog);
        TextView tvDescription = (TextView) v.findViewById(R.id.tv_description_dialog);

        tvTitle.setText(titulo);
        tvTitle.setCompoundDrawables(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_question), null, null, null);
        tvDescription.setText(mensaje);

        final ArrayList<Usuario> temp_list = this.usuarios;

        builder.setPositiveButton(context.getResources().getString(R.string.list_admin_dialog_option_continue),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (type.equals("EDIT")){

                            builder.create().dismiss();

                            Intent intent = new Intent(context, AdminUserTransactionActivity.class);
                            intent.putExtra("TYPE", "UPDATE");
                            intent.putExtra("ID", id);
                            intent.putExtra("NAME", name);
                            intent.putExtra("SURNAME", surname);
                            intent.putExtra("CODIGO", codigo);
                            intent.putExtra("EMAIL", email);
                            intent.putExtra("WORK", work);
                            context.startActivity(intent);

                            temp_list.get(post).setTipo_usu("E");
                        }
                        else {
                            String cad = context.getResources().getString(R.string.list_admin_disabled);
                            String user_state = "D";
                            if (estado.equals("D")) {
                                cad = context.getResources().getString(R.string.list_admin_enabled);
                                user_state = "H";
                            }

                            Usuario new_usr = new Usuario(codigo,"","","","","","",user_state,"","");

                            //TODO: URL USUARIOS (REVISAR LLAMADO) --ACTUALIZADO -- OK

                            ApiConstants apiConstants = new ApiConstants();

                            String url = apiConstants.buildUrl(pais, "API");

                            Retrofit retrofit = prepareRetrofit(url);
                            AylluApiService service = retrofit.create(AylluApiService.class);
                            Call<Mensaje> call = service.estadoUsuario(new_usr);
                            final String finalCad = cad;
                            call.enqueue(new Callback<Mensaje>() {
                                @Override
                                public void onResponse(Call<Mensaje> call, Response<Mensaje> response) {
                                    if (response.isSuccessful()){
                                        temp_list.remove(post);
                                        changeList(temp_list);
                                        builder.create().dismiss();
                                        Toast.makeText(context, finalCad, Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Mensaje> call, Throwable t) {

                                }
                            });
                        }
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.list_admin_dialog_option_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        });

        return builder.create();
    }

    private Retrofit prepareRetrofit(String url) {
        //------------------------------------------------------------------------------------------
        //Preparamos el servicio de Retrofit
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient.build())
                .build();
    }
}
