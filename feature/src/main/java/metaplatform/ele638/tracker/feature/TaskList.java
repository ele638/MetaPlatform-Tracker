package metaplatform.ele638.tracker.feature;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by ele638 on 24/02/2018.
 */

public class TaskList extends Fragment implements AdapterView.OnItemClickListener {

    VolleySingleton volleySingleton;
    Map<Long, String> users;
    List<Task> tasks;
    final String LOG_TAG = "TASKLIST ";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        View out_view = inflater.inflate(R.layout.tasklist, null);
        final RecyclerView taskRecyclerView = out_view.findViewById(R.id.taskRecyclerView);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        taskRecyclerView.setLayoutManager(layoutManager);

        tasks = new ArrayList<>();
        final TaskAdapter taskAdapter = new TaskAdapter();

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
                            if (response.getInt("code") == 200) {
                                tasks = Task.arrayFromJson(response.getJSONArray("body"));
                                taskRecyclerView.setAdapter(taskAdapter);
                                taskAdapter.notifyDataSetChanged();
                                Log.d(LOG_TAG + "getTasks: ",response.toString());
                            } else {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);
                                Log.d(LOG_TAG + "getTasks: ",response.toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG + "getTasks: ", error.toString());
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                });
        return out_view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }


    class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

        @Override
        public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.task_card, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        @Override
        public void onBindViewHolder(TaskAdapter.ViewHolder holder, final int position) {
            Task element = tasks.get(position);
            if (element != null) {
                holder.taskNumber.setText(element.number.toString());
                holder.taskTitle.setText(element.name);
                holder.taskStatus.setText(element.status);
                holder.taskCategory.setText(element.category);
                holder.taskDataPlan.setText(element.data_plan);
                if (tasks.get(position).background_color != null) {
                    int color = Color.TRANSPARENT;
                    Drawable background = holder.itemView.findViewById(R.id.taskCardLayout).getBackground();
                    if (background instanceof ColorDrawable)
                        color = ((ColorDrawable) background).getColor();
                    GradientDrawable gd = new GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            new int[]{Color.parseColor(element.background_color), color});
                    gd.setCornerRadius(0f);

                    holder.itemView.findViewById(R.id.taskCardLayout).setBackground(gd);
                }
            }
            holder.itemView.findViewById(R.id.taskCardView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskDetail detail = new TaskDetail();
                    detail.setTask(tasks.get(position));
                    detail.show(getFragmentManager(), "detail1");
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
}
