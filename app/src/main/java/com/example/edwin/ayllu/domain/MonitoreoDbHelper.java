package com.example.edwin.ayllu.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static com.example.edwin.ayllu.domain.MonitoreoContract.MonitoreoEntry;

public class MonitoreoDbHelper extends SQLiteOpenHelper {
    public static final int DATABESE_VERSION = 1;
    public static final String DATABESE_NAME = "Monitoreos.db";

    public MonitoreoDbHelper(Context context) {
        super(context, DATABESE_NAME, null, DATABESE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + MonitoreoEntry.TABLE_NAME + " ("
                + MonitoreoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MonitoreoEntry.PUNTO + " TEXT NOT NULL, "
                + MonitoreoEntry.FECHA + " TEXT NOT NULL, "
                + MonitoreoEntry.MONITOR + " TEXT NOT NULL, "
                + MonitoreoEntry.PROPIEDAD + " TEXT NOT NULL, "
                + MonitoreoEntry.VARIABLE + " TEXT NOT NULL, "
                + MonitoreoEntry.LATITUD + " TEXT NOT NULL, "
                + MonitoreoEntry.LONGITUD + " TEXT NOT NULL, "
                + MonitoreoEntry.REPERCUSIONES + " TEXT NOT NULL, "
                + MonitoreoEntry.ORIGEN + " TEXT NOT NULL, "
                + MonitoreoEntry.PORCENTAJE + " TEXT NOT NULL, "
                + MonitoreoEntry.FRECUENCIA + " TEXT NOT NULL, "
                + MonitoreoEntry.NOMBRE + " TEXT NOT NULL, "
                + MonitoreoEntry.NOMBRE2 + " TEXT NOT NULL, "
                + MonitoreoEntry.NOMBRE3 + " TEXT NOT NULL, "
                + MonitoreoEntry.ESTADO + " TEXT NOT NULL )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    /**
     * =============================================================================================
     * METODO: Registra un dato en la tabla Monitoreos
     **/
    public long saveMonitoreos(Reporte datos) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        datos.setEstado("OFFLINE");
        return sqLiteDatabase.insert(MonitoreoEntry.TABLE_NAME, null, datos.toContentValues());
    }

    /**
     * =============================================================================================
     * METODO: Obtiene el tamaño de la tabla Monitoreos
     **/
    public Cursor getSizeDatabase() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + MonitoreoEntry.TABLE_NAME, null);
    }

    /**
     * =============================================================================================
     * METODO: Procesa una consulta
     **/
    public Cursor generateQuery(String query) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(query + MonitoreoEntry.TABLE_NAME + " ORDER BY "+ MonitoreoEntry._ID + " ASC", null);
    }

    /**
     * =============================================================================================
     * METODO: Borrar la base de datos
     **/
    public void deleteDataBase(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(MonitoreoEntry.TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }

    /**
     * =============================================================================================
     * METODO: Borrar un Elemento de la base de datos
     **/
    public void generateConditionalDelete(String[] condition, String atributo) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(MonitoreoEntry.TABLE_NAME, atributo+"="+condition[0], null);
    }

    /**
     * =============================================================================================
     * METODO: Actualizar un Elemento de la base de datos
     **/
    public void generateConditionalUpdate(String[] condition, String[] atributo) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(atributo[0], "null");

        sqLiteDatabase.update(MonitoreoEntry.TABLE_NAME, valores, atributo[1]+"="+condition[0], null);
    }

    /**
     * =============================================================================================
     * METODO: Obtener un listado de monitoreos
     **/
    public ArrayList<Reporte> getMonitoreos() {
        Cursor cursor;
        ArrayList<Reporte> reportes = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MonitoreoEntry.TABLE_NAME, null);

        if (cursor.moveToFirst()){
            do{
                Reporte reporte = new Reporte();

                reporte.setCod_paf(Integer.parseInt(cursor.getString(1)));
                reporte.setFecha_mon(cursor.getString(2));
                reporte.setUsuario(cursor.getString(3));
                reporte.setArea(cursor.getString(4));
                reporte.setVariable(cursor.getString(5));
                reporte.setLatitud(cursor.getString(6));
                reporte.setLongitud(cursor.getString(7));
                reporte.setRepercusiones(cursor.getString(8));
                reporte.setOrigen(cursor.getString(9));
                reporte.setPorcentaje(Integer.parseInt(cursor.getString(10)));
                reporte.setFrecuencia(Integer.parseInt(cursor.getString(11)));
                reporte.setPrueba1(cursor.getString(12));
                reporte.setPrueba2(cursor.getString(13));
                reporte.setPrueba3(cursor.getString(14));
                reporte.setEstado(cursor.getString(15));

                reportes.add(reporte);
            } while (cursor.moveToNext());
        }

        return reportes;
    }
}
