package net.danopia.mobile.laundryview.data;

import net.danopia.mobile.laundryview.Util;
import net.danopia.mobile.laundryview.structs.Location;
import net.danopia.mobile.laundryview.structs.Machine;
import net.danopia.mobile.laundryview.structs.Provider;
import net.danopia.mobile.laundryview.structs.Room;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by danopia on 5/17/13.
 */
public class Client {
    private static final String BASE_URL = "http://mobile.danopia.net/laundryview/data/";
    protected static String getPage(String path) {
        BufferedReader in = null;
        StringBuffer sb = new StringBuffer("");

        try {
            HttpClient client = new DefaultHttpClient();
            URI website = new URI(BASE_URL + path);
            HttpGet request = new HttpGet();
            request.setURI(website);
            HttpResponse response = client.execute(request);
            response.getStatusLine().getStatusCode();

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String l = "";
            while ((l = in.readLine()) !=null){
                sb.append(l); // TODO: newline?
            }
            in.close();
        } catch (ClientProtocolException e) {
            sb.append(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            sb.append(e.getMessage());
            e.printStackTrace();
        } catch (URISyntaxException e) {
            sb.append(e.getMessage());
            e.printStackTrace();
        } finally{
            if (in != null){
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    protected static Map<String, String> getDataFile(String path) {
        String raw = getPage(path);
        String[] parts = raw.substring(1).split("&");

        Map<String, String> data = new HashMap<String, String>();
        for (int i = 0; i < parts.length; i++) {
            String[] keyval = parts[i].split("=", 2);
            data.put(keyval[0], keyval[1]);
        }

        return data;
    }

    private static final Pattern p1 = Pattern.compile("<div [^>]+>\\s+(.+?)\\s+</div>");
    private static final Pattern p2 = Pattern.compile("<a href=\"laundry_room\\.php\\?lr=(\\d+)\"[^>]+>\\s+(.+?)\\s+<[^<]+<span[^>]+>\\((\\d+) W / (\\d+) D\\)</");
    private static final Pattern p3 = Pattern.compile("flashvars = \\{gallons: \"([\\d,]+)\", room: \"gallons of water saved at [ ]?([^\"]+)\"\\}");

    public static Provider getLocations() {
        Matcher m;

        String raw = getPage("lvs.php");
        String chunk = raw.substring(raw.indexOf("<div class=\"home-schoolinfo\">"));
        chunk = chunk.substring(0, chunk.indexOf("class=\"bg-blue4\""));

        String[] chunks = chunk.split("</div></div>");
        List<Location> locations = new ArrayList<Location>(chunks.length - 2);
        for (int i = 1; i < chunks.length - 1; i++) {
            m = p1.matcher(chunks[i]);
            m.find();
            String name = m.group(1);

            String[] sChunks = chunks[i].split("<br>");
            List<Room> rooms = new ArrayList<Room>(sChunks.length - 1);
            for (int j = 0; j < sChunks.length - 1; j++) {
                m = p2.matcher(sChunks[j]);
                m.find();

                String rName = Util.titleCase(m.group(2).replace(name + " - ", "").replace(name + " ", ""));
                rooms.add(new Room(Integer.parseInt(m.group(1)), rName, Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))));
            }
            locations.add(new Location(Util.titleCase(name), rooms));
        }

        m = p3.matcher(raw);
        if (m.find()) {
            int gals = Integer.parseInt(m.group(1).replaceAll(",", ""));
            return new Provider(Util.titleCase(m.group(2)), gals, locations);
        } else {
            return new Provider("Unknown Provider", 0, locations);
        }
    }

    public static void getMachines(Room room) {
        room.machines = new ArrayList<Machine>();
        Map<String, String> data = getDataFile("staticRoomData.php?location=" + room.id);
        for (int i = 1; data.containsKey("machineData" + i); i++) {
            System.out.println(i + ": " + data.get("machineData" + i));
        }
    }
}
