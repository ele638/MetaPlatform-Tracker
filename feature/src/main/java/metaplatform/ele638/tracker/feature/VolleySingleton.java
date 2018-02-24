package metaplatform.ele638.tracker.feature;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ele638 on 24/02/2018.
 */

public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    private final String SERVER_URL = "https://tracker.metaplatform.ru:8183/";
    private String COOKIE;
    SharedPreferences preferences;

    private VolleySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
        preferences = PreferenceManager.getDefaultSharedPreferences(mCtx);
        //SharedPreferences.Editor editor = preferences.edit();
        COOKIE = preferences.getString("Cookie", "");
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void login(final String login, final String password, Response.Listener<String> listner, Response.ErrorListener errorListener) {
        final String url = SERVER_URL + "core/rest/auth/login";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, listner, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("login", login);
                params.put("password", password);
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Cookie", response.headers.get("Set-Cookie"));
                editor.apply();
                COOKIE = response.headers.get("Set-Cookie");
                return super.parseNetworkResponse(response);
            }
        };

        addToRequestQueue(postRequest);
    }

    public void getTasks(Response.Listener<String> listner, Response.ErrorListener errorListener){
        final String url = SERVER_URL + "core/rest/meta/formGrid/1778116460558";
        StringRequest taskRequest = new StringRequest(Request.Method.GET, url, listner, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Cookie", COOKIE);
                return params;
            }
        };
        addToRequestQueue(taskRequest);
    }
}