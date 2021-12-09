package com.example.carertrackingapplication.helperClass;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GoogleDirectionAPIPointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    CallbackOnTaskDone taskCallback;
    String TravelMode = "driving"; //set as default

    public GoogleDirectionAPIPointsParser(Context context, String directionMode) {
        this.taskCallback = (CallbackOnTaskDone) context;
        this.TravelMode = directionMode;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            Log.d("apiPointsParserLog", jsonData[0].toString());
            GoogleDirectionAPIDataParser parser = new GoogleDirectionAPIDataParser();
            Log.d("apiPointsParserLog", parser.toString());


            // Starts parsing data
            routes = parser.parse(jObject);
            Log.d("apiPointsParserLog", "Executing routes!");
            Log.d("apiPointsParserLog", routes.toString());



        } catch (Exception e) {
            Log.d("apiPointsParserLog", e.toString());
            e.printStackTrace();
        }
        return routes;
    }

    //parse process then execute inside of UI thread
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions polyLineSettings = null;
        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            polyLineSettings = new PolylineOptions();
            // Fetches i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetch all points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }

            // Adds all points in route to polyLineSettings
            polyLineSettings.addAll(points);
            if (TravelMode.equalsIgnoreCase("walking")) {
                polyLineSettings.width(10);
                polyLineSettings.color(Color.MAGENTA);
            } else {
                polyLineSettings.width(20);
                polyLineSettings.color(Color.BLUE);
            }
            Log.d("apiPointsParserLog", "polyLineSettings decoded in onPostExe");
        }

        // Drawing polyline in the Google Map for the i-th route
        if (polyLineSettings != null) {
            //mMap.addPolyline(polyLineSettings);
            taskCallback.onTaskFinished(polyLineSettings);

        } else {
            Log.d("apiPointsParserLog", "none polylines are drawn");
        }
    }
}