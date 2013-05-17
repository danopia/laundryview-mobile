package net.danopia.mobile.laundryview.structs;

import java.util.List;

/**
 * Created by danopia on 5/17/13.
 */
public class Room {
    public final int id;
    public final String name;
    public final int w;
    public final int d;
    public List<Machine> machines;

    public Room(int id, String name, int w, int d) {
        this.id = id;
        this.name = name;
        this.w = w;
        this.d = d;
        this.machines = null;
    }
}
