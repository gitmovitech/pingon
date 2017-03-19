package cl.pingon.SQLite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TblAreaNegocioHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = TblAreaNegocioDefinition.Entry.TABLE_NAME+".db";

    public TblAreaNegocioHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TblAreaNegocioDefinition.Entry.TABLE_NAME;
        query += " ("+TblAreaNegocioDefinition.Entry.ARN_ID+" INTEGER PRIMARY KEY NOT NULL,";
        query += TblAreaNegocioDefinition.Entry.ARN_NOMBRE+ " TEXT NOT NULL,";
        query += TblAreaNegocioDefinition.Entry.ACTIVO+ " INTEGER NOT NULL,";
        query += "UNIQUE ("+TblAreaNegocioDefinition.Entry.ARN_ID+"))";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        db.insert(TblAreaNegocioDefinition.Entry.TABLE_NAME, null, values);
    }

    public void update(Integer id, ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        String where = TblAreaNegocioDefinition.Entry.ARN_ID + " = "+ id;
        db.update(TblAreaNegocioDefinition.Entry.TABLE_NAME, values, where, null);
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {"ARN_ID", "ARN_NOMBRE", "ACTIVO"};
        Cursor cursor = db.query(TblAreaNegocioDefinition.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }
}
