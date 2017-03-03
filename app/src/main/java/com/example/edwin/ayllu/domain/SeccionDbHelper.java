package com.example.edwin.ayllu.domain;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static com.example.edwin.ayllu.domain.SeccionContract.SeccionEntry;

public class SeccionDbHelper extends SQLiteOpenHelper {

    public static final int DATABESE_VERSION = 1;
    public static final String DATABESE_NAME = "Secciones.db";

    public SeccionDbHelper(Context context) {
        super(context, DATABESE_NAME, null, DATABESE_VERSION);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Crea la estructura inicial de la tabla Secciones
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + SeccionEntry.TABLE_NAME + " ("
                + SeccionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SeccionEntry.CODIGO + " INTEGER NOT NULL, "
                + SeccionEntry.DESCRIPCION + " TEXT NOT NULL, "
                + SeccionEntry.SUBTRAMO + " INTEGER NOT NULL )");
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //----------------------------------------------------------------------------------------------
    //METODO: Registra una lista de datos en la tabla Secciones
    public void saveSeccionList (ArrayList<Seccion> secciones) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Seccion datos;

        for (int i = 0; i<secciones.size(); i++){
            datos = secciones.get(i);
            sqLiteDatabase.insert(SeccionEntry.TABLE_NAME, null, datos.toContentValues());
        }
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Registra un dato en la tabla Secciones
    public long saveSeccion(Seccion datos) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert(SeccionEntry.TABLE_NAME, null, datos.toContentValues());
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Obtiene el tamaño de la tabla Secciones
    public int getSizeDatabase() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + SeccionEntry.TABLE_NAME, null);
        if(cursor.moveToFirst()) return cursor.getInt(0);
        else return 0;
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesa una consulta
    public Cursor generateQuery(String query) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(query + SeccionEntry.TABLE_NAME, null);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesa una consulta con un condicional WHERE
    public Cursor generateConditionalQuery(String[] condition, String atributo) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT * FROM " + SeccionEntry.TABLE_NAME + " WHERE " + atributo + "=?";

        return sqLiteDatabase.rawQuery(query,condition);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Elimina la base de datos Secciones
    public void deleteDataBase() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(SeccionEntry.TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }
}
