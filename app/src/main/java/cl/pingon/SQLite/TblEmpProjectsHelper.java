package cl.pingon.SQLite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TblEmpProjectsHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = TblEmpProjectsDefinition.Entry.TABLE_NAME+".db";

    public TblEmpProjectsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TblEmpProjectsDefinition.Entry.TABLE_NAME;
        query += " ("+TblEmpProjectsDefinition.Entry.ID+" INTEGER NOT NULL,";
        query += TblEmpProjectsDefinition.Entry.NAME+ " TEXT NOT NULL,";
        query += TblEmpProjectsDefinition.Entry.COORDINATES+ " TEXT NOT NULL,";
        query += TblEmpProjectsDefinition.Entry.ADDRESS+ " TEXT NOT NULL,";
        query += TblEmpProjectsDefinition.Entry.COMPANY_ID+ " INTEGER NOT NULL)";
        //query += "UNIQUE ("+TblEmpProjectsDefinition.Entry.ID+"))";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void deleteAll(){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(TblEmpProjectsDefinition.Entry.TABLE_NAME, null, null);
        db.close();
    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        //Log.w("VALUES",values.get("id").toString());
        try {
            db.insert(TblEmpProjectsDefinition.Entry.TABLE_NAME, null, values);
        } catch (Exception e){

        }
    }

    public void update(Integer id, ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        String where = TblEmpProjectsDefinition.Entry.ID + " = "+ id;
        db.update(TblEmpProjectsDefinition.Entry.TABLE_NAME, values, where, null);
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {"ID", "NAME", "COORDINATES", "ADDRESS", "COMPANY_ID"};
        Cursor cursor = db.query(TblEmpProjectsDefinition.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }

    public Cursor getByCompanyId(int ID){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {"ID", "NAME", "COORDINATES", "ADDRESS", "COMPANY_ID"};
        Cursor cursor = db.query(TblEmpProjectsDefinition.Entry.TABLE_NAME, projection, "COMPANY_ID = ?", new String[]{String.valueOf(ID)}, null, null, null);
        return cursor;
    }

}
