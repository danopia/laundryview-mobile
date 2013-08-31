package net.danopia.mobile.laundryview.structs;

import java.util.List;

/**
 * Caches provider info.
 *
 * Created by danopia on 5/17/13.
 */
public class Provider {
    public final String name;
    public final boolean isSingleLoc;
    public final boolean isDemo;
    public final Integer gallonsSaved;
    public final String reportLink; // i.e. "mailto:address@one; address@two"
    public final List<Location> locations;

    public Provider(String name, Integer gallonsSaved, String reportLink, List<Location> locations) {
        this.gallonsSaved = gallonsSaved;
        this.reportLink = reportLink;
        this.locations = locations;

        this.isSingleLoc = (locations.size() == 1);

        if (this.isSingleLoc) {
            this.name = locations.get(0).name;
        } else if (name != null) {
            this.name = name;
        } else {
            this.name = "Unknown Provider";
        }

        this.isDemo = (this.name.equals("Demo Location"));
    }

    public Room getRoom(long id) {
        for (Location location : this.locations) {
            for (Room room : location.rooms) {
                if (room.id == id)
                    return room;
            }
        }

        return null;
    }

    public Location getRoomLocation(long id) {
        for (Location location : this.locations) {
            for (Room room : location.rooms) {
                if (room.id == id)
                    return location;
            }
        }

        return null;
    }
}
