package com.example.carertrackingapplication.helperClass;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GoogleDirectionAPIFetchURL extends AsyncTask<String, Void, String> {
    Context context;
    String TravelMode = "driving";

    public GoogleDirectionAPIFetchURL(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... str) {
        // Store data from web service
        String data = "";
        TravelMode = str[1];
        try {
            // Fetching the data from web service
            data = getTheUrl(str[0]);
            Log.d("mylog", "Task in background" + data.toString());
        } catch (Exception e) {
            Log.d("Task in Background", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        GoogleDirectionAPIPointsParser parserTask = new GoogleDirectionAPIPointsParser(context, TravelMode);
        parserTask.execute(s);// Call thread to parse JSON data
    }

    private String getTheUrl(String stringUrl) throws IOException {
        String data = "";
        InputStream myInputStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(stringUrl);
            // start http connection to communicate with the url
            urlConnection = (HttpURLConnection) url.openConnection();
            // connect to url
            urlConnection.connect();
            // read data from url
            myInputStream = urlConnection.getInputStream();
            BufferedReader bufRea = new BufferedReader(new InputStreamReader(myInputStream));
            StringBuffer strBuf = new StringBuffer();
            String line = "";
            while ((line = bufRea.readLine()) != null) {
                strBuf.append(line);
            }
            data = strBuf.toString();
            Log.d("myfetchURLLogging", "URL has been downloaded successfully: " + data.toString());
            bufRea.close();
        } catch (Exception e) {
            Log.d("myfetchURLLogging", "Exception was caught while downloading URL: " + e.toString());
        } finally {
            //ends it when finished
            myInputStream.close();
            urlConnection.disconnect();
        }
        return data; //return url
    }
}
