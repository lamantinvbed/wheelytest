package wheely.test.locationfinder;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import wheely.test.locationfinder.model.api.Api;
import wheely.test.locationfinder.model.dto.ApiLocation;
import wheely.test.locationfinder.model.dto.UserLocation;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Api api = new Api();
        api.connect("aa", "aa");
//        api.setLocation(new UserLocation(56.698474, 59.238281));
        api.setLocation(UserLocation.fromJSON("{\n" +
                "\n" +
                "\"lat\": 55.373703,\n" +
                "\n" +
                "\"lon\": 37.474764\n" +
                "\n" +
                "}\n"));
        api.getLocations().subscribe(apiLocations -> {
            for(ApiLocation apiLocation : apiLocations) {
                Log.d("LOCATIONS", apiLocation.toString());
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(() -> api.setLocation(new UserLocation(56.698474, 59.238281)), 20000);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
