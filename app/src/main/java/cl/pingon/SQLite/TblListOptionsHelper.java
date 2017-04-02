package cl.pingon.SQLite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TblListOptionsHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = TblListOptionsDefinition.Entry.TABLE_NAME+".db";

    public TblListOptionsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ TblListOptionsDefinition.Entry.TABLE_NAME;
        query += " ("+TblListOptionsDefinition.Entry.FRM_ID+" INTEGER NOT NULL,";
        query += TblListOptionsDefinition.Entry.OPC_ID+" INTEGER NOT NULL,";
        query += TblListOptionsDefinition.Entry.CAM_ID+" INTEGER NOT NULL,";
        query += TblListOptionsDefinition.Entry.OPC_VALOR+ " TEXT NOT NULL,";
        query += TblListOptionsDefinition.Entry.OPC_NOMBRE+ " TEXT NOT NULL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        db.insert(TblListOptionsDefinition.Entry.TABLE_NAME, null, values);
    }

    public void update(Integer id, ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        String where = TblListOptionsDefinition.Entry.CAM_ID + " = "+ id;
        db.update(TblListOptionsDefinition.Entry.TABLE_NAME, values, where, null);
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "FRM_ID",
                "OPC_ID",
                "CAM_ID",
                "OPC_VALOR",
                "OPC_NOMBRE"
        };
        Cursor cursor = db.query(TblListOptionsDefinition.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }
}
