package metaplatform.ele638.tracker.feature;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.Html;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ele638 on 25/02/2018.
 * ele638@gmail.com
 */

public class Task implements Comparable<Task>{
    Long id;
    String name;
    String status;
    String category;
    String data_plan;
    String description;
    String project;
    String waiting_date;
    Integer number;
    String background_color;
    String statusbar_color;
    HashMap<String, Long> userId;


    public Task(JSONObject jsonObject){
        if (jsonObject != null) {
            try {
                JSONObject data = jsonObject.getJSONObject("data");
                JSONObject system = jsonObject.getJSONObject("system");
                this.id = system.getLong("id");
                this.background_color = statuses_init().get(data.getJSONObject("A$STATUSID").getString("displayValue"));
                this.statusbar_color = statusBar_init().get(data.getJSONObject("A$STATUSID").getString("displayValue"));
                this.number = data.isNull("NUMBER") ? null : data.getJSONObject("NUMBER").getInt("value");
                this.name = data.getJSONObject("NAME").getString("value");
                this.status = data.getJSONObject("A$STATUSID").getString("displayValue");
                this.category = data.getJSONObject("CATID").getString("displayValue");
                this.data_plan = data.isNull("DATE_PLAN") ? "" : data.getJSONObject("DATE_PLAN").getString("value");
                this.description = data.isNull("DESCR") ? "" : data.getJSONObject("DESCR").getString("value");
                this.project = data.isNull("PROJECTID") ? "" : data.getJSONObject("PROJECTID").getString("displayValue");
                this.waiting_date = data.isNull("DATE_WISH") ? "" : data.getJSONObject("DATE_WISH").getString("value");
                if(data.isNull("USERID")){
                    this.userId = null;
                } else{
                    this.userId = new HashMap<>();
                    this.userId.put(data.getJSONObject("USERID").getString("displayValue"), data.getJSONObject("USERID").getLong("value"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static HashMap<String, String> statuses_init(){
        HashMap<String, String> newmap = new HashMap<>(0);
        newmap.put("Новый", "#2196F3");
        newmap.put("Приостановлена", "#BDBDBD");
        newmap.put("В работе", "#8BC34A");
        return newmap;
    }

    public static HashMap<String, String> statusBar_init(){
        HashMap<String, String> newmap = new HashMap<>(0);
        newmap.put("Новый", "#1976D2");
        newmap.put("Приостановлена", "#757575");
        newmap.put("В работе", "#689F38");
        return newmap;
    }

    public Long getId(){
        return this.id;
    }

    @Override
    public int compareTo(@NonNull Task o) {
        return o.id.compareTo(id);
    }

    public static List<Task> arrayFromJson(JSONArray array){
        List<Task> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Task task = null;
            try {
                task = new Task(array.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            list.add(task);
        }
        Collections.sort(list);
        return list;
    }

    public int getColor(){
        return (this.background_color.isEmpty() ? 0 : Color.parseColor(this.background_color));
    }
}
