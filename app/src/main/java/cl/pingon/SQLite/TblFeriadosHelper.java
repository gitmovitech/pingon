package cl.pingon.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class TblFeriadosHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = TblFeriadosDefinition.Entry.TABLE_NAME+".db";

    public TblFeriadosHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TblFeriadosDefinition.Entry.TABLE_NAME;
        query += " ("+TblFeriadosDefinition.Entry.ID+" INTEGER NOT NULL,";
        query += TblFeriadosDefinition.Entry.DAY+ " TEXT NOT NULL,";
        query += TblFeriadosDefinition.Entry.MONTH+ " TEXT NOT NULL,";
        query += TblFeriadosDefinition.Entry.YEAR+ " TEXT NOT NULL )";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void deleteAll(){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(TblFeriadosDefinition.Entry.TABLE_NAME, null, null);
        db.close();
    }

    public void insert(ContentValues values) {
        SQLiteDatabase db = getReadableDatabase();
        db.insert(TblFeriadosDefinition.Entry.TABLE_NAME, null, values);
    }

    public void update(Integer id, ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        String where = TblFeriadosDefinition.Entry.ID + " = "+ id;
        db.update(TblFeriadosDefinition.Entry.TABLE_NAME, values, where, null);
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                TblFeriadosDefinition.Entry.ID,
                TblFeriadosDefinition.Entry.DAY,
                TblFeriadosDefinition.Entry.MONTH,
                TblFeriadosDefinition.Entry.YEAR
        };
        Cursor cursor = db.query(TblFeriadosDefinition.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }

    public Cursor getById(String ID){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection =  {
                TblFeriadosDefinition.Entry.ID,
                TblFeriadosDefinition.Entry.DAY,
                TblFeriadosDefinition.Entry.MONTH,
                TblFeriadosDefinition.Entry.YEAR
        };
        Cursor cursor = db.query(
                TblFeriadosDefinition.Entry.TABLE_NAME,
                projection,
                TblFeriadosDefinition.Entry.ID + "= ?",
                new String[]{ID},
                null,
                null,
                null);
        return cursor;
    }
}
