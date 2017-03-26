package cl.pingon.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class TblEmpCompanyHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = TblEmpCompanyDefinition.Entry.TABLE_NAME+".db";

    public TblEmpCompanyHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TblEmpCompanyDefinition.Entry.TABLE_NAME;
        query += " ("+TblEmpCompanyDefinition.Entry.ID+" INTEGER NOT NULL,";
        query += TblEmpCompanyDefinition.Entry.NAME+ " TEXT NOT NULL,";
        query += TblEmpCompanyDefinition.Entry.RUT+ " TEXT NOT NULL )";
        //query += "UNIQUE ("+TblEmpCompanyDefinition.Entry.ID+"))";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        db.insert(TblEmpCompanyDefinition.Entry.TABLE_NAME, null, values);
    }

    public void update(Integer id, ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        String where = TblEmpCompanyDefinition.Entry.ID + " = "+ id;
        db.update(TblEmpCompanyDefinition.Entry.TABLE_NAME, values, where, null);
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {"ID", "NAME", "RUT"};
        Cursor cursor = db.query(TblEmpCompanyDefinition.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }
}
