package cl.pingon.SQLite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TblFormulariosHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = TblFormulariosDefinition.Entry.TABLE_NAME+".db";

    public TblFormulariosHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TblFormulariosDefinition.Entry.TABLE_NAME;
        query += " ("+TblFormulariosDefinition.Entry.ARN_ID+" INTEGER NOT NULL,";
        query += TblFormulariosDefinition.Entry.ARN_NOMBRE+ " TEXT NOT NULL,";
        query += TblFormulariosDefinition.Entry.FRM_ID+ " INTEGER NOT NULL,";
        query += TblFormulariosDefinition.Entry.FRM_NOMBRE+ " TEXT NOT NULL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        db.insert(TblFormulariosDefinition.Entry.TABLE_NAME, null, values);
    }

    public void update(Integer id, ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        String where = TblFormulariosDefinition.Entry.ARN_ID + " = "+ id;
        db.update(TblFormulariosDefinition.Entry.TABLE_NAME, values, where, null);
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {"ARN_ID", "ARN_NOMBRE", "FRM_ID", "FRM_NOMBRE"};
        Cursor cursor = db.query(TblFormulariosDefinition.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }

    public Cursor getByArnId(int ARN_ID){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {"ARN_ID", "ARN_NOMBRE", "FRM_ID", "FRM_NOMBRE"};
        Cursor cursor = db.query(TblFormulariosDefinition.Entry.TABLE_NAME, projection, "ARN_ID = ?", new String[]{String.valueOf(ARN_ID)}, null, null, null, null);
        return cursor;
    }

    public Cursor getByArnIdFrmId(int ARN_ID, int FRM_ID){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {"ARN_ID", "ARN_NOMBRE", "FRM_ID", "FRM_NOMBRE"};
        Cursor cursor = db.query(TblFormulariosDefinition.Entry.TABLE_NAME,
                projection,
                "ARN_ID = ? AND FRM_ID = ?",
                new String[]{String.valueOf(ARN_ID), String.valueOf(FRM_ID)}, null, null, null, null);
        return cursor;
    }
}
