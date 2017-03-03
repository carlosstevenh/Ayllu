package com.example.edwin.ayllu.domain;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static com.example.edwin.ayllu.domain.VariableContract.VariableEntry;

public class VariableDbHelper extends SQLiteOpenHelper {

    public static final int DATABESE_VERSION = 1;
    public static final String DATABESE_NAME = "Variables.db";

    public VariableDbHelper(Context context) {
        super(context, DATABESE_NAME, null, DATABESE_VERSION);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Crea la estructura inicial de la tabla Variables
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + VariableEntry.TABLE_NAME + " ("
                + VariableEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + VariableEntry.CODIGO + " TEXT NOT NULL, "
                + VariableEntry.NOMBRE + " TEXT NOT NULL, "
                + VariableEntry.FACTOR + " TEXT NOT NULL )");
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //----------------------------------------------------------------------------------------------
    //METODO: Registra una lista de datos en la tabla Variables
    public void saveVariableList(ArrayList<Variable> variables) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Variable datos;

        for (int i = 0; i < variables.size(); i++) {
            datos = variables.get(i);
            sqLiteDatabase.insert(VariableContract.VariableEntry.TABLE_NAME, null, datos.toContentValues());
        }
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Registra un dato en la tabla Variables
    public long saveVariable(Variable datos) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert(VariableEntry.TABLE_NAME, null, datos.toContentValues());
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Obtiene el tamaño de la tabla Variables
    public int getSizeDatabase() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + VariableEntry.TABLE_NAME, null);
        if(cursor.moveToFirst()) return cursor.getInt(0);
        else return 0;
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesa una consulta
    public Cursor generateQuery(String query) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(query + VariableEntry.TABLE_NAME, null);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Procesa una consulta con un condicional WHERE
    public Cursor generateConditionalQuery(String[] condition, String atributo) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT * FROM " + VariableEntry.TABLE_NAME + " WHERE " + atributo + "=?";

        return sqLiteDatabase.rawQuery(query,condition);
    }

    //----------------------------------------------------------------------------------------------
    //METODO: Elimina la base de datos Variables
    public void deleteDataBase() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(VariableEntry.TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }
}
