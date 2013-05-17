package net.danopia.mobile.laundryview;

import java.util.StringTokenizer;

/**
 * Created by danopia on 5/17/13.
 */
public class Util {
    public static String titleCase(String s) {
        StringBuilder rv = new StringBuilder(s.length());
        StringTokenizer strtok = new StringTokenizer(s);
        // handle the potential null error: (should really output a runtime warning here)
        if(s == null) return null;
        while(strtok.hasMoreTokens()) {
            String word = strtok.nextToken();
            String firstLetter = word.substring(0,1);
            String restOfWord = word.substring(1);

            if (rv.length() > 0) rv.append(" ");
            rv.append(firstLetter.toUpperCase() + restOfWord.toLowerCase());
        }
        return rv.toString();
    }
}
