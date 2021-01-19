package com.scwot.renamer.core.utils.enums;

public enum States {
    MASSACHUSETTS("Massachusetts",  "MA"),
    MICHIGAN     ("Michigan",       "MI");

    private final String full;
    private final String abbr;

    private States(String full, String abbr) {
        this.full = full;
        this.abbr = abbr;
    }

    public String getFullName() {
        return full;
    }

    public String getAbbreviatedName() {
        return abbr;
    }

}
