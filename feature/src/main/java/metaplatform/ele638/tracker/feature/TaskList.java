package metaplatform.ele638.tracker.feature;

import android.app.Fragment;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ele638 on 24/02/2018.
 */

public class TaskList extends Fragment implements AdapterView.OnItemClickListener {

    VolleySingleton volleySingleton;
    Map<Long, String> users;
    List<Task> tasks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        View out_view = inflater.inflate(R.layout.tasklist, null);
        final ListView tasksView = out_view.findViewById(R.id.tasklist);
        tasks = new ArrayList<>();
        final TaskAdapter taskAdapter = new TaskAdapter();
        tasksView.setAdapter(taskAdapter);
        tasksView.setOnItemClickListener(this);

        volleySingleton = VolleySingleton.getInstance(getContext());
                // ArrayAdapter.createFromResource(this,
                //users.values().toString(), android.R.layout.simple_spinner_item);
        /* volleySingleton.getUsers(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("code") == 200){
                                JSONArray body = response.getJSONArray("body");
                                Log.d("UserLoadBody", body.toString());
                                for(int i=0; i < body.length(); i++)
                                    users.put(body.getJSONObject(i).getLong("id"),
                                            body.getJSONObject(i).getString("name"));

                            };
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("UsersLoad", error.toString());
                    }
                }
        );
        */
        volleySingleton.getTasks(
                new Response.Listener<JSONObject>() {
                     @Override
                     public void onResponse(JSONObject response) {
                         try {
                             Log.d("getTasks", response.toString());
                             if (response.getInt("code") == 200) {
                                 JSONArray body = response.getJSONArray("body");
                                 for (int i = 0; i < body.length(); i++){
                                     Task task = new Task(body.getJSONObject(i));
                                     tasks.add(task);
                                 }

                                 taskAdapter.notifyDataSetChanged();
                             }
                         }catch (Exception e){
                             e.printStackTrace();
                         }
                     }
                 },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response", error.toString());
                    }
                });
        return out_view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(getContext(), tasks.get(i).id.toString(), Toast.LENGTH_SHORT).show();
    }

    class UsersAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return users.values().toArray().length;
        }

        @Override
        public Object getItem(int i) {
            return users.values().toArray()[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            TextView out = new TextView(getContext());
            out.setText(users.values().toArray()[i].toString());
            //View out =  View.inflate(getContext(), android.R.layout.simple_spinner_item, null);
            //((TextView) out.findViewById(android.R.id.text1)).setText(users.values().toArray()[i].toString());
            return out;
        }
    }

    class TaskAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return tasks.size();
        }

        @Override
        public Object getItem(int i) {
            return tasks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return tasks.get(i).getId();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Task element = tasks.get(i);
            View task_card = LayoutInflater.from(getContext()).inflate(R.layout.task_card, null);
            ((TextView) task_card.findViewById(R.id.taskNumber)).setText(element.number.toString());;
            ((TextView) task_card.findViewById(R.id.taskTitle)).setText(element.name);
            ((TextView) task_card.findViewById(R.id.taskStatus)).setText(element.status);
            ((TextView) task_card.findViewById(R.id.taskCategory)).setText(element.category);
            ((TextView) task_card.findViewById(R.id.taskDatePlan)).setText(element.data_plan);

            if (element.background_color != null) task_card.setBackgroundColor(Color.parseColor(element.background_color));
            return task_card;
        }
    }
}
