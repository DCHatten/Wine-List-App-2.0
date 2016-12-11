package edu.kvcc.cis298.cis298assignment4;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by dhatt_000 on 12/5/2016.
 */

public class BeverageFetcher {

    private static final String TAG = "BeverageFragment";

    private byte[] getUrlBytes(String urlSpec) throws IOException {

        URL url = new URL(urlSpec);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;

            byte[] buffer = new byte[1024];

            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            in.close();

            return out.toByteArray();

        } finally {
            connection.disconnect();
        }
    }

    //This method parses a URL and pulls the data from the URL and loads it into a JSON Array.
    //It then calls the parse method to load that data into the Beverage List, then return that list
    private String getURLString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<Beverage> fetchBeverages() {
        List<Beverage> beverages = new ArrayList<>();
        try {
            String url = Uri.parse("http://barnesbrothers.homeserver.com/beverageapi").buildUpon()
                    .build().toString();

            String jsonString = this.getURLString(url);

            JSONArray jsonArray = new JSONArray(jsonString);

            parseBeverages(beverages, jsonArray);
        } catch(JSONException jse) {
            Log.e(TAG, "Failed to parse JSON String: ", jse);
        } catch(IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
        }
        return beverages;
    }

    //This Method pulls the JSON Array populated by the get URL method and breaks it down to load into
    //an ArrayList of beverages
    private void parseBeverages(List<Beverage> beverages, JSONArray jsonArray) throws IOException, JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            double price = 0;

            JSONObject beverageJSONObject = jsonArray.getJSONObject(i);

            String idForNewBeverage = beverageJSONObject.getString("id");

            String nameForNewBeverage = beverageJSONObject.getString("name");

            String packForNewBeverage = beverageJSONObject.getString("pack");

            try {
                price = Double.parseDouble(beverageJSONObject.getString("price"));
            } catch(Exception e) {
                Log.e(TAG, "Failed to parse the Price: ", e);
            }

            boolean isActiveForNewBeverage = !beverageJSONObject.getString("isActive").contains("0");

            beverages.add(new Beverage(idForNewBeverage, nameForNewBeverage, packForNewBeverage,
                    price, isActiveForNewBeverage));
        }
    }
}