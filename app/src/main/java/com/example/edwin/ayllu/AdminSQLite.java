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
    //tabla usuarios
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

    //tabla paises
    public static final String TABLENAMEPAIS = "paises";
    public static final String CODIGO_PAIS = "codigo_pais";
    public static final String NOMBRE_PAIS = "nombre_pais";


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

    private static final String SQLPAIS = "create table "+TABLENAMEPAIS +"(" +
            CODIGO_PAIS + " text not null primary key"
            + NOMBRE_PAIS+ " text not null";

    private static final String SQLPAISINSERT =
            "insert into "+TABLENAMEPAIS + "("+CODIGO_PAIS +","+NOMBRE_PAIS+") values ('01','ARGENTINA');"
            +"insert into "+TABLENAMEPAIS + "("+CODIGO_PAIS +","+NOMBRE_PAIS+") values ('02','BOLIVIA');"
            +"insert into "+TABLENAMEPAIS + "("+CODIGO_PAIS +","+NOMBRE_PAIS+") values ('03','CHILE');"
            +"insert into "+TABLENAMEPAIS + "("+CODIGO_PAIS +","+NOMBRE_PAIS+") values ('04','COLOMBIA');"
            +"insert into "+TABLENAMEPAIS + "("+CODIGO_PAIS +","+NOMBRE_PAIS+") values ('05','ECUADOR');"
            +"insert into "+TABLENAMEPAIS + "("+CODIGO_PAIS +","+NOMBRE_PAIS+") values ('06','PERÚ');" ;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
