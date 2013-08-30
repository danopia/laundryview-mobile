package net.danopia.mobile.laundryview;

import net.danopia.mobile.laundryview.structs.Provider;

/**
 * Created by danopia on 5/17/13.
 */
class Cache {
    public static Provider provider = null;

    public static void bust() {
        provider = null;
    }
}
