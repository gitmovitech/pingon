package cl.pingon.Sync;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cl.pingon.Libraries.RESTService;
import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblRegistroDefinition;

public class SyncDocumentos {

    RESTService REST;
    String URL;
    Integer LOCAL_DOC_ID;
    Context context;
    JSONObject params;

    public SyncDocumentos(Context context, String url, int LOCAL_DOC_ID){
        this.URL = url;
        this.LOCAL_DOC_ID = LOCAL_DOC_ID;
        this.context = context;
        REST = new RESTService(context);
    }

    public void AddData(Cursor c){
        c.moveToFirst();
        params = new JSONObject();
        try {
            params.put(TblDocumentoDefinition.Entry.USU_ID, c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.USU_ID)));
            params.put(TblDocumentoDefinition.Entry.FRM_ID, c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.FRM_ID)));
            params.put(TblDocumentoDefinition.Entry.DOC_FECHA_CREACION, c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_FECHA_CREACION)));
            params.put(TblDocumentoDefinition.Entry.DOC_FECHA_MODIFICACION, c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_FECHA_MODIFICACION)));
            params.put(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO, c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO)));
            params.put(TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO, c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO)));
            params.put(TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE, c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE)));
            params.put(TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE, c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE)));
            params.put(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE, c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE)));
            params.put(TblDocumentoDefinition.Entry.DOC_EXT_OBRA, c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_OBRA)));
            params.put(TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO, c.getString(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO)));
        } catch(Exception e){}
        c.close();
    }

    public void Post(Response.Listener<JSONObject> jsonListener, Response.ErrorListener errorListener){
        REST.post(URL, params, jsonListener, errorListener);
    }

}
