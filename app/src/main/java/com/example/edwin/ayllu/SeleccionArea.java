package com.example.edwin.ayllu;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.edwin.ayllu.domain.Area;
import com.example.edwin.ayllu.domain.Seccion;
import com.example.edwin.ayllu.domain.Subtramo;
import com.example.edwin.ayllu.domain.Tramo;

import java.util.ArrayList;

public class SeleccionArea extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Tramo> tramos = new ArrayList<>();
    ArrayList<Subtramo> subtramos = new ArrayList<>();
    ArrayList<Seccion> secciones = new ArrayList<>();
    ArrayList<Area> areas = new ArrayList<>();
    int[] opciones = {0,0,0,0};

    CharSequence[] items_tramos, items_subtramos, items_secciones, items_areas;
    ArrayList<String> list_tramos, list_subtramos, list_secciones, list_areas;

    ImageButton imgBtn_tramos, imgBtn_subtramos, imgBtn_secciones, imgBtn_areas;
    FloatingActionButton fab_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        //------------------------------------------------------------------------------------------
        //ARGENTINA
        tramos.add(new Tramo(1, "Otros-Tramos-Argentina", "01"));

        subtramos.add(new Subtramo ("1", "Santa Ana-Valle Colorado", "1"));
        subtramos.add(new Subtramo ("2", "Santa Rosa de Tastil-Potrero de Payogasta", "1"));
        subtramos.add(new Subtramo ("3", "Potrero de Payogasta-Los Graneros de la Poma", "1"));
        subtramos.add(new Subtramo ("4", "Otros-Subtramos-Argentina", "1"));
        subtramos.add(new Subtramo ("5", "Los Corrales-Las Pircas", "1"));
        subtramos.add(new Subtramo ("6", "Ciénaga de Yalguaraz-Puente del Inca", "1"));

        secciones.add(new Seccion("1", "Quebrada Grande-Las escaleras", "1"));
        secciones.add(new Seccion("2", "Santa Rosa de Tastil", "2"));
        secciones.add(new Seccion("3", "Abra de Chaupiyaco-Las Capillas", "2"));
        secciones.add(new Seccion("4", "Las Peras-Sauzalito", "3"));
        secciones.add(new Seccion("5", "Complejo Ceremonial Volcán Llullaillaco", "4"));
        secciones.add(new Seccion("6", "Complejo Arqueológico La Ciudacita", "4"));
        secciones.add(new Seccion("7", "Pucará del Aconquija", "4"));
        secciones.add(new Seccion("8", "Angualasto-Colangüil", "4"));
        secciones.add(new Seccion("9", "Llano de los Leones", "4"));
        secciones.add(new Seccion("10", "Los Corrales-Las Pircas", "5"));
        secciones.add(new Seccion("11", "Ciénaga de Yalguaraz-San Alberto", "6"));
        secciones.add(new Seccion("12", "Ranchillos", "6"));
        secciones.add(new Seccion("13", "Tambillitos", "6"));
        secciones.add(new Seccion("14", "Puente del Inca", "6"));

        areas.add(new Area("1", "C", "Quebrada Grande-Las escaleras", "1"));
        areas.add(new Area("2", "CS", "Santa Rosa de Tastil", "2"));
        areas.add(new Area("3", "CS", "Abra de Chaupiyaco-Las Capillas", "3"));
        areas.add(new Area("4", "CS", "Las Peras-Sauzalito", "4"));
        areas.add(new Area("5", "CS", "Complejo Ceremonial Volcán Llullaillaco", "5"));
        areas.add(new Area("6", "CS", "Complejo Arqueológico La Ciudacita", "6"));
        areas.add(new Area("7", "CS", "Pucará del Aconquija", "7"));
        areas.add(new Area("8", "CS", "Angualasto-Colangüil", "8"));
        areas.add(new Area("9", "CS", "Llano de los Leones", "9"));
        areas.add(new Area("10", "CS", "Los Corrales-Las Pircas", "10"));
        areas.add(new Area("11", "CS", "Ciénaga de Yalguaraz-San Alberto", "11"));
        areas.add(new Area("12", "CS", "Ranchillos", "12"));
        areas.add(new Area("13", "CS", "Tambillitos", "13"));
        areas.add(new Area("14", "CS", "Puente del Inca", "14"));


        //------------------------------------------------------------------------------------------
        //BOLIVIA
        tramos.add(new Tramo(2, "Desaguadero-Viacha", "02"));

        subtramos.add(new Subtramo ("7", "Desaguadero-Guaqui", "2"));
        subtramos.add(new Subtramo ("8", "Guaqui-Tiwanacu Tiwanacu-Cantapa", "2"));
        subtramos.add(new Subtramo ("9", "Cantapa-Yanamuyu Alto", "2"));
        subtramos.add(new Subtramo ("10", "Yanamuyu Alto-Viacha", "2"));

        secciones.add(new Seccion("15", "Desaguadero Titijoni", "7"));
        secciones.add(new Seccion("16", "Tiwanacu", "8"));
        secciones.add(new Seccion("17", "Kallamarka-Apacheta", "9"));
        secciones.add(new Seccion("18", "Quimsa Cruz-Ilata", "10"));

        areas.add(new Area("15", "CS", "Desaguadero Titijoni", "15"));
        areas.add(new Area("16", "CS", "Tiwanacu", "16"));
        areas.add(new Area("17", "CS", "Kallamarka-Apacheta", "17"));
        areas.add(new Area("18", "CS", "Quimsa Cruz-Ilata", "18"));

        //------------------------------------------------------------------------------------------
        //COLOMBIA
        tramos.add(new Tramo(3, "Rumichaca-Pasto", "04"));

        subtramos.add(new Subtramo ("11", "Otros-Subtramos-Colombia", "3"));

        secciones.add(new Seccion("19", "Rumichaca", "11"));
        secciones.add(new Seccion("20", "San Pedro", "11"));
        secciones.add(new Seccion("21", "La Cofradia", "11"));
        secciones.add(new Seccion("22", "La Paz", "11"));
        secciones.add(new Seccion("23", "Chitarran", "11"));
        secciones.add(new Seccion("24", "Rosal de Chapal", "11"));
        secciones.add(new Seccion("25", "Guapuscal Bajo", "11"));
        secciones.add(new Seccion("26", "Inantaz", "11"));
        secciones.add(new Seccion("27", "Los Ajos", "11"));

        areas.add(new Area("19", "C", "Rumichaca", "19"));
        areas.add(new Area("20", "C", "San Pedro", "20"));
        areas.add(new Area("21", "C", "La Cofradia", "21"));
        areas.add(new Area("22", "C", "La Paz", "22"));
        areas.add(new Area("23", "C", "Chitarran", "23"));
        areas.add(new Area("24", "C", "Rosal de Chapal", "24"));
        areas.add(new Area("25", "C", "Guapuscal Bajo", "25"));
        areas.add(new Area("26", "C", "Inantaz", "26"));
        areas.add(new Area("27", "C", "Los Ajos", "27"));*/

        items_tramos = new CharSequence[tramos.size()];
        for (int i=0; i<tramos.size(); i++) items_tramos[i] = tramos.get(i).getDescripcion_t();

        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_monitoring_registration_form1);

        imgBtn_tramos = (ImageButton) findViewById(R.id.btn_tramos);
        imgBtn_subtramos = (ImageButton) findViewById(R.id.btn_subtramos);
        imgBtn_secciones = (ImageButton) findViewById(R.id.btn_secciones);
        imgBtn_areas = (ImageButton) findViewById(R.id.btn_areas);

        fab_next = (FloatingActionButton) findViewById(R.id.fab_next);*/

        imgBtn_tramos.setOnClickListener(this);
        imgBtn_subtramos.setOnClickListener(this);
        imgBtn_secciones.setOnClickListener(this);
        imgBtn_areas.setOnClickListener(this);

        fab_next.setOnClickListener(this);

        imgBtn_subtramos.setEnabled(false);
        imgBtn_secciones.setEnabled(false);
        imgBtn_areas.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        /*switch (view.getId()){
            case R.id.btn_tramos:
                createRadioListDialog(items_tramos, "Seleccione un Tramo", 1).show();
                break;
            case R.id.btn_subtramos:
                createRadioListDialog(items_subtramos, "Selecione un Subtramo", 2).show();
                break;
            case R.id.btn_secciones:
                createRadioListDialog(items_secciones, "Seleccione una Sección", 3).show();
                break;
            case R.id.btn_areas:
                createRadioListDialog(items_areas, "Seleccione una Área", 4).show();
                break;
            case R.id.fab_next:
                String respuesta = procesarOpciones(opciones);
                if(!respuesta.equals("")) createSimpleDialog(respuesta, "Opciones Elegidas", 1).show();
                else createSimpleDialog("Aun faltan elementos por seleccionar", "ERROR USUARIO", 2).show();
                break;
            default:
                break;
        }*/
    }

    public AlertDialog createRadioListDialog(final CharSequence[] items, String title, final int zn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int op = which+1;
                int con = 0;
                String opc = "";
                /*
                switch (zn){
                    case 1:
                        list_subtramos = new ArrayList<String>();
                        for (int i=0; i<tramos.size(); i++){
                            if(tramos.get(i).getDescripcion_t().equals(items[which]))
                                opc = tramos.get(i).getCodigo_t()+"";
                        }

                        opciones[0]=Integer.parseInt(opc);
                        for (int i=1; i<opciones.length; i++) opciones[i] = 0;

                        for (int i = 0; i<subtramos.size(); i++) {
                            if(subtramos.get(i).getTramo().equals(opc))
                                list_subtramos.add(subtramos.get(i).getDescripcion_subtramo());
                        }

                        items_subtramos = new CharSequence[list_subtramos.size()];

                        for (int i = 0; i < list_subtramos.size(); i++)
                            items_subtramos[i] = list_subtramos.get(i);

                        imgBtn_subtramos.setEnabled(true);
                        break;
                    case 2:
                        list_secciones = new ArrayList<String>();
                        for (int i=0; i<subtramos.size(); i++){
                            if(subtramos.get(i).getDescripcion_subtramo().equals(items[which]))
                                opc = subtramos.get(i).getCodigo_subtramo();
                        }

                        opciones[1]=Integer.parseInt(opc);
                        for (int i=2; i<opciones.length; i++) opciones[i] = 0;

                        for (int i = 0; i<secciones.size(); i++) {
                            if(secciones.get(i).getSubtramo().equals(opc))
                                list_secciones.add(secciones.get(i).getDescripcion_seccion());
                        }

                        items_secciones = new CharSequence[list_secciones.size()];

                        for (int i = 0; i < list_secciones.size(); i++)
                            items_secciones[i] = list_secciones.get(i);

                        imgBtn_secciones.setEnabled(true);
                        break;
                    case 3:
                        list_areas = new ArrayList<String>();
                        for (int i=0; i<secciones.size(); i++){
                            if(secciones.get(i).getDescripcion_seccion().equals(items[which]))
                                opc = secciones.get(i).getCodigo_seccion();
                        }

                        opciones[2] = Integer.parseInt(opc);
                        opciones[3] = 0;

                        for (int i = 0; i<areas.size(); i++) {
                            if(areas.get(i).getSeccion().equals(opc))
                                list_areas.add(areas.get(i).getPropiedad_nominada());
                        }

                        items_areas = new CharSequence[list_areas.size()];
                        for (int i = 0; i < list_areas.size(); i++)items_areas[i] = list_areas.get(i);
                        imgBtn_areas.setEnabled(true);
                        break;
                    case 4:
                        for (int i=0; i<areas.size(); i++){
                            if(areas.get(i).getPropiedad_nominada().equals(items[which]))
                                opciones[3]=Integer.parseInt(areas.get(i).getCodigo_area());
                        }

                        for (int i=0; i<opciones.length; i++) Log.e("OPCION:",""+opciones[i]);
                        break;
                    default:
                        break;
                }

                Toast.makeText(
                        SeleccionArea.this,
                        "Seleccionaste: " + items[which],
                        Toast.LENGTH_SHORT)
                        .show();*/
            }
        });

        return builder.create();
    }

    public AlertDialog createSimpleDialog(String mensaje, String titulo, int op) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final int opci = op;
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (opci){
                                    case 1:
                                        Intent intent = new Intent(SeleccionArea.this, InstitutionalActivity.class);
                                        intent.putExtra("AREA",""+opciones[3]);
                                        startActivity(intent);
                                        finish();
                                        break;
                                    case 2:
                                        builder.create().dismiss();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        });

        return builder.create();
    }

    public String procesarOpciones(int[] opciones){
        for (int i=0; i<opciones.length; i++)if(opciones[i] == 0) return "";

        String resp = "";
        /*for (int i=0; i<opciones.length; i++){
            switch (i){
                case 0:
                    for (int j=0; j<tramos.size(); j++){
                        if(tramos.get(j).getCodigo_t() == opciones[i])
                            resp += "TRAMO: "+tramos.get(j).getDescripcion_t()+"\n";
                    }
                    break;
                case 1:
                    for (int j=0; j<subtramos.size(); j++){
                        if(subtramos.get(j).getCodigo_subtramo().equals(""+opciones[i]))
                            resp += "SUBTRAMO: "+subtramos.get(j).getDescripcion_subtramo()+"\n";
                    }
                    break;
                case 2:
                    for (int j=0; j<secciones.size(); j++){
                        if(secciones.get(j).getCodigo_seccion().equals(""+opciones[i]))
                            resp += "SECCIÓN: "+secciones.get(j).getDescripcion_seccion()+"\n";
                    }
                    break;
                case 3:
                    for (int j=0; j<areas.size(); j++){
                        if(areas.get(j).getCodigo_area().equals(""+opciones[i]))
                            resp += "ÁREA: "+areas.get(j).getPropiedad_nominada()+"\n";
                    }
                    break;
                default:
                    break;
            }
        }*/
        return resp;
    }
}
