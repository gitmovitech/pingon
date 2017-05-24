package cl.pingon.Sync;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cl.pingon.Libraries.RESTService;
import cl.pingon.SQLite.TblRegistroDefinition;

public class SyncRegistros {

    RESTService REST;
    String URL;
    Integer LOCAL_DOC_ID;
    Integer DOC_ID;
    Context context;
    JSONObject params;
    String token;

    public SyncRegistros(Context context, String url, int LOCAL_DOC_ID, int DOC_ID){
        this.URL = url;
        this.LOCAL_DOC_ID = LOCAL_DOC_ID;
        this.context = context;
        this.DOC_ID = DOC_ID;
        REST = new RESTService(context);
    }

    public void addToken(String token){
        this.token = token;
    }

    public void addData(Cursor c){
        params = new JSONObject();
        try{
            params.put(TblRegistroDefinition.Entry.CAM_ID, c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.CAM_ID)));
            params.put(TblRegistroDefinition.Entry.CHK_ID, c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.CHK_ID)));
            params.put(TblRegistroDefinition.Entry.FRM_ID, c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.FRM_ID)));
            params.put(TblRegistroDefinition.Entry.REG_TIPO, c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_TIPO)));
            params.put(TblRegistroDefinition.Entry.REG_VALOR, c.getString(c.getColumnIndexOrThrow(TblRegistroDefinition.Entry.REG_VALOR)));
            params.put("token", token);
        } catch (Exception e){}
    }

    public void Post(Response.Listener<JSONObject> jsonListener, Response.ErrorListener errorListener){
        REST.post(URL, params, jsonListener, errorListener);
    }
}
