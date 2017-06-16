package cl.pingon.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TblChecklistHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = TblChecklistDefinition.Entry.TABLE_NAME+".db";

    public TblChecklistHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TblChecklistDefinition.Entry.TABLE_NAME;
        query += " ("+TblChecklistDefinition.Entry.FRM_ID+" INTEGER NOT NULL,";
        query += TblChecklistDefinition.Entry.CHK_ID+" INTEGER NOT NULL,";
        query += TblChecklistDefinition.Entry.CAM_ID+" INTEGER NOT NULL,";
        query += TblChecklistDefinition.Entry.CAM_POSICION+" INTEGER NOT NULL,";
        query += TblChecklistDefinition.Entry.CUSTOM_LIST+" INTEGER NOT NULL,";
        query += TblChecklistDefinition.Entry.ACTIVO+" INTEGER NOT NULL,";
        query += TblChecklistDefinition.Entry.CHK_NOMBRE+ " TEXT NOT NULL,";
        query += TblChecklistDefinition.Entry.CAM_NOMBRE_INTERNO+ " TEXT NOT NULL,";
        query += TblChecklistDefinition.Entry.CAM_NOMBRE_EXTERNO+ " TEXT NOT NULL,";
        query += TblChecklistDefinition.Entry.CAM_TIPO+ " TEXT NOT NULL,";
        query += TblChecklistDefinition.Entry.CAM_MANDATORIO+ " TEXT NOT NULL,";
        query += TblChecklistDefinition.Entry.CAM_VAL_DEFECTO +" TEXT NOT NULL,";
        query += TblChecklistDefinition.Entry.CAM_PLACE_HOLDER+ " TEXT NOT NULL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        db.insert(TblChecklistDefinition.Entry.TABLE_NAME, null, values);
    }

    public void update(Integer id, ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        String where = TblChecklistDefinition.Entry.CAM_ID + " = "+ id;
        db.update(TblChecklistDefinition.Entry.TABLE_NAME, values, where, null);
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "FRM_ID",
                "CHK_ID",
                "CAM_ID",
                "CAM_POSICION",
                "CUSTOM_LIST",
                "ACTIVO",
                "CHK_NOMBRE",
                "CAM_NOMBRE_INTERNO",
                "CAM_NOMBRE_EXTERNO",
                "CAM_TIPO",
                "CAM_MANDATORIO",
                "CAM_VAL_DEFECTO",
                "CAM_PLACE_HOLDER"
        };
        Cursor cursor = db.query(TblChecklistDefinition.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }

    public Cursor getAllGroupByChkNombre(int FRM_ID){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "CHK_ID",
                "CAM_ID",
                "CAM_POSICION",
                "CUSTOM_LIST",
                "ACTIVO",
                "CHK_NOMBRE",
                "CAM_NOMBRE_INTERNO",
                "CAM_NOMBRE_EXTERNO",
                "CAM_TIPO",
                "CAM_MANDATORIO",
                "CAM_VAL_DEFECTO",
                "CAM_PLACE_HOLDER"
        };
        Cursor cursor = db.query(TblChecklistDefinition.Entry.TABLE_NAME, projection, "ACTIVO = ? AND FRM_ID = ?", new String[]{String.valueOf(1), String.valueOf(FRM_ID)}, TblChecklistDefinition.Entry.CHK_NOMBRE, null, null);
        return cursor;
    }

    public Cursor getAllByFrmIdAndChkId(int FRM_ID, int CHK_ID){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "CHK_ID",
                "CAM_ID",
                "CAM_POSICION",
                "CUSTOM_LIST",
                "ACTIVO",
                "CHK_NOMBRE",
                "CAM_NOMBRE_INTERNO",
                "CAM_NOMBRE_EXTERNO",
                "CAM_TIPO",
                "CAM_MANDATORIO",
                "CAM_VAL_DEFECTO",
                "CAM_PLACE_HOLDER"
        };
        Cursor cursor = db.query(TblChecklistDefinition.Entry.TABLE_NAME, projection, "ACTIVO = ? AND FRM_ID = ? AND CHK_ID = ?", new String[]{String.valueOf(1), String.valueOf(FRM_ID), String.valueOf(CHK_ID)}, null, null, TblChecklistDefinition.Entry.CAM_POSICION+" ASC");
        return cursor;
    }


    public Cursor getByFrmId(int FRM_ID){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                TblChecklistDefinition.Entry.CHK_ID,
                TblChecklistDefinition.Entry.CAM_ID,
                TblChecklistDefinition.Entry.CAM_POSICION,
                TblChecklistDefinition.Entry.CUSTOM_LIST,
                TblChecklistDefinition.Entry.ACTIVO,
                TblChecklistDefinition.Entry.CHK_NOMBRE,
                TblChecklistDefinition.Entry.CAM_NOMBRE_INTERNO,
                TblChecklistDefinition.Entry.CAM_NOMBRE_EXTERNO,
                TblChecklistDefinition.Entry.CAM_TIPO,
                TblChecklistDefinition.Entry.CAM_MANDATORIO,
                TblChecklistDefinition.Entry.CAM_VAL_DEFECTO,
                TblChecklistDefinition.Entry.CAM_PLACE_HOLDER
        };
        Cursor cursor = db.query(TblChecklistDefinition.Entry.TABLE_NAME, projection,
                "FRM_ID = ?",
                new String[]{
                        String.valueOf(FRM_ID)
        },null, null, TblChecklistDefinition.Entry.CHK_ID+","+TblChecklistDefinition.Entry.CAM_POSICION+" ASC");
        return cursor;
    }

}
