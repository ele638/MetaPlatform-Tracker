package metaplatform.ele638.tracker.feature;
import android.graphics.Color;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    Integer number;
    String background_color;


    public Task(JSONObject jsonObject){
        try {
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject system = jsonObject.getJSONObject("system");
            this.id = system.getLong("id");
            this.background_color = system.isNull("color") ? "" : system.getString("color");
            this.number = data.isNull("NUMBER") ? null : data.getJSONObject("NUMBER").getInt("value");
            this.name = data.getJSONObject("NAME").getString("value");
            this.status = data.getJSONObject("A$STATUSID").getString("displayValue");
            this.category = data.getJSONObject("CATID").getString("displayValue");
            this.data_plan = data.isNull("DATE_PLAN") ? "" : data.getJSONObject("DATE_PLAN").getString("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        //TODO добавить спраочник цветов статусов
        return (this.background_color.isEmpty() ? 0 : Color.parseColor(this.background_color));
    }
}
