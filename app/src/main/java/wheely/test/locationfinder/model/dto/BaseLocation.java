package wheely.test.locationfinder.model.dto;

import com.google.gson.annotations.Expose;

public class BaseLocation {
    @Expose
    public final double lat;
    @Expose
    public final double lon;

    public BaseLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "lat=" + lat + " lon=" + lon;
    }
}
