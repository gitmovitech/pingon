package cl.pingon.Sync;

import android.content.Context;
import android.database.Cursor;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.pingon.Libraries.RESTService;
import cl.pingon.SQLite.TblDocumentoDefinition;

public class SyncDocumentosRegistros {

    RESTService REST;
    String URL;
    Context context;
    JSONObject params;
    String token;

    public SyncDocumentosRegistros(Context context, String url){
        this.URL = url;
        this.context = context;
        REST = new RESTService(context);
    }

    public void addToken(String token){
        this.token = token;
    }

    public void addData(JSONArray params){
        this.params = new JSONObject();
        try {
            this.params.put("token", this.token);
            this.params.put("data", params);
        } catch (JSONException e){

        }
    }

    public void post(Response.Listener<JSONObject> jsonListener, Response.ErrorListener errorListener){
        REST.post(URL, params, jsonListener, errorListener);
    }
}
