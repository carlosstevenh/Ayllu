package com.example.edwin.ayllu.domain;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                + TaskEntry.NOMBRE + " TEXT NOT NULL )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long saveTask(Task datos) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert(TaskEntry.TABLE_NAME, null, datos.toContentValues());
    }

    public Cursor getSizeDatabase() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + TaskEntry.TABLE_NAME, null);
    }

    public Cursor generateQuery(String query) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(query + TaskEntry.TABLE_NAME, null);
    }

    public void deleteDataBase(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TaskEntry.TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }
}
