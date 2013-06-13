package net.danopia.mobile.laundryview.data;

import net.danopia.mobile.laundryview.structs.Machine;

import java.util.Comparator;

/**
 * Created by daniel on 6/13/13.
 */
public class MachineComparator implements Comparator<Machine> {
    @Override
    public int compare(Machine m1, Machine m2) {
        if (Integer.parseInt(m1.number) > 0 && Integer.parseInt(m2.number) > 0)
            return Double.compare(Integer.parseInt(m1.number), Integer.parseInt(m2.number));

        return m1.number.compareTo(m2.number);
    }
}