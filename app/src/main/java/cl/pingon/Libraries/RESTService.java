package cl.pingon.Libraries;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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

    public void get(String uri, Response.Listener<JSONObject> jsonListener,
                    Response.ErrorListener errorListener,
                    final HashMap<String, String> cabeceras) {

        // Crear petición GET
        JsonObjectRequest peticion = new JsonObjectRequest(
                uri,
                null,
                jsonListener,
                errorListener
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return cabeceras;
            }
        };

        // Añadir petición a la pila
        VolleySingleton.getInstance(contexto).addToRequestQueue(peticion);
    }

    public void login(String uri, String username, String password, Response.Listener<String> jsonListener,
                      Response.ErrorListener errorListener, final HashMap<Object, Object> cabeceras) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, jsonListener, errorListener){
            protected Map<String,String> getLoginParams(String username, String password){
                Log.d("username", username);
                Log.d("password", password);
                Map<String,String> params = new HashMap<String, String>();
                params.put("user",username);
                params.put("pass",password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this.contexto);
        requestQueue.add(stringRequest);
    }


}
