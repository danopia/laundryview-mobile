package net.danopia.mobile.laundryview.structs;

import java.util.List;

/**
 * Stores location info.
 *
 * Created by danopia on 5/17/13.
 */
public class Location {
    public final String name;
    public final List<Room> rooms;

    public Location(String name, List<Room> rooms) {
        this.name = name;
        this.rooms = rooms;
    }
}
