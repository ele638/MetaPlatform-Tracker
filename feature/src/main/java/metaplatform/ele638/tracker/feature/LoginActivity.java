package metaplatform.ele638.tracker.feature;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    final String LOG_TAG = "LOGIN ";
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //обработка энтера на поле пароля
        EditText passwordField = findViewById(R.id.passwordEditText);
        passwordField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            login_button(v);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        ////////TESTING
        EditText loginField = findViewById(R.id.loginEditText);
        loginField.setText(preferences.getString("LOGIN", ""));
        passwordField.setText(preferences.getString("PASSWORD", ""));
        if (!loginField.getText().toString().isEmpty() && !passwordField.getText().toString().isEmpty()) {
            login_button(findViewById(R.id.loginButton));
        }

        ////////TESTING
    }

    public void login_button(View view) {
        EditText loginField = findViewById(R.id.loginEditText);
        EditText passwordField = findViewById(R.id.passwordEditText);
        ProgressBar bar = findViewById(R.id.progressBar);
        loginField.setEnabled(false);
        passwordField.setEnabled(false);
        bar.setVisibility(View.VISIBLE);

        login_func(loginField.getText().toString(), passwordField.getText().toString());
    }

    private void login_func(final String login, final String password){
        final Context ctx = getApplicationContext();
        final VolleySingleton volleySingleton = VolleySingleton.getInstance(getApplicationContext());

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(LOG_TAG+"Success: ", response);
                JsonObject object = (new JsonParser()).parse(response).getAsJsonObject();
                if(object.get("code").getAsInt() == 200){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("LOGIN", login);
                    editor.putString("PASSWORD", password);
                    editor.putString("USERNAME", object.get("body").getAsJsonObject().get("name").getAsString());
                    editor.putLong("USERID", object.get("body").getAsJsonObject().get("userId").getAsLong());

                    //editor.putString("JSESSIONID", );
                    editor.apply();
                    Intent intent = new Intent(ctx, MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(ctx, object.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    Log.d(LOG_TAG+"ERR. Response: ", response);
                    relaunch();
                }
            }

        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG+"Error_listner: ", error.toString());
                Toast.makeText(ctx, "Ошибка подключения", Toast.LENGTH_SHORT).show();
                relaunch();
            }
        };

        volleySingleton.login(login, password, listener, errorListener);
    }

    public void relaunch(){
        EditText loginField = findViewById(R.id.loginEditText);
        EditText passwordField = findViewById(R.id.passwordEditText);
        ProgressBar bar = findViewById(R.id.progressBar);
        loginField.setEnabled(true);
        passwordField.setEnabled(true);
        bar.setVisibility(View.INVISIBLE);
    }
}
