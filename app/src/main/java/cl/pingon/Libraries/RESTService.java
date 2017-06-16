package cl.pingon.Libraries;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RESTService {

    private static final String TAG = RESTService.class.getSimpleName();

    private final Context contexto;

    public RESTService(Context contexto) {
        this.contexto = contexto;
    }

    /**
     * Servicio GET
     * @param uri
     * @param jsonListener
     * @param errorListener
     * @param cabeceras
     */
    public void get(String uri, Response.Listener<JSONObject> jsonListener,Response.ErrorListener errorListener,final HashMap<String, String> cabeceras) {
        //Log.d("URL SYNC", uri);
        JsonObjectRequest peticion = new JsonObjectRequest(uri,null,jsonListener,errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return cabeceras;
            }
        };
        VolleySingleton.getInstance(contexto).addToRequestQueue(peticion);
    }

    /**
     * Servicio POST
     * @param uri
     * @param params
     * @param jsonListener
     * @param errorListener
     */
    public void post(String uri, JSONObject params, Response.Listener<JSONObject> jsonListener, Response.ErrorListener errorListener){
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, uri, params, jsonListener,errorListener);
        RequestQueue requestQueue = Volley.newRequestQueue(contexto);
        requestQueue.add(stringRequest);
    }


}
