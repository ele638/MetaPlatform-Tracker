package metaplatform.ele638.tracker.feature;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ele638 on 23/04/2018.
 * ele638@gmail.com
 */

public class Time implements Comparable<Time>{
    Long id;
    Integer minutes;
    Double hours;
    Long linkedTaskId;
    String taskName;
    String displayDate;
    String comment;

    public Time(JSONObject jsonObject){
        if (jsonObject != null) {
            try {
                JSONObject data = jsonObject.getJSONObject("data");
                JSONObject system = jsonObject.getJSONObject("system");
                this.id = system.getLong("id");
                if (!(data.isNull("ISSUEID")))
                    this.taskName = data.getJSONObject("ISSUEID").optString("displayValue");
                if (!(data.isNull("ISSUEID")))
                    this.linkedTaskId = data.getJSONObject("ISSUEID").optLong("value");
                if (!(data.isNull("COMNT")))
                    this.comment = data.getJSONObject("COMNT").optString("value");
                if (!(data.isNull("MINS")))
                    this.minutes = data.getJSONObject("MINS").optInt("value");
                if (!(data.isNull("HOURS")))
                    this.hours = data.getJSONObject("HOURS").optDouble("value");
                if (!(data.isNull("DATA")))
                    this.displayDate = data.getJSONObject("DATA").optString("value");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Time> arrayFromJson(JSONArray array){
        List<Time> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Time time = null;
            try {
                time = new Time(array.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            list.add(time);
        }
        return list;
    }

    @Override
    public int compareTo(@NonNull Time o) {
        return o.id.compareTo(id);
    }
}
