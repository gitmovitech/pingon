package cl.pingon.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TblDocumentoHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = TblDocumentoDefinition.Entry.TABLE_NAME+".db";

    public TblDocumentoHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TblDocumentoDefinition.Entry.TABLE_NAME;
        query += " ("+ TblDocumentoDefinition.Entry.ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
        query += TblDocumentoDefinition.Entry.DOC_ID+" INTEGER NULL,";
        query += TblDocumentoDefinition.Entry.USU_ID+" INTEGER NOT NULL,";
        query += TblDocumentoDefinition.Entry.FRM_ID+" INTEGER NOT NULL,";
        query += TblDocumentoDefinition.Entry.DOC_NOMBRE+" TEXT NULL,";
        query += TblDocumentoDefinition.Entry.DOC_FECHA_CREACION+" TEXT NULL,";
        query += TblDocumentoDefinition.Entry.DOC_FECHA_MODIFICACION+" TEXT NULL,";
        query += TblDocumentoDefinition.Entry.DOC_PDF+ " TEXT NULL,";
        query += TblDocumentoDefinition.Entry.DOC_DECLARACION+ " TEXT NULL,";
        query += TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO+ " TEXT NOT NULL,";
        query += TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO+ " TEXT NOT NULL,";
        query += TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE+ " TEXT NOT NULL,";
        query += TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE+ " TEXT NOT NULL,";
        query += TblDocumentoDefinition.Entry.DOC_EXT_OBRA+ " TEXT NOT NULL,";
        query += TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE+ " INTEGER NOT NULL,";
        query += TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO+ " INTEGER NOT NULL,";
        query += TblDocumentoDefinition.Entry.SEND_STATUS+ " TEXT NOT NULL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        db.insert(TblDocumentoDefinition.Entry.TABLE_NAME, null, values);
        Cursor cursor = getAll();
        cursor.moveToLast();
        return cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.ID));
    }

    public void update(Integer id, ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        String where = TblDocumentoDefinition.Entry.ID + " = "+ id;
        db.update(TblDocumentoDefinition.Entry.TABLE_NAME, values, where, null);
    }

    public void deleteById(Integer id){
        SQLiteDatabase db = getReadableDatabase();
        String where = TblDocumentoDefinition.Entry.ID + " = ?";
        db.delete(TblDocumentoDefinition.Entry.TABLE_NAME, where, new String[]{String.valueOf(id)});
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "ID",
                "DOC_ID",
                "USU_ID",
                "FRM_ID",
                "DOC_NOMBRE",
                "DOC_FECHA_CREACION",
                "DOC_FECHA_MODIFICACION",
                "DOC_PDF",
                "DOC_DECLARACION",
                "DOC_EXT_EQUIPO",
                "DOC_EXT_MARCA_EQUIPO",
                "DOC_EXT_NUMERO_SERIE",
                "DOC_EXT_NOMBRE_CLIENTE",
                "DOC_EXT_OBRA",
                "DOC_EXT_ID_CLIENTE",
                "DOC_EXT_ID_PROYECTO",
                "SEND_STATUS"
        };
        Cursor cursor = db.query(TblDocumentoDefinition.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }

    public Cursor getDraftsGroupByCliente(){
        SQLiteDatabase db = getReadableDatabase();
        String[] select = {
                TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE,
                TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE
        };
        Cursor cursor = db.query(
                TblDocumentoDefinition.Entry.TABLE_NAME,
                select,
                TblDocumentoDefinition.Entry.SEND_STATUS+" = ?",
                new String[]{"DRAFT"},
                TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE, null, null);
        return cursor;
    }

    public Cursor getDraftsByProyecto(String proyecto_id){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                TblDocumentoDefinition.Entry.ID,
                TblDocumentoDefinition.Entry.FRM_ID,
                TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO,
                TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO,
                TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE
        };
        Cursor cursor = db.query(
                TblDocumentoDefinition.Entry.TABLE_NAME,
                projection,
                TblDocumentoDefinition.Entry.SEND_STATUS+" = ? AND "+TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO+" = ?",
                new String[]{"DRAFT", proyecto_id},
                null, null, null);
        return cursor;
    }

    public Cursor getDraftsGroupByProyecto(int CLIENTE_ID){
        SQLiteDatabase db = getReadableDatabase();
        String[] select = {
                TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO,
                TblDocumentoDefinition.Entry.DOC_EXT_OBRA
        };
        Cursor cursor = db.query(
                TblDocumentoDefinition.Entry.TABLE_NAME,
                select,
                TblDocumentoDefinition.Entry.SEND_STATUS+" = ? AND "+TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE+" = ?",
                new String[]{"DRAFT", String.valueOf(CLIENTE_ID)},
                TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO, null, null);
        return cursor;
    }

    public Cursor getById(int ID){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                TblDocumentoDefinition.Entry.FRM_ID,
                TblDocumentoDefinition.Entry.DOC_FECHA_CREACION,
                TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE
        };
        Cursor cursor = db.query(TblDocumentoDefinition.Entry.TABLE_NAME, projection, "ID = ?", new String[]{String.valueOf(ID)}, null, null, null);
        return cursor;
    }
}
