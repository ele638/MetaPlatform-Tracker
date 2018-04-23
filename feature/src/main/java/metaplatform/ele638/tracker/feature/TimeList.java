package metaplatform.ele638.tracker.feature;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by ele638 on 24/02/2018.
 */

public class TimeList extends Fragment implements View.OnClickListener {

    Button startButton, endButton, searchButton;
    TextView progressText;
    ProgressBar progressBar;
    VolleySingleton volleySingleton;
    DateFormat df;
    static ProgressDialog pd;
    List<Time> timeList;
    Double summary;
    TimeAdapter timeAdapter;
    RecyclerView timeRV;
    SwipeRefreshLayout refreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View out_view = inflater.inflate(R.layout.timelist, null);
        startButton = out_view.findViewById(R.id.timeStartDate);
        endButton = out_view.findViewById(R.id.timeEndDate);
        searchButton = out_view.findViewById(R.id.timeSearch);
        progressBar = out_view.findViewById(R.id.timeProgressBar);
        progressText = out_view.findViewById(R.id.timeFilledDetailText);
        startButton.setOnClickListener(this);
        endButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        timeRV = out_view.findViewById(R.id.timeRecyclerView);
        refreshLayout = out_view.findViewById(R.id.timeSwipeContainer);
        volleySingleton = VolleySingleton.getInstance(getContext());

        progressBar.setProgress(0);
        progressText.setText("");


        Calendar cal = GregorianCalendar.getInstance(Locale.FRENCH);

        df = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        startButton.setText(df.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        endButton.setText(df.format(cal.getTime()));



        timeList = new ArrayList<>();
        timeAdapter = new TimeAdapter();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        timeRV.setAdapter(timeAdapter);
        timeRV.setLayoutManager(layoutManager);
        pd = new ProgressDialog(getContext());
        pd.setTitle("Загрузка данных");
        pd.setMessage("Загрузка периода...");

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pd.show();
                getTimes();
            }
        });

        pd.show();
        getTimes();
        return out_view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v == startButton || v == endButton) {
            final Button myButton = (Button) v;
            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext());
            datePickerDialog.getDatePicker().setFirstDayOfWeek(2);
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    myButton.setText(String.format("%4d-%02d-%02d", year, month + 1, dayOfMonth));
                    if (myButton == startButton && !(endButton.getText().toString().isEmpty())) {
                        endButton.setText("");
                    }
                }
            });
            if (!startButton.getText().toString().isEmpty() && myButton == endButton) {
                long minVal = Date.valueOf(startButton.getText().toString()).getTime();
                datePickerDialog.getDatePicker().setMinDate(minVal);
            }
            datePickerDialog.show();
        }

        if (v == searchButton) {
            pd.show();
            getTimes();
        }
    }

    private void getTimes() {
        if (!(startButton.getText().toString().isEmpty())) {

            volleySingleton.getTimes(startButton.getText().toString(), endButton.getText().toString(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getInt("code") == 200) {
                            if (response.isNull("body")) {
                                Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                                refreshLayout.setRefreshing(false);
                                return;
                            }
                            summary = Double.valueOf(0);
                            timeList = Time.arrayFromJson(response.getJSONArray("body"));
                            timeAdapter.notifyDataSetChanged();
                            for (Time time : timeList) summary += time.hours;
                            String getTime = endButton.getText().toString();
                            if (!(endButton.getText().toString().isEmpty())) {
                                progressBar.setMax(40);
                                //progressBar.setProgress(summary.intValue());
                                setProgressAnimate(progressBar, summary.intValue());
                                progressText.setText(
                                        getString(R.string.PBCaptionDefault)
                                                .replace("N", summary.toString())
                                                .replace("M", "40.0")
                                                .replace("X", summary / 40.0 * 100 + ""));
                            } else {
                                progressBar.setMax(8);
                                //progressBar.setProgress(summary.intValue());
                                setProgressAnimate(progressBar, summary.intValue());
                                progressText.setText(
                                        getString(R.string.PBCaptionDefault)
                                                .replace("N", summary.toString())
                                                .replace("M", "8.0")
                                                .replace("X", summary / 8.0 * 100 + ""));

                                //Toast.makeText(getContext(), "Success: ", Toast.LENGTH_SHORT).show();
                            }
                            pd.dismiss();
                            refreshLayout.setRefreshing(false);
                        }
                        else{
                            Toast.makeText(getContext(), "ERR: " + response.getInt("code"), Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                            refreshLayout.setRefreshing(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        refreshLayout.setRefreshing(false);
                        pd.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

        }

    }

    class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {

        @Override
        public TimeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.time_card, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Time element = timeList.get(position);
            holder.timeTitle.setText(element.taskName);
            holder.timeDate.setText(element.displayDate);
            holder.timeHours.setText("В часах: " + element.hours);
            holder.timeMinutes.setText("В минутах: " + element.minutes);
        }

        @Override
        public int getItemCount() {
            return timeList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView timeTitle, timeDate, timeHours, timeMinutes;

            public ViewHolder(View itemView) {
                super(itemView);
                timeTitle = itemView.findViewById(R.id.timeTitle);
                timeDate = itemView.findViewById(R.id.timeDate);
                timeHours = itemView.findViewById(R.id.timeHours);
                timeMinutes = itemView.findViewById(R.id.timeMinutes);
            }
        }

    }

    private void setProgressAnimate(ProgressBar pb, int progressTo)
    {
        pb.setMax(pb.getMax()*1000);
        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", 0, progressTo * 1000);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

}
