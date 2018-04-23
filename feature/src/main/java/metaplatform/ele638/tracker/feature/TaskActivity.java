package metaplatform.ele638.tracker.feature;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TaskActivity extends AppCompatActivity {

    JSONObject data, statuses;
    TextView taskCaption, taskCategory, taskProject,
            taskPlanDate, taskWaitingDate;
    RecyclerView bottomBar;
    LinearLayoutManager linearLayout;
    VolleySingleton volleySingleton;
    BottomBarAdapter adapter;
    ProgressDialog dialog;
    Task currentTask;
    Button timeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TaskEditActivity.class);
                intent.putExtra("taskObj", getIntent().getStringExtra("response"));
                startActivityForResult(intent, 0);
            }
        });
        timeButton = findViewById(R.id.timeButton);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        volleySingleton = VolleySingleton.getInstance(this);
        setData(getIntent());

        TaskList.progress.dismiss();
    }

    public void setData(Intent intent){
        volleySingleton = VolleySingleton.getInstance(this);
        taskCaption = findViewById(R.id.DtaskCaption);
        taskCategory = findViewById(R.id.DTaskCategory);
        taskProject = findViewById(R.id.DTaskProject);
        taskPlanDate = findViewById(R.id.DTaskPlanDate);
        taskWaitingDate = findViewById(R.id.DTaskWaitingDate);
        if (intent.getStringExtra("response").isEmpty()) {
            volleySingleton.getTaskDetail(getIntent().getLongExtra("id", 0), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("code").equals("200")) {
                            Log.d("test", response.toString());
                            currentTask = new Task(response.getJSONObject("body"));
                            updateViews();
                            getStatuses();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
        }else{
            try {
                currentTask = new Task(new JSONObject(intent.getStringExtra("response")));
                updateViews();
                getStatuses();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void updateViews(){
        bottomBar = findViewById(R.id.DbottomBar);
        linearLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        bottomBar.setLayoutManager(linearLayout);
        adapter = new BottomBarAdapter();
        bottomBar.setAdapter(adapter);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Загрузка");
        dialog.setMessage("Обновление данных с сервера");
        dialog.setCancelable(false); // disable dismiss by tapping outside of the dialog


        getSupportActionBar().setTitle(currentTask.number + " - " + currentTask.name);

        taskCaption.setText(Html.fromHtml(currentTask.description));
        taskCategory.setText(currentTask.category);
        taskProject.setText(currentTask.project);
        taskPlanDate.setText(currentTask.data_plan);
        taskWaitingDate.setText(currentTask.waiting_date);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(currentTask.background_color)));
        findViewById(R.id.toolbar_layout).setBackgroundColor(Color.parseColor(currentTask.background_color));
        getWindow().setStatusBarColor(Color.parseColor(currentTask.statusbar_color));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            setData(data);
        }
    }

    public void getStatuses() {
        volleySingleton.getStatuses(currentTask.id, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("code").equals("200")) {
                        adapter.statuses = response.getJSONArray("body");
                        adapter.recreateMap();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public void setMessageDialog(String message) {
        dialog.setMessage(message);
    }

    class BottomBarAdapter extends RecyclerView.Adapter<BottomBarAdapter.ViewHolder> {

        JSONArray statuses;
        ArrayList<HashMap<String, String>> map;

        public BottomBarAdapter() {
            map = new ArrayList<>(0);
        }

        public void recreateMap() {
            map = new ArrayList<>(statuses.length());
            for (int i = 0; i < statuses.length(); i++) {
                HashMap<String, String> hashMapmap = new HashMap(0);
                try {
                    Iterator keys = statuses.getJSONObject(i).keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        hashMapmap.put(key, statuses.getJSONObject(i).getString(key));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                map.add(hashMapmap);
            }
            Log.d("test", "map = " + map.toString());
            notifyDataSetChanged();
        }

        @Override
        public BottomBarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.status_textview, null, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(BottomBarAdapter.ViewHolder holder, int position) {
            holder.status.setText((String) map.get(position).get("name"));
            holder.id = Long.parseLong((String) map.get(position).get("bfId"));
        }

        @Override
        public int getItemCount() {
            return map.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            Button status;
            Long id;

            public ViewHolder(View itemView) {
                super(itemView);
                this.status = itemView.findViewById(R.id.statusButton);
                status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setMessageDialog("Установка статуса " + status.getText().toString());
                        showDialog();
                        volleySingleton.setStatus(id, currentTask.id, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("code").equals("200")) {
                                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                        getStatuses();
                                        dismissDialog();
                                    } else {
                                        Log.d("setStatus", response.toString());
                                        dismissDialog();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    dismissDialog();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });

                    }
                });
            }
        }
    }
}
