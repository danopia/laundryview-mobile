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
    private static String getPage(String path) {
        BufferedReader in = null;
        StringBuilder sb = new StringBuilder();

        try {
            HttpClient client = new DefaultHttpClient();
            URI website = new URI(BASE_URL + path);
            HttpGet request = new HttpGet();
            request.setURI(website);
            HttpResponse response = client.execute(request);
            response.getStatusLine().getStatusCode();

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String l;
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
        for (String part : parts) {
            String[] keyval = part.split("=", 2);
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

    public static void getRoom(Room room) {
        List<Machine> machines = new ArrayList<Machine>();

        Map<String, String> data = getDataFile("staticRoomData.php?location=" + room.id);
        for (int i = 1; data.containsKey("machineData" + i); i++) {
            String[] values = data.get("machineData" + i).split(":");
            data.remove("machineData" + i);

            double x = Double.parseDouble(values[0]);
            double y = Double.parseDouble(values[1]);
            String heading = values[2];
            String type = values[3];

            if (type.equals("washFL")) {
                machines.add(new Machine(room, Integer.parseInt(values[5]), values[4], x, y,  0, heading, "washer"));
            } else if (type.equals("dry")) {
                machines.add(new Machine(room, Integer.parseInt(values[5]), values[4], x, y,  0, heading, "dryer"));
            } else if (type.equals("dblDry")) {
                machines.add(new Machine(room, Integer.parseInt(values[5]), values[4], x, y, 50, heading, "dryer"));
                machines.add(new Machine(room, Integer.parseInt(values[9]), values[8], x, y,  0, heading, "dryer"));
            } else if (type.equals("washNdry")) {
                machines.add(new Machine(room, Integer.parseInt(values[5]), values[4], x, y, 50, heading, "dryer"));
                machines.add(new Machine(room, Integer.parseInt(values[9]), values[8], x, y,  0, heading, "washer"));
            } else //noinspection StatementWithEmptyBody,StatementWithEmptyBody
                if (type.equals("tableSm") // towers
                    || type.equals("sink") // e.g. van r
                    || type.equals("cardReader")) { // public demo
                // do nothing
            } else {
                System.out.println("Unknown machine type found: " + type); // TODO: report
            }
        }

        room.enhance(data, machines);
    }

    public static void updateRoom(Room room) {
        Map<String, String> data = getDataFile("dynamicRoomData.php?location=" + room.id);
        for (int i = 1; data.containsKey("machineStatus" + i); i++) {
            String[] values = data.get("machineStatus" + i).split(":");
            data.remove("machineStatus" + i);

            switch (values.length) {
                case 18:
                    int status = Integer.parseInt(values[9]);
                    int timeLeft = Integer.parseInt(values[10]);
                    int id = Integer.parseInt(values[12]);
                    int cycleLength = Integer.parseInt(values[13]);
                    String message = (values[15].equals("0")) ? null : values[15];

                    room.getMachine(id).enhance(status, timeLeft, cycleLength, message);

                case 9:
                    status = Integer.parseInt(values[0]);
                    timeLeft = Integer.parseInt(values[1]);
                    id = Integer.parseInt(values[3]);
                    cycleLength = Integer.parseInt(values[4]);
                    message = (values[6].equals("0")) ? null : values[6];

                    room.getMachine(id).enhance(status, timeLeft, cycleLength, message);
            }
        }
    }
}
