package com.example.edwin.ayllu.domain;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static com.example.edwin.ayllu.domain.SubtramoContract.SubtramoEntry;

public class SubtramoDbHelper extends SQLiteOpenHelper {

    public static final int DATABESE_VERSION = 1;
    public static final String DATABESE_NAME = "Subtramos.db";

    public SubtramoDbHelper(Context context) {
        super(context, DATABESE_NAME, null, DATABESE_VERSION);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Crea la estructura inicial de la tabla Subtramos
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + SubtramoEntry.TABLE_NAME + " ("
                + SubtramoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SubtramoEntry.CODIGO + " INTEGER NOT NULL, "
                + SubtramoEntry.DESCRIPCION + " TEXT NOT NULL, "
                + SubtramoEntry.TRAMO + " INTEGER NOT NULL )");
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //----------------------------------------------------------------------------------------------
    //METODO: Registra una lista de datos en la tabla Subtramos
    public void saveSubtramoList(ArrayList<Subtramo> subtramos) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Subtramo datos;

        for (int i = 0; i < subtramos.size(); i++) {
            datos = subtramos.get(i);
            sqLiteDatabase.insert(SubtramoEntry.TABLE_NAME, null, datos.toContentValues());
        }
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Registra un dato en la tabla Subtramos
    public long saveSubtramo(Subtramo datos) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert(SubtramoEntry.TABLE_NAME, null, datos.toContentValues());
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Obtiene el tamaño de la tabla Subtramos
    public int getSizeDatabase() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + SubtramoEntry.TABLE_NAME, null);
        if(cursor.moveToFirst()) return cursor.getInt(0);
        else return 0;
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesa una consulta
    public Cursor generateQuery(String query) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(query + SubtramoEntry.TABLE_NAME, null);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesa una consulta con un condicional WHERE
    public Cursor generateConditionalQuery(String[] condition, String atributo) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT * FROM " + SubtramoEntry.TABLE_NAME + " WHERE " + atributo + "=?";

        return sqLiteDatabase.rawQuery(query,condition);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Elimina la base de datos Subtramos
    public void deleteDataBase() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(SubtramoEntry.TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }
}
