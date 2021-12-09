package com.example.carertrackingapplication.helperClass;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GoogleDirectionAPIDataParser {
    public List<List<HashMap<String, String>>> parse(JSONObject jsObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jsRoutes;
        JSONArray jsLegs;
        JSONArray jsSteps;
        try {
            jsRoutes = jsObject.getJSONArray("routes");
            //go all routes
            for (int i = 0; i < jsRoutes.length(); i++) {
                jsLegs = ((JSONObject) jsRoutes.get(i)).getJSONArray("legs");
                List myPath = new ArrayList<>();
                //all legs
                for (int j = 0; j < jsLegs.length(); j++) {
                    jsSteps = ((JSONObject) jsLegs.get(j)).getJSONArray("steps");

                    //all steps
                    for (int k = 0; k < jsSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jsSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> polyList = decodePoly(polyline);

                        for (int z = 0; z < polyList.size(); z++) {//Go to each points
                            HashMap<String, String> myPointsHashMap = new HashMap<>();
                            myPointsHashMap.put("lat", Double.toString((polyList.get(z)).latitude));
                            myPointsHashMap.put("lng", Double.toString((polyList.get(z)).longitude));
                            myPath.add(myPointsHashMap);
                        }
                    }
                    routes.add(myPath);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return routes;
    }


    //decode polyline points
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}