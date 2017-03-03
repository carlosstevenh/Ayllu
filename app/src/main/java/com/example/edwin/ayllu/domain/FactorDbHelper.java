package com.example.edwin.ayllu.domain;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static com.example.edwin.ayllu.domain.FactorContract.FactorEntry;

public class FactorDbHelper extends SQLiteOpenHelper {

    public static final int DATABESE_VERSION = 1;
    public static final String DATABESE_NAME = "Factores.db";

    public FactorDbHelper(Context context) {
        super(context, DATABESE_NAME, null, DATABESE_VERSION);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Crea la estructura inicial de la tabla Factores
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + FactorEntry.TABLE_NAME + " ("
                + FactorEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FactorEntry.CODIGO + " TEXT NOT NULL, "
                + FactorEntry.NOMBRE + " TEXT NOT NULL )");
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //----------------------------------------------------------------------------------------------
    //METODO: Registra una lista de datos en la tabla Factor
    public void saveFactorList(ArrayList<Factor> factores) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Factor datos;

        for (int i = 0; i < factores.size(); i++) {
            datos = factores.get(i);
            sqLiteDatabase.insert(FactorEntry.TABLE_NAME, null, datos.toContentValues());
        }
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Registra un dato en la tabla Factores
    public long saveFactor(Factor datos) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert(FactorEntry.TABLE_NAME, null, datos.toContentValues());
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Obtiene el tamaño de la tabla Factores
    public int getSizeDatabase() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + FactorEntry.TABLE_NAME, null);
        if(cursor.moveToFirst()) return cursor.getInt(0);
        else return 0;
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesa una consulta
    public Cursor generateQuery(String query) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(query + FactorEntry.TABLE_NAME, null);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesa una consulta con un condicional WHERE
    public Cursor generateConditionalQuery(String[] condition, String atributo) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT * FROM " + FactorEntry.TABLE_NAME + " WHERE " + atributo + "=?";

        return sqLiteDatabase.rawQuery(query,condition);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Elimina la base de datos Factores
    public void deleteDataBase() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(FactorEntry.TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }
}
