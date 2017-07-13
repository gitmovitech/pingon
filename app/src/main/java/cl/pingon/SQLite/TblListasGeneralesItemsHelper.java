package cl.pingon.SQLite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TblListasGeneralesItemsHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = TblListasGeneralesItemsDefinition.Entry.TABLE_NAME+".db";

    public TblListasGeneralesItemsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TblListasGeneralesItemsDefinition.Entry.TABLE_NAME;
        query += " ("+TblListasGeneralesItemsDefinition.Entry.ID+" INTEGER NOT NULL,";
        query += TblListasGeneralesItemsDefinition.Entry.ID_LISTA+" INTEGER NOT NULL,";
        query += TblListasGeneralesItemsDefinition.Entry.NAME+ " TEXT NOT NULL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void deleteAll(){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(TblListasGeneralesItemsDefinition.Entry.TABLE_NAME, null, null);
        db.close();
    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        db.insert(TblListasGeneralesItemsDefinition.Entry.TABLE_NAME, null, values);
    }

    public void update(Integer id, ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        String where = TblListasGeneralesItemsDefinition.Entry.ID + " = "+ id;
        db.update(TblListasGeneralesItemsDefinition.Entry.TABLE_NAME, values, where, null);
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                TblListasGeneralesItemsDefinition.Entry.ID,
                TblListasGeneralesItemsDefinition.Entry.NAME,
                TblListasGeneralesItemsDefinition.Entry.ID_LISTA
        };
        Cursor cursor = db.query(TblListasGeneralesItemsDefinition.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }

    public Cursor getByListaId(int id){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                TblListasGeneralesItemsDefinition.Entry.ID,
                TblListasGeneralesItemsDefinition.Entry.NAME
        };
        Cursor cursor = db.query(TblListasGeneralesItemsDefinition.Entry.TABLE_NAME, projection, TblListasGeneralesItemsDefinition.Entry.ID_LISTA+"=?", new String[]{String.valueOf(id)}, null, null, null);
        return cursor;
    }

}
