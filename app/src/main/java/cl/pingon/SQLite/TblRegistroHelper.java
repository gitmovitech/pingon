package cl.pingon.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TblRegistroHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = TblRegistroDefinition.Entry.TABLE_NAME+".db";

    public TblRegistroHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TblRegistroDefinition.Entry.TABLE_NAME;
        query += " ("+ TblRegistroDefinition.Entry.DOC_ID+" INTEGER NOT NULL,";
        query += TblRegistroDefinition.Entry.CAM_ID+" INTEGER NOT NULL,";
        query += TblRegistroDefinition.Entry.FRM_ID+" INTEGER NOT NULL,";
        query += TblRegistroDefinition.Entry.REG_ID+" INTEGER NOT NULL,";
        query += TblRegistroDefinition.Entry.REG_TIPO+" TEXT NOT NULL,";
        query += TblRegistroDefinition.Entry.REG_VALOR+" TEXT NOT NULL,";
        query += TblRegistroDefinition.Entry.REG_METADATOS+ " INTEGER NOT NULL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        db.insert(TblRegistroDefinition.Entry.TABLE_NAME, null, values);
    }

    public void update(Integer id, ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        String where = TblRegistroDefinition.Entry.DOC_ID + " = "+ id;
        db.update(TblRegistroDefinition.Entry.TABLE_NAME, values, where, null);
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "DOC_ID",
                "CAM_ID",
                "FRM_ID",
                "REG_ID",
                "REG_TIPO",
                "REG_VALOR",
                "REG_METADATOS"
        };
        Cursor cursor = db.query(TblRegistroDefinition.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }

    public Cursor getById(int ID){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "DOC_ID",
                "CAM_ID",
                "FRM_ID",
                "REG_ID",
                "REG_TIPO",
                "REG_VALOR",
                "REG_METADATOS"
        };
        Cursor cursor = db.query(TblRegistroDefinition.Entry.TABLE_NAME, projection, "DOC_ID = ?", new String[]{String.valueOf(ID)}, null, null, null);
        return cursor;
    }
}
