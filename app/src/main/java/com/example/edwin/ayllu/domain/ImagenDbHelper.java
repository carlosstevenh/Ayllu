package com.example.edwin.ayllu.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.edwin.ayllu.domain.ImagenContract.ImagenEntry;

public class ImagenDbHelper extends SQLiteOpenHelper {

    public static final int DATABESE_VERSION = 1;
    public static final String DATABESE_NAME = "Imagenes.db";

    public ImagenDbHelper(Context context) {
        super(context, DATABESE_NAME, null, DATABESE_VERSION);
    }

    /**
     * =============================================================================================
     * METODO: Crea la estructura inicial de la tabla Imagenes
     **/
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + ImagenEntry.TABLE_NAME + " ("
                + ImagenEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ImagenEntry.FOTOGRAFIA1 + " TEXT NOT NULL, "
                + ImagenEntry.FOTOGRAFIA2 + " TEXT NOT NULL, "
                + ImagenEntry.FOTOGRAFIA3 + " TEXT NOT NULL )");
    }

    /**
     * =============================================================================================
     * METODO:
     **/
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * =============================================================================================
     * METODO: Registra un dato en la tabla Imagenes
     **/
    public long saveImagen(Imagen datos) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert(ImagenEntry.TABLE_NAME, null, datos.toContentValues());
    }

    /**
     * =============================================================================================
     * METODO: Obtiene el tamaño de la tabla Imagenes
     **/
    public int getSizeDatabase() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + ImagenEntry.TABLE_NAME, null);
        if(cursor.moveToFirst()) return cursor.getInt(0);
        else return 0;
    }

    /**
     * =============================================================================================
     * METODO: Procesa una consulta
     **/
    public Cursor generateQuery(String query) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(query + ImagenEntry.TABLE_NAME, null);
    }

    /**
     * =============================================================================================
     * METODO: Procesa una consulta con un condicional WHERE
     **/
    public Cursor generateConditionalQuery(String[] condition, String atributo) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT * FROM " + ImagenEntry.TABLE_NAME + " WHERE " + atributo + "=?";

        return sqLiteDatabase.rawQuery(query,condition);
    }

    /**
     * =============================================================================================
     * METODO: Elimina la base de datos Imagenes
     **/
    public void deleteDataBase() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(ImagenEntry.TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }

    /**
     * =============================================================================================
     * METODO: Actualizar un Elemento de la base de datos Imagenes
     **/
    public void generateConditionalUpdate(String[] condition, String[] atributo) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(atributo[0], "null");

        sqLiteDatabase.update(ImagenEntry.TABLE_NAME, valores, atributo[1]+"="+condition[0], null);
    }
}
