package cl.pingon.SQLite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TblEmpBrandsHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = TblEmpBrandsDefinition.Entry.TABLE_NAME+".db";

    public TblEmpBrandsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TblEmpBrandsDefinition.Entry.TABLE_NAME;
        query += " ("+TblEmpBrandsDefinition.Entry.ID+" INTEGER NOT NULL,";
        query += TblEmpBrandsDefinition.Entry.NAME+ " TEXT NOT NULL,";
        query += TblEmpBrandsDefinition.Entry.PROJECT_ID+ " INTEGER NOT NULL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        db.insert(TblEmpBrandsDefinition.Entry.TABLE_NAME, null, values);
    }

    public void update(Integer id, ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        String where = TblEmpBrandsDefinition.Entry.ID + " = "+ id;
        db.update(TblEmpBrandsDefinition.Entry.TABLE_NAME, values, where, null);
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {"ID", "NAME", "PROJECT_ID"};
        Cursor cursor = db.query(TblEmpBrandsDefinition.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }

    public Cursor getByProjectId(int ID){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {"ID", "NAME", "PROJECT_ID"};
        Cursor cursor = db.query(TblEmpBrandsDefinition.Entry.TABLE_NAME, projection, "PROJECT_ID = ?", new String[]{String.valueOf(ID)}, null, null, null);
        return cursor;
    }
}
