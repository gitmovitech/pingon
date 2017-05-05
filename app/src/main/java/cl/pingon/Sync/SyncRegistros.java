package cl.pingon.Sync;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import cl.pingon.Libraries.RESTService;
import cl.pingon.SQLite.TblRegistroDefinition;

public class SyncRegistros {

    RESTService REST;
    String URL;
    Integer LOCAL_DOC_ID;
    Context context;

    public SyncRegistros(Context context, String url, int LOCAL_DOC_ID){
        this.URL = url;
        this.LOCAL_DOC_ID = LOCAL_DOC_ID;
        this.context = context;
        REST = new RESTService(context);
    }

    public void SyncData(Cursor c){
        //TODO Recorrer el cursor y subirlo al servidor
        Map<String,String> params = new HashMap<String, String>();
        params.put(TblRegistroDefinition.Entry.CAM_ID, c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.CAM_ID)));
        params.put(TblRegistroDefinition.Entry.CHK_ID, c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.CHK_ID)));
        params.put(TblRegistroDefinition.Entry.FRM_ID, c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.FRM_ID)));
        params.put(TblRegistroDefinition.Entry.REG_TIPO, c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO)));
        params.put(TblRegistroDefinition.Entry.REG_VALOR, c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)));
    }
}
