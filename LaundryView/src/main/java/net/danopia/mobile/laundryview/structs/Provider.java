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
        for (int i = 0; i < this.locations.size(); i++) {
            for (int j = 0; j < this.locations.get(i).rooms.size(); j++) {
                Room room = this.locations.get(i).rooms.get(j);
                if (room.id == id)
                    return room;
            }
        }

        return null;
    }

    public Location getRoomLocation(int id) {
        for (int i = 0; i < this.locations.size(); i++) {
            for (int j = 0; j < this.locations.get(i).rooms.size(); j++) {
                Room room = this.locations.get(i).rooms.get(j);
                if (room.id == id)
                    return this.locations.get(i);
            }
        }

        return null;
    }
}
