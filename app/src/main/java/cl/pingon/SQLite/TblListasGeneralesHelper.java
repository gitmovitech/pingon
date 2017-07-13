package cl.pingon.SQLite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TblListasGeneralesHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = TblListasGeneralesDefinition.Entry.TABLE_NAME+".db";

    public TblListasGeneralesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TblListasGeneralesDefinition.Entry.TABLE_NAME;
        query += " ("+TblListasGeneralesDefinition.Entry.ID+" INTEGER NOT NULL,";
        query += TblListasGeneralesDefinition.Entry.NAME+ " TEXT NOT NULL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void deleteAll(){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(TblListasGeneralesDefinition.Entry.TABLE_NAME, null, null);
        db.close();
    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        db.insert(TblListasGeneralesDefinition.Entry.TABLE_NAME, null, values);
    }

    public void update(Integer id, ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        String where = TblListasGeneralesDefinition.Entry.ID + " = "+ id;
        db.update(TblListasGeneralesDefinition.Entry.TABLE_NAME, values, where, null);
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                TblListasGeneralesDefinition.Entry.ID,
                TblListasGeneralesDefinition.Entry.NAME
        };
        Cursor cursor = db.query(TblListasGeneralesDefinition.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }
}
