package cl.pingon.SQLite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
        query += TblEmpProductsDefinition.Entry.YEAR+ " TEXT NOT NULL,";
        query += TblEmpProductsDefinition.Entry.BRAND_ID+ " TEXT NOT NULL,";
        query += TblEmpProductsDefinition.Entry.PROJECT_ID+ " INTEGER NOT NULL)";
        //query += "UNIQUE ("+TblEmpProductsDefinition.Entry.ID+"))";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void deleteAll() {
        SQLiteDatabase db = getReadableDatabase();
        db.delete(TblEmpProductsDefinition.Entry.TABLE_NAME, null, null);
        db.close();
    }

    public void insert(ContentValues values) {
        SQLiteDatabase db = getReadableDatabase();
        //Log.w("VALUES",values.get("id").toString());
        try {
            db.insert(TblEmpProductsDefinition.Entry.TABLE_NAME, null, values);
        } catch (Exception e){

        }
    }

    public void update(Integer id, ContentValues values) {
        SQLiteDatabase db = getReadableDatabase();
        String where = TblEmpProductsDefinition.Entry.ID + " = "+ id;
        db.update(TblEmpProductsDefinition.Entry.TABLE_NAME, values, where, null);
    }

    public Cursor getAll() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {"ID", "NAME", "CODE", "YEAR", "BRAND_ID"};
        Cursor cursor = db.query(TblEmpProductsDefinition.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }

    public Cursor getByBrandIdProjectId(int BRAND_ID, int PROJECT_ID) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT ID, NAME, CODE, YEAR, BRAND_ID, PROJECT_ID FROM "+TblEmpProductsDefinition.Entry.TABLE_NAME+" WHERE BRAND_ID = "+BRAND_ID+" AND PROJECT_ID = "+PROJECT_ID+" GROUP BY NAME", null);
        return cursor;
    }

    public Cursor getByProductNameBrandId(String PRODUCT_NAME, int BRAND_ID, int PROJECT_ID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID, NAME, CODE, YEAR, BRAND_ID, PROJECT_ID FROM "+TblEmpProductsDefinition.Entry.TABLE_NAME+" WHERE NAME = '"+PRODUCT_NAME+"' AND BRAND_ID = "+BRAND_ID+" AND PROJECT_ID = "+PROJECT_ID, null);
        return cursor;
    }

}
