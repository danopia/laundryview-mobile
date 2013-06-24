package net.danopia.mobile.laundryview.data;

import net.danopia.mobile.laundryview.structs.Machine;

import java.util.Comparator;

/**
 * Created by daniel on 6/13/13.
 */
public class MachineComparator implements Comparator<Machine> {
    @Override
    public int compare(Machine m1, Machine m2) {
        String n1 = "0" + m1.number.replaceAll("[^0-9]", "");
        String n2 = "0" + m2.number.replaceAll("[^0-9]", "");

        return Double.compare(Integer.parseInt(n1), Integer.parseInt(n2));
    }
}