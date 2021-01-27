package com.scwot.renamer.core.utils.enums;

public enum Country {

    AF("Afghanistan", "AF"),
    AX("Aland", "AX"),
    AL("Albania", "AL"),
    AS("American Samoa", "AS"),
    AD("Andorra", "AD"),
    AO("Angola", "AO"),
    AI("Anguilla", "AI"),
    AM("Armenia", "AM"),
    AU("Australia", "AU"),
    AT("Austria", "AT"),
    AZ("Azerbaijan", "AZ"),
    BD("Bangladesh", "BD"),
    BY("Belarus", "BY"),
    BE("Belgium", "BE"),
    BJ("Benin", "BJ"),
    BT("Bhutan", "BT"),
    BR("Brazil", "BR"),
    BG("Bulgaria", "BG"),
    BA("Bosnia", "BA"),
    CA("Canada", "CA"),
    CL("Chile", "CL"),
    CN("China", "CN"),
    HR("Croatia", "HR"),
    CU("Cuba", "CU"),
    CZ("Czech", "CZ"),
    DK("Denmark", "DK"),
    EC("Egypt", "EG"),
    ET("Ethiopia", "ET"),
    FI("Finland", "FI"),
    FR("France", "FR"),
    GE("Georgia", "GE"),
    DE("Germany", "DE"),
    GR("Greece", "GR"),
    HU("Hungary", "HU"),
    IS("Iceland", "IS"),
    IN("India", "IN"),
    ID("Indonesia", "ID"),
    IR("Iran", "IR"),
    IQ("Iraq", "IQ"),
    IE("Ireland", "IE"),
    IL("Israel", "IL"),
    IT("Italy", "IT"),
    JM("Jamaica", "JM"),
    JP("Japan", "JP"),
    KZ("Kazakhstan", "KZ"),
    LV("Latvia", "LV"),
    LU("Luxembourg", "LU"),
    MX("Mexico", "MX"),
    MA("Morocco", "MA"),
    NZ("New Zealand", "NZ"),
    NO("Norway", "NO"),
    PK("Pakistan", "PK"),
    PE("Peru", "PE"),
    PL("Poland", "PL"),
    PT("Portugal", "PT"),
    RU("Russia", "RU"),
    SG("Singapore", "SG"),
    SK("Slovakia", "SK"),
    SI("Slovenia", "SI"),
    ZA("South Africa", "ZA"),
    ES("Spain", "ES"),
    SD("Sudan", "SD"),
    SE("Sweden", "SE"),
    CH("Switzerland", "CH"),
    TH("Thailand", "TH"),
    TR("Turkey", "TR"),
    TM("Turkmenistan", "TM"),
    UA("Ukraine", "UA"),
    GB("UK", "GB"),
    UK("UK", "UK"),
    US("USA", "US"),
    UZ("Uzbekistan", "UZ");

    private final String full;
    private final String abbr;

    private Country(String full, String abbr) {
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
