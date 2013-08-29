package net.danopia.mobile.laundryview.structs.Assist;

import android.location.Location;

/**
 * Created by daniel on 8/29/13.
 */
public class Campus implements Comparable<Campus> {
    public String name, path;
    public float[] coords;
    public int distance;
    public Location location;

    public int compareTo(Campus that) {
        return this.distance - that.distance;
    }
}
