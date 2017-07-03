package com.example.edwin.ayllu.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.example.edwin.ayllu.domain.Usuario;
import com.example.edwin.ayllu.R;

import java.util.ArrayList;

/**
 * Created by steven on 14/11/16.
 */

public class UsuariosAdapter  extends ArrayAdapter<Usuario> {
    private TextView nombre;
    private TextView apellido;
    private TextView identificacion;

    public UsuariosAdapter(Context context, ArrayList<Usuario> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(
                    R.layout.usuarios_adapter,
                    parent,
                    false);
        }

        nombre = (TextView) convertView.findViewById(R.id.tv_name);
        apellido = (TextView) convertView.findViewById(R.id.date);
        identificacion = (TextView) convertView.findViewById(R.id.tv_company);

        // Lead actual.
        Usuario user = getItem(position);

        // Setup.
        nombre.setText(user.getNombre_usu());
        apellido.setText(user.getApellido_usu());
        identificacion.setText(user.getIdentificacion_usu());
        return convertView;
    }
}
