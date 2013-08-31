package net.danopia.mobile.laundryview;

import android.location.Location;

import net.danopia.mobile.laundryview.structs.Provider;

/**
 * Stores stuff.
 *
 * Created by danopia on 5/17/13.
 */
class Cache {
    public static Provider provider = null;
    public static Location location = null;

    public static void bust() {
        provider = null;
    }
}
