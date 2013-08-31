package net.danopia.mobile.laundryview.data;

import android.os.Build;

import net.danopia.mobile.laundryview.structs.Location;
import net.danopia.mobile.laundryview.structs.Machine;
import net.danopia.mobile.laundryview.structs.Page;
import net.danopia.mobile.laundryview.structs.Provider;
import net.danopia.mobile.laundryview.structs.Room;
import net.danopia.mobile.laundryview.util.Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles HTTP stuff
 * Created by danopia on 5/17/13.
 */
public class LvClient {
    private static final String BASE_URL = "http://www.laundryview.com/";
    // private static final String BASE_URL = "http://mobile.danopia.net/laundryview/data/";

    static {
        // Work around pre-Froyo bugs in HTTP connection reuse.
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        } else { // Enforce otherwise, seems to improve performance
            System.setProperty("http.keepAlive", "true");
        }

        resetCookies();
    }

    public static void resetCookies() {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    public static Page getPage(String path) {
        BufferedReader in;
        StringBuilder sb = new StringBuilder();
        HttpURLConnection urlConnection = null;
        Page page = new Page();

        try {
            URL url = new URL(BASE_URL + path);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(false);

            if (urlConnection.getResponseCode() == 302) {
                page.location = urlConnection.getHeaderField("Location");
                urlConnection.disconnect();

                if (!page.location.substring(0, 4).equals("http")) {
                    page.location = BASE_URL + page.location;
                }

                url = new URL(page.location);
                urlConnection = (HttpURLConnection) url.openConnection();
            }
            page.code = urlConnection.getResponseCode();

            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String l;
            while ((l = in.readLine()) != null) {
                sb.append(l);
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

        page.body = sb.toString();
        return page;
    }

    protected static Map<String, String> getDataFile(String path) {
        Page page = getPage(path);
        if (page.body == null) return null;

        Map<String, String> data = new HashMap<String, String>();
        if (page.body.length() == 0) return data;

        // UGLY HACK. THANKS MACGREY.
        String raw = page.body.replaceAll("&deg;", "°").replaceAll("&amp;", "&");
        String[] parts = raw.substring(1).split("&");

        for (String part : parts) {
            String[] keyval = part.split("=", 2);
            if (keyval.length == 2)
                data.put(keyval[0], keyval[1]);
        }

        return data;
    }

    private static final Pattern p1 = Pattern.compile("<(?:div|h4) [^>]+>\\s+(.+?)\\s+</(?:div|h4)>");
    private static final Pattern p2 = Pattern.compile("<a href=\"laundry_room\\.php\\?lr=(\\d+)\"[^>]+>\\s+(.+?)\\s+<[^<]+<span[^>]+>\\((\\d+) W(?:ASHERS)? / (\\d+) D(?:RYERS)?\\)</");
    private static final Pattern p3 = Pattern.compile("flashvars = \\{gallons: \"([\\d,]+)\", room: \"gallons of water saved at [ ]?([^\"]+)\"\\}");
    private static final Pattern p4 = Pattern.compile("<div class=\"home-box2\"><a href=\"([^\"]+)\"[^>]+>Click here</a> to report a problem [^<]+?at ([^<]+?)\\.</div>");

    public static Provider getLocations() {
        Matcher m;

        Page page = getPage("lvs.php");
        if (page.body == null) return null; // probably no network

        String chunk = page.body.substring(page.body.indexOf("<div class=\"home-schoolinfo\">"));
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

                String rName = Helpers.titleCase(m.group(2).replace(name + " - ", "").replace(name + " ", ""));
                rooms.add(new Room(Long.parseLong(m.group(1)), rName, Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))));
            }
            locations.add(new Location(Helpers.titleCase(name), rooms));
        }

        String name = null;
        int gals = 0;
        String reportLink = null;

        m = p3.matcher(page.body);
        if (m.find()) {
            gals = Integer.parseInt(m.group(1).replaceAll(",", ""));
            name = Helpers.titleCase(m.group(2));
        }

        m = p4.matcher(page.body);
        if (m.find()) {
            reportLink = m.group(1);
            name = Helpers.titleCase(m.group(2));
        }

        return new Provider(name, gals, reportLink, locations);
    }

    public static void getRoom(Room room) {
        List<Machine> machines = new ArrayList<Machine>();

        Map<String, String> data = getDataFile("staticRoomData.php?location=" + room.id);
        if (data == null) return;

        if (data.isEmpty()) { // classic room, no enhanced data
            return; // TODO: /classic_laundry_room_ajax.php?lr=
        }

        for (int i = 1; data.containsKey("machineData" + i); i++) {
            String[] values = data.get("machineData" + i).split(":");
            data.remove("machineData" + i);

            double x = Double.parseDouble(values[0]);
            double y = Double.parseDouble(values[1]);
            String heading = values[2];
            String type = values[3];

            if (type.equals("washFL") || type.equals("washTL")) {
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
                    || type.equals("tableLg") // york university - alcuin college
                    || type.equals("cardReader")) { // public demo
                // do nothing
            } else {
                System.out.println("LaundryView: Unknown machine type found: " + type); // TODO: report
            }
        }

        room.enhance(data, machines);
    }

    public static void updateRoom(Room room) {
        if (room == null) {
            System.out.println("LaundryView: Asked to update a null room");
            return;
        }

        Map<String, String> data = getDataFile("dynamicRoomData.php?location=" + room.id);
        if (data == null) return;

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

                    if (room.getMachine(id) != null)
                        room.getMachine(id).enhance(status, timeLeft, cycleLength, message);

                case 9:
                    status = Integer.parseInt(values[0]);
                    timeLeft = Integer.parseInt(values[1]);
                    id = Integer.parseInt(values[3]);
                    cycleLength = Integer.parseInt(values[4]);
                    message = (values[6].equals("0")) ? null : values[6];

                    if (room.getMachine(id) != null)
                        room.getMachine(id).enhance(status, timeLeft, cycleLength, message);
            }
        }
    }
}
