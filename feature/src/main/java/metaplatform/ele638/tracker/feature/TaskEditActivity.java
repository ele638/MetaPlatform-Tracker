package metaplatform.ele638.tracker.feature;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TaskEditActivity extends AppCompatActivity {

    Task currentTask;
    EditText taskNumber, taskTitle, taskDesc;
    Button saveButton;
    VolleySingleton volleySingleton;
    ProgressDialog dialog;
    Spinner userSpinner;
    ArrayList<String> userNameArray;
    HashMap<String, Long> usersMap;
    Button plan_date, wait_date;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        ctx = this;
        if (getIntent().hasExtra("taskObj")) {
            try {
                currentTask = new Task(new JSONObject(getIntent().getStringExtra("taskObj")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            currentTask = null;
        }
        volleySingleton = VolleySingleton.getInstance(this);
        taskNumber = findViewById(R.id.EtaskNumberEditText);
        taskTitle = findViewById(R.id.EtaskTitleEditText);
        taskDesc = findViewById(R.id.EtaskDescEditText);
        userSpinner = findViewById(R.id.EtaskUserSpinner);
        plan_date = findViewById(R.id.EtaskPlanDate);
        wait_date = findViewById(R.id.EtaskWaitingDate);

        getSupportActionBar().setTitle("Добавить задачу");
        if (currentTask != null) {
            taskNumber.setText(currentTask.number + "");
            taskTitle.setText(currentTask.name);
            taskDesc.setText(currentTask.description);
            plan_date.setText(currentTask.data_plan);
            wait_date.setText(currentTask.waiting_date);
            getSupportActionBar().setTitle("Редактирование");
        }else{
            taskNumber.setHint("Номер присвоится автоматически");
            taskNumber.setEnabled(false);
            taskTitle.requestFocus();
        }

        dialog = new ProgressDialog(this);
        dialog.setTitle("Обновление данных");
        dialog.setMessage("Пожалуйста подождите");

        plan_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // создаем DatePickerDialog и возвращаем его
                Dialog picker = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        plan_date.setText(year + "-"+(month > 10 ? month : "0"+month)+"-"+dayOfMonth);
                        if (currentTask != null)
                            currentTask.data_plan = year + "-"+(month > 10 ? month : "0"+month)+"-"+dayOfMonth;
                    }
                },
                        year, month, day);
                picker.setTitle("Плановая дата");
                picker.show();
            }
        });

        wait_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // создаем DatePickerDialog и возвращаем его
                Dialog picker = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        wait_date.setText(year + "-"+(month > 10 ? month : "0"+month)+"-"+dayOfMonth);
                        if (currentTask != null)
                            currentTask.waiting_date = year + "-"+(month > 10 ? month : "0"+month)+"-"+dayOfMonth;
                    }
                },
                        year, month, day);
                picker.setTitle("Желаемая дата");
                picker.show();
            }
        });

        saveButton = findViewById(R.id.EtaskSaveButton);
        if (currentTask != null) {
            saveButton.setText("Применить изменения");
        } else {
            saveButton.setText("Добавить задачу");
        }

        final Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("code").equals("200")) {
                        Intent intent = new Intent(getBaseContext(), TaskActivity.class);
                        intent.putExtra("response", "");
                        setResult(RESULT_OK, intent);
                        finish();
                        dialog.dismiss();
                        Toast.makeText(getBaseContext(), "Данные обновлены", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getBaseContext(), "Ошибка обновления (" + response.getString("code") + ")", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                }
            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
            }
        };
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                JSONObject request = new JSONObject();
                try {
                    if (!(taskNumber.getText().toString().isEmpty()))
                        request.put("NUMBER", taskNumber.getText().toString());
                    request.put("NAME", taskTitle.getText().toString());
                    request.put("DESCR", taskDesc.getText());
                    request.put("USERID", usersMap.get(userNameArray.get(userSpinner.getSelectedItemPosition())));
                    if (!(plan_date.getText().toString().isEmpty()))
                        request.put("DATE_PLAN", plan_date.getText().toString());
                    if (!(wait_date.getText().toString().isEmpty()))
                        request.put("DATE_WISH", wait_date.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (currentTask != null) {
                    volleySingleton.editObject(currentTask.id, request, listener, errorListener);
                }else{
                    volleySingleton.addObject(Long.valueOf("1778116460558"), request, listener, errorListener);
                }
            }
        });


        volleySingleton.getUsers(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        usersMap = new HashMap<>(0);
                        userNameArray = new ArrayList<>();
                        try {
                            if (response.getInt("code") == 200) {
                                JSONArray body = response.getJSONArray("body");
                                for (int i = 0; i < body.length(); i++) {
                                    userNameArray.add(body.getJSONObject(i).getString("name"));
                                    usersMap.put(body.getJSONObject(i).getString("name"), body.getJSONObject(i).getLong("id"));

                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, userNameArray);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                userSpinner.setAdapter(adapter);
                                if (currentTask != null) {
                                    userSpinner.setSelection(userNameArray.indexOf(currentTask.userId.keySet().iterator().next()));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );


    }

}
