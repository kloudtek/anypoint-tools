package com.kloudtek.anypoint.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {
    public static final Pattern MAJORVERSION_REGEX = Pattern.compile("(\\d*)\\..*");

    public static int getMajorVersion(String version) {
        Matcher m = MAJORVERSION_REGEX.matcher(version);
        try {
            if( m.find() ) {
                return Integer.parseInt(m.group(1));
            }
        } catch (NumberFormatException e) {
            //
        }
        throw new IllegalArgumentException("Invalid version: "+version);
    }
}
