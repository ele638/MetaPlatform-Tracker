package metaplatform.ele638.tracker.feature;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by ele638 on 25/02/2018.
 * ele638@gmail.com
 */

public class Task {
    Long id;
    String name;
    String status;
    String category;
    String data_plan;
    Integer number;
    String background_color;

    public Task(Long id, Integer number, String name, String status, String category, String data_plan){
        this.id = id;
        this.number = number;
        this.name = name;
        this.status = status;
        this.category = category;
        this.data_plan = data_plan;
    }

    public Task(JSONObject jsonObject){
        try {
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject system = jsonObject.getJSONObject("system");
            this.id = system.getLong("id");
            this.number = data.getJSONObject("NUMBER").getInt("value");
            this.name = data.getJSONObject("NAME").getString("value");
            this.status = data.getJSONObject("A$STATUSID").getString("displayValue");
            this.category = data.getJSONObject("CATID").getString("displayValue");
            this.data_plan = data.getJSONObject("DATE_PLAN").getString("value");
            this.background_color = system.getString("color");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Long getId(){
        return this.id;
    }
}
