package com.example.edwin.ayllu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by steven on 11/11/16.
 */

public class AdminSQLite extends SQLiteOpenHelper {
    public AdminSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public static final String TABLENAME = "login";
    public static final String COD_USU_SQL = "cod_sql";
    public static final String COD_USU = "codigo";
    public static final String IDE_USU = "identificacion";
    public static final String NOM_USU = "nombre";
    public static final String APE_USU = "apellido";
    public static final String TIP_USU = "tipo";
    public static final String CON_USU = "contrasena";
    public static final String CLA_API = "clave";
    public static final String PAI_USU = "pais";


    private static final  String SQL = "create table " + TABLENAME + " ("
            + COD_USU_SQL + " integer primary key autoincrement,"
            + COD_USU + " integer  not null,"
            + IDE_USU + " text not null,"
            + NOM_USU + " text not null,"
            + APE_USU + " text not null,"
            + TIP_USU + " text not null,"
            + CON_USU + " text not null,"
            + CLA_API + " text not null,"
            + PAI_USU + " text not null);";



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
