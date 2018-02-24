package metaplatform.ele638.tracker.feature;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ele638 on 24/02/2018.
 */

public class TaskList extends Fragment {

    VolleySingleton volleySingleton;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        volleySingleton = VolleySingleton.getInstance(getContext());
        volleySingleton.getTasks(
                new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         Log.d("Response", response);
                     }
                 },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response", error.toString());
                    }
                });
        return inflater.inflate(R.layout.tasklist, null);
    }
}
