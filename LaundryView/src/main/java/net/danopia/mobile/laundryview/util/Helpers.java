package net.danopia.mobile.laundryview.util;

import java.util.StringTokenizer;

/**
 * Misc halpers
 *
 * Created by daniel on 8/29/13.
 */
public class Helpers {
    public static String titleCase(String s) {
        StringBuilder rv = new StringBuilder(s.length());
        StringTokenizer strtok = new StringTokenizer(s);

        while (strtok.hasMoreTokens()) {
            String word = strtok.nextToken();
            String firstLetter = word.substring(0,1);
            String restOfWord = word.substring(1);

            if (rv.length() > 0) rv.append(" ");
            rv.append(firstLetter.toUpperCase());
            rv.append(restOfWord.toLowerCase());
        }

        return rv.toString();
    }
}
