package com.example.edwin.ayllu.domain;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static com.example.edwin.ayllu.domain.PaisContract.PaisEntry;

public class PaisDbHelper extends SQLiteOpenHelper {

    public static final int DATABESE_VERSION = 1;
    public static final String DATABESE_NAME = "Paises.db";

    public PaisDbHelper(Context context) {
        super(context, DATABESE_NAME, null, DATABESE_VERSION);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Crea la estructura inicial de la tabla Paises
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + PaisEntry.TABLE_NAME + " ("
                + PaisEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PaisEntry.CODIGO + " TEXT NOT NULL, "
                + PaisEntry.NOMBRE + " TEXT NOT NULL )");
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //----------------------------------------------------------------------------------------------
    //METODO: Registra un dato en la tabla Paises
    public long savePais(Pais datos) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert(PaisEntry.TABLE_NAME, null, datos.toContentValues());
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Registra una lista de datos en la tabla Paises
    public void savePaisList(ArrayList<Pais> paises) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Pais datos;

        for (int i = 0; i < paises.size(); i++) {
            datos = paises.get(i);
            sqLiteDatabase.insert(PaisEntry.TABLE_NAME, null, datos.toContentValues());
        }
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Obtiene el tamaño de la tabla Paises
    public int getSizeDatabase() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + PaisEntry.TABLE_NAME, null);
        if (cursor.moveToFirst()) return cursor.getInt(0);
        else return 0;
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesa una consulta
    public Cursor generateQuery(String query) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(query + PaisEntry.TABLE_NAME, null);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesa una consulta con un condicional WHERE
    public Cursor generateConditionalQuery(String[] condition, String atributo) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT * FROM " + PaisEntry.TABLE_NAME + " WHERE " + atributo + "=?";

        return sqLiteDatabase.rawQuery(query, condition);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Elimina la base de datos Paises
    public void deleteDataBase() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(PaisEntry.TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }
}
