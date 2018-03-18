package metaplatform.ele638.tracker.feature;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ele638 on 24/02/2018.
 */

public class TaskList extends Fragment {

    VolleySingleton volleySingleton;
    Map<Long, String> users;
    List<Task> tasks, tasks_filtered;
    final String LOG_TAG = "TASKLIST ";
    RadioGroup.OnCheckedChangeListener toggleListner;
    static ProgressDialog progress;
    TaskAdapter taskAdapter;
    SwipeRefreshLayout refreshLayout;
    RadioGroup radioGroup;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        View out_view = inflater.inflate(R.layout.tasklist, null);
        final RecyclerView taskRecyclerView = out_view.findViewById(R.id.taskRecyclerView);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        taskRecyclerView.setLayoutManager(layoutManager);
        refreshLayout = out_view.findViewById(R.id.swipeContainer);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                taskAdapter.clear();
                getTasks();
                taskAdapter.notifyDataSetChanged();
                radioGroup.clearCheck();
            }
        });
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        tasks = new ArrayList<>();
        tasks_filtered = new ArrayList<>();
        taskAdapter = new TaskAdapter();
        taskRecyclerView.setAdapter(taskAdapter);

        radioGroup = out_view.findViewById(R.id.tasksRadioGroup);
        toggleListner = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId ==  R.id.radioInWork)
                    taskAdapter.getFilter().filter("В работе");
                if (checkedId ==  R.id.radioNew)
                    taskAdapter.getFilter().filter("Новый");
                if (checkedId ==  R.id.radioPause)
                    taskAdapter.getFilter().filter("Приостановлена");
                if (checkedId == -1)
                    taskAdapter.getFilter().filter("");
            }

        };
        radioGroup.setOnCheckedChangeListener(toggleListner);
        Button btn = out_view.findViewById(R.id.radioReset);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroup.clearCheck();
            }
        });
        volleySingleton = VolleySingleton.getInstance(getContext());

        progress = new ProgressDialog(getContext());
        progress.setTitle("Загрузка");
        progress.setMessage("Обновление данных с сервера");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog

        FloatingActionButton fab = out_view.findViewById(R.id.TaskListAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TaskEditActivity.class);
                startActivity(intent);
            }
        });

        getTasks();

        return out_view;
    }

    public void getTasks(){
        volleySingleton.getTasks(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("code") == 200) {
                                tasks = Task.arrayFromJson(response.getJSONArray("body"));
                                tasks_filtered = tasks;
                                taskAdapter.notifyDataSetChanged();
                                progress.dismiss();
                                refreshLayout.setRefreshing(false);
                                Log.d(LOG_TAG + "getTasks: ",response.toString());
                            } else {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);
                                Log.d(LOG_TAG + "getTasks: ",response.toString());
                                progress.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            progress.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG + "getTasks: ", error.toString());
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        progress.dismiss();
                    }
                });
    }


    class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements Filterable {

        @Override
        public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.task_card, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public int getItemCount() {
            return tasks_filtered.size();
        }

        public void clear(){
            tasks_filtered.clear();
            tasks.clear();
            notifyDataSetChanged();
        }


        @Override
        public void onBindViewHolder(TaskAdapter.ViewHolder holder, final int position) {
            final Task element = tasks_filtered.get(position);
            if (element != null) {
                holder.taskNumber.setText(element.number.toString());
                holder.taskTitle.setText(element.name);
                holder.taskStatus.setText(element.status);
                holder.taskCategory.setText(element.category);
                holder.taskDataPlan.setText(element.data_plan);
                int color = Color.TRANSPARENT;
                Drawable background = holder.itemView.findViewById(R.id.taskCardLayout).getBackground();
                if (background instanceof ColorDrawable)
                    color = ((ColorDrawable) background).getColor();
                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{element.getColor() == 0 ? color : element.getColor(), color});
                gd.setCornerRadius(0f);

                holder.itemView.findViewById(R.id.taskCardLayout).setBackground(gd);

            }
            holder.itemView.findViewById(R.id.taskCardView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    volleySingleton.getTaskDetail(element.id, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            TaskList.progress.setMessage(element.name);
                            TaskList.progress.show();
                            try {
                                Log.d(LOG_TAG + "getSelTask: ",response.toString());
                                if (response.getString("code").equals("200")){
                                    Intent intent = new Intent(getContext(), TaskActivity.class);
                                    intent.putExtra("response", response.getJSONObject("body").toString());
                                    intent.putExtra("id", response.getJSONObject("body").getJSONObject("system").getLong("id"));
                                    startActivity(intent);

                                }else{
                                    TaskList.progress.dismiss();
                                    Toast.makeText(getContext(), "Error loading, code "+response.getString("code"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                TaskList.progress.dismiss();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //Toast.makeText(getContext(), element.name, Toast.LENGTH_SHORT).show();
                    //TODO Добавить открытие карточки задачи с деталями https://www.youtube.com/watch?v=nA2Axt5LjKQ
                }
            });
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView taskNumber, taskTitle, taskStatus, taskCategory, taskDataPlan;

            public ViewHolder(View itemView) {
                super(itemView);
                taskNumber = itemView.findViewById(R.id.taskNumber);
                taskTitle = itemView.findViewById(R.id.taskTitle);
                taskStatus = itemView.findViewById(R.id.taskStatus);
                taskCategory = itemView.findViewById(R.id.taskCategory);
                taskDataPlan = itemView.findViewById(R.id.taskDatePlan);
            }
        }


        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    ArrayList<Task> filtered = new ArrayList<Task>();
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i<tasks.size(); i++){
                        if (tasks.get(i).status.toLowerCase().contentEquals(constraint) || constraint.toString().isEmpty()){
                            filtered.add(tasks.get(i));
                        }
                    }
                    results.count = filtered.size();
                    results.values = filtered;
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    tasks_filtered = (List<Task>) results.values;
                    notifyDataSetChanged();
                }
            };
            return filter;
        }

    }

}
