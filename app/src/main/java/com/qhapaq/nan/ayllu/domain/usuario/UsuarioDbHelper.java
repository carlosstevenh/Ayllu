package com.qhapaq.nan.ayllu.domain.usuario;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

import static com.qhapaq.nan.ayllu.domain.usuario.UsuarioContract.UsuarioEntry;

public class UsuarioDbHelper extends SQLiteOpenHelper {
    public static final int DATABESE_VERSION = 1;
    public static final String DATABESE_NAME = "Usuarios.db";

    public UsuarioDbHelper(Context context) {
        super(context, DATABESE_NAME, null, DATABESE_VERSION);
    }

    /**
     * =============================================================================================
     * METODO: Crea la estructura inicial de la tabla Usuarios
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + UsuarioEntry.TABLE_NAME + " ("
                + UsuarioEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UsuarioEntry.CODIGO + " INTEGER NOT NULL, "
                + UsuarioEntry.IDENTIFICACION + " TEXT NOT NULL, "
                + UsuarioEntry.NOMBRE + " TEXT NOT NULL, "
                + UsuarioEntry.APELLIDO + " TEXT NOT NULL, "
                + UsuarioEntry.TIPO + " TEXT NOT NULL, "
                + UsuarioEntry.ESTADO + " TEXT NOT NULL, "
                + UsuarioEntry.PAIS + " INTEGER NOT NULL, "
                + UsuarioEntry.CLAVE_API + " TEXT NOT NULL, "
                + UsuarioEntry.EMAIL + " TEXT NOT NULL, "
                + UsuarioEntry.WORK + " TEXT NOT NULL)"
        );
    }

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * =============================================================================================
     * METODO: Registra una listado de datos en la tabla Usuarios
     */
    public void saveUsuarioList(ArrayList<Usuario> usuarios) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Usuario usuario;

        for (int i = 0; i < usuarios.size(); i++) {
            usuario = usuarios.get(i);
            sqLiteDatabase.insert(UsuarioEntry.TABLE_NAME, null, usuario.toContentValues());
        }
    }

    /**
     * =============================================================================================
     * METODO: Registra un dato en la tabla Usuarios
     */
    public long saveUsuario(Usuario usuario) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert(UsuarioEntry.TABLE_NAME, null, usuario.toContentValues());
    }

    /**
     * =============================================================================================
     * METODO: Obtiene el tamaño de la tabla Usuarios
     */
    public int getSizeDatabase() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + UsuarioEntry.TABLE_NAME, null);
        cursor.close();
        if (cursor.moveToFirst()) return cursor.getInt(0);
        else return 0;
    }

    /**
     * =============================================================================================
     * METODO: Procesa una consulta
     */
    public Cursor generateQuery(String query) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(query + UsuarioEntry.TABLE_NAME, null);
    }

    /**
     * =============================================================================================
     * METODO: Procesa una consulta con un condicional WHERE
     */
    public Cursor generateConditionalQuery(String[] condition, String[] atributo) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String consult = atributo[0] + "=? AND " + atributo[1] + "=?";

        return sqLiteDatabase.query(UsuarioEntry.TABLE_NAME, null, consult, condition, null, null, null, null);
    }

    /**
     * =============================================================================================
     * METODO: Elimina la base de datos Tramos
     */
    public void deleteDataBase() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(UsuarioEntry.TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }

    /**
     * =============================================================================================
     * METODO: Procesa datos de un Cursor
     */
    public Usuario processCursor(Cursor cursor){

        return new Usuario(
                cursor.getString(1), cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(9), cursor.getString(10),
                cursor.getString(5), cursor.getString(6),
                cursor.getString(7), cursor.getString(8));
    }
}
