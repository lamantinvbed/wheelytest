package wheely.test.locationfinder.model.dto;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class UserLocation extends BaseLocation {

    public UserLocation(double lat, double lon) {
        super(lat, lon);
    }

    public static UserLocation fromJSON(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        return gson.fromJson(json, UserLocation.class);
    }

    public JSONObject toJson() {
        Gson gson = new Gson();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(gson.toJson(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
