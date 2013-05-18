package net.danopia.mobile.laundryview.structs;

import net.danopia.mobile.laundryview.Util;

import java.util.List;
import java.util.Map;

/**
 * Created by danopia on 5/17/13.
 */
public class Room {
    public final int id;
    public final String name;
    public final int w;
    public final int d;
    public List<Machine> machines = null;
    public Map<String, String> meta = null;

    public Room(int id, String name, int w, int d) {
        this.id = id;
        this.name = name;
        this.w = w;
        this.d = d;
    }

    public Room(int id, Map<String, String> meta, List<Machine> machines) {
        this.id = id;
        this.name = Util.titleCase(meta.get("name"));
        this.w = -1;
        this.d = -1;

        enhance(meta, machines);
    }

    public void enhance(Map<String, String> meta, List<Machine> machines) {
        this.meta = meta;
        this.machines = machines;
    }

    public Machine getMachine(int id) {
        for (int i = 0; i < this.machines.size(); i++) {
            if (this.machines.get(i).id == id)
                return this.machines.get(i);
        }

        return null;
    }

    // &wallColor=006BB7&bgColor=255,255,255&borderColor=0,107,183
}
