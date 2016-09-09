package wheely.test.locationfinder.model.dto;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ApiLocation extends BaseLocation {

    @Expose
    private final long id;

    public ApiLocation(double lat, double lon, long id) {
        super(lat, lon);
        this.id = id;
    }

    public static List<ApiLocation> getLocationsListFromJson(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        Type listType = new TypeToken<ArrayList<ApiLocation>>(){}.getType();
        return gson.fromJson(json, listType);
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

    @Override
    public String toString() {
        return "id=" + id + " lat=" + lat + " lon=" + lon;
    }
}
