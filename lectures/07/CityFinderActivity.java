/*
 * CS 193A, Winter 2015, Marty Stepp
 * This program demonstrates the use of the Google Maps API.
 * Note that the code will not run without a Maps API key.
 * You would have to get your own API key to run it yourself.
 */

package com.example.stepp.cityfinder;

import android.app.*;
import android.content.*;
import android.location.*;
import android.os.*;
import android.widget.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import java.util.*;

public class CityFinderActivity extends Activity
        implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    private LatLng myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_finder);

        MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.the_map);
        mf.getMapAsync(this);                  // calls onMapReady when loaded
    }

    @Override
    public void onMapReady(GoogleMap map) {    // map is loaded but not laid out yet
        this.map = map;
        map.setOnMapLoadedCallback(this);      // calls onMapLoaded when layout done
    }

    @Override
    public void onMapLoaded() {
        // code to run when the map has loaded
        readCities();
        map.setOnMarkerClickListener(this);

        // read user's current location, if possible
        myLocation = getMyLocation();
        if (myLocation == null) {
            Toast.makeText(this, "Unable to access your location. Consider enabling Location in your device's Settings.", Toast.LENGTH_LONG).show();
        } else {
            map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("ME!")
            );
        }
    }

    /*
     * Reads a list of cities from a text file and draws a marker for each one on the map.
     */
    private void readCities() {
        Scanner scan = new Scanner(getResources().openRawResource(R.raw.cities));
        while (scan.hasNextLine()) {
            String name = scan.nextLine();
            if (name.isEmpty()) break;
            double lat = Double.parseDouble(scan.nextLine());
            double lng = Double.parseDouble(scan.nextLine());
            map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(name)
            );
        }
    }

    /*
     * Returns the user's current location as a LatLng object.
     * Returns null if location could not be found (such as in an AVD emulated virtual device).
     */
    private LatLng getMyLocation() {
        // try to get location three ways: GPS, cell/wifi network, and 'passive' mode
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            // fall back to network if GPS is not available
            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (loc == null) {
            // fall back to "passive" location if GPS and network are not available
            loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        if (loc == null) {
            return null;   // could not get user's location
        } else {
            double myLat = loc.getLatitude();
            double myLng = loc.getLongitude();
            return new LatLng(myLat, myLng);
        }
    }

    /*
     * Called when user clicks on any of the city map markers.
     * Adds a line from the user's location to that city.
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (myLocation != null) {
            LatLng markerLatLng = marker.getPosition();
            map.addPolyline(new PolylineOptions()
                            .add(myLocation)
                            .add(markerLatLng)
            );
            return true;
        } else {
            return false;
        }
    }
}
