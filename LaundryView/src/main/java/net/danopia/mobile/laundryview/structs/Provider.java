package net.danopia.mobile.laundryview.structs;

import java.util.List;

/**
 * Created by danopia on 5/17/13.
 */
public class Provider {
    public final String name;
    public final Integer gallonsSaved;
    public final List<Location> locations;

    public Provider(String name, Integer gallonsSaved, List<Location> locations) {
        this.name = name;
        this.gallonsSaved = gallonsSaved;
        this.locations = locations;
    }

    public Room getRoom(int id) {
        for (Location location : this.locations) {
            for (Room room : location.rooms) {
                if (room.id == id)
                    return room;
            }
        }

        return null;
    }

    public Location getRoomLocation(int id) {
        for (Location location : this.locations) {
            for (Room room : location.rooms) {
                if (room.id == id)
                    return location;
            }
        }

        return null;
    }
}
