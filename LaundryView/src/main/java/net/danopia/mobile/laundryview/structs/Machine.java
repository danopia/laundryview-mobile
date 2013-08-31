package net.danopia.mobile.laundryview.structs;

/**
 * Caches machine info.
 *
 * Created by danopia on 5/17/13.
 */
public class Machine {
    public final Room room;
    public final int id;
    public final double x, y, z;
    public final String number, heading, type;

    public int status = -1, timeLeft = -1, cycleLength = -1;
    public String message = null;

    public Machine(Room room, int id, String number, double x, double y, double z, String heading, String type) {
        this.room = room;
        this.id = id;
        this.number = number;

        this.x = x;
        this.y = y;
        this.z = z;

        this.heading = heading;
        this.type = type;
    }

    public void enhance(int status, int timeLeft, int cycleLength, String message) {
        this.status = status;
        this.timeLeft = timeLeft;
        this.cycleLength = cycleLength;
        this.message = message;
    }
}
