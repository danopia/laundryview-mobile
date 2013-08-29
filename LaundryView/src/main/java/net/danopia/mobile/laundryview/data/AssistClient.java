package net.danopia.mobile.laundryview.data;

import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.danopia.mobile.laundryview.structs.Assist.Campus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 8/29/13.
 */
public class AssistClient {
    private static final String BASE_URL = "http://mobile.danopia.net/laundryview/";

    public static List<Campus> getCampuses(Location loc) {
        String json = getPage("geo?lat=" + loc.getLatitude() + "&long=" + loc.getLongitude());
        Gson gson = new Gson();

        Type listType = new TypeToken<ArrayList<Campus>>() {}.getType();
        return gson.fromJson(json, listType);
    }

    public static void submitPath(String path, Location loc) {
        getPage("path/" + path + "?lat=" + loc.getLatitude() + "&long=" + loc.getLongitude());
    }

    private static String getPage(String path) {
        BufferedReader in;
        StringBuilder sb = new StringBuilder();
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(BASE_URL + path);
            urlConnection = (HttpURLConnection) url.openConnection();

            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String l;
            while ((l = in.readLine()) != null) {
                sb.append(l).append('\n');
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return sb.toString();
    }
}
