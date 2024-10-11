package com.scwot.renamer.core.utils;

import java.util.regex.Pattern;

public class Patterns {

    private static final Pattern MULTI_DISK_PATTERN =
            Pattern.compile("^(cd( |_|-|)|disc( |_|-|)|disk( |_|-|))\\d+.*", Pattern.CASE_INSENSITIVE);

    public static Pattern getMultiDiskPattern() {
        return MULTI_DISK_PATTERN;
    }
}
