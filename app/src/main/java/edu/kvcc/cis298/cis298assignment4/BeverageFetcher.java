package edu.kvcc.cis298.cis298assignment4;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    private String getURLString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public void fetchBeverages() {
        try{
            String url = Uri.parse("http://barnesbrothers.homeserver.com/beverageapi").buildUpon()
                    .build().toString();

            String result = this.getURLString(url);

            Log.i(TAG, "Fetched contents of URL: " + result);
        } catch(IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
        }
    }
}