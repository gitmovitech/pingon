package cl.pingon.SQLite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TblEmpProductsHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = TblEmpProductsDefinition.Entry.TABLE_NAME+".db";

    public TblEmpProductsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TblEmpProductsDefinition.Entry.TABLE_NAME;
        query += " ("+TblEmpProductsDefinition.Entry.ID+" INTEGER NOT NULL,";
        query += TblEmpProductsDefinition.Entry.NAME+ " TEXT NOT NULL,";
        query += TblEmpProductsDefinition.Entry.CODE+ " TEXT NOT NULL,";
        query += TblEmpProductsDefinition.Entry.YEAR+ " TEXT NOT NULL)";
        //query += "UNIQUE ("+TblEmpProductsDefinition.Entry.ID+"))";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        //Log.w("VALUES",values.get("id").toString());
        try {
            db.insert(TblEmpProductsDefinition.Entry.TABLE_NAME, null, values);
        } catch (Exception e){

        }
    }

    public void update(Integer id, ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        String where = TblEmpProductsDefinition.Entry.ID + " = "+ id;
        db.update(TblEmpProductsDefinition.Entry.TABLE_NAME, values, where, null);
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {"ID", "NAME", "CODE", "YEAR"};
        Cursor cursor = db.query(TblEmpProductsDefinition.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }
}
