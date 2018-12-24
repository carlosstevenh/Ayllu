package com.qhapaq.nan.ayllu.ui.utilities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.qhapaq.nan.ayllu.R;

public class ToolbarUtility {

    /*----------------------------------------------------------------------------------------------
    * Establece las propiedades (titulo/Previous_Botton)
    *
    * @param <code>Activity activity</code> : actividad que solicita el metodo
    * @param title : titulo para el toolbar
    * @param upButton : Determina si el toolbar tiene un boton de regreso*/
    public static void showToolbar(Activity activity, View view, String title, Boolean upButton) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(title);
        ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }
}
