package com.example.edwin.ayllu.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.edwin.ayllu.domain.TaskContract.TaskEntry;

public class TaskDbHelper extends SQLiteOpenHelper {

    public static final int DATABESE_VERSION = 1;
    public static final String DATABESE_NAME = "Tasks.db";

    public TaskDbHelper(Context context) {
        super(context, DATABESE_NAME, null, DATABESE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TaskEntry.TABLE_NAME + " ("
                + TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TaskEntry.MONITOR + " TEXT NOT NULL, "
                + TaskEntry.VARIABLE + " TEXT NOT NULL, "
                + TaskEntry.AREA + " TEXT NOT NULL, "
                + TaskEntry.LATITUD + " TEXT NOT NULL, "
                + TaskEntry.LONGITUD + " TEXT NOT NULL, "
                + TaskEntry.FECHA + " TEXT NOT NULL, "
                + TaskEntry.REPERCUSIONES + " TEXT NOT NULL, "
                + TaskEntry.ORIGEN + " TEXT NOT NULL, "
                + TaskEntry.PORCENTAJE + " TEXT NOT NULL, "
                + TaskEntry.FRECUENCIA + " TEXT NOT NULL, "
                + TaskEntry.NOMBRE + " TEXT NOT NULL, "
                + TaskEntry.NOMBRE2 + " TEXT NOT NULL, "
                + TaskEntry.NOMBRE3 + " TEXT NOT NULL, "
                + TaskEntry.TIPO + " TEXT NOT NULL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    /**
     * =============================================================================================
     * METODO: Registra un dato en la tabla Task
     **/
    public long saveTask(Task datos) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert(TaskEntry.TABLE_NAME, null, datos.toContentValues());
    }

    /**
     * =============================================================================================
     * METODO: Obtiene el tamaño de la tabla Subtramos
     **/
    public Cursor getSizeDatabase() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + TaskEntry.TABLE_NAME, null);
    }

    /**
     * =============================================================================================
     * METODO: Procesa una consulta
     **/
    public Cursor generateQuery(String query) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(query + TaskEntry.TABLE_NAME + " ORDER BY "+ TaskEntry._ID + " ASC", null);
    }

    /**
     * =============================================================================================
     * METODO: Borrar la base de datos
     **/
    public void deleteDataBase(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TaskEntry.TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }

    /**
     * =============================================================================================
     * METODO: Borrar un Elemento de la base de datos
     **/
    public void generateConditionalDelete(String[] condition, String atributo) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TaskEntry.TABLE_NAME, atributo+"="+condition[0], null);
    }

    /**
     * =============================================================================================
     * METODO: Actualizar un Elemento de la base de datos
     **/
    public void generateConditionalUpdate(String[] condition, String[] atributo) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(atributo[0], "null");

        sqLiteDatabase.update(TaskEntry.TABLE_NAME, valores, atributo[1]+"="+condition[0], null);
    }

}
