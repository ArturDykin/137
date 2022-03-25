package ru.utils;

public enum LoanSystemCode {

    //should use one of option of enum - do not add new option
    PROFILE("Профайл", "Profile"),
    BQ("Бисквит VIP (Москва)", "BQ"),
    CFT2RL("CFT2RL", "CFT");

    private final String systemCode;
    private final String shortSystemCode;

    LoanSystemCode(String systemCode, String shortSystemCode) {

        this.systemCode = systemCode;
        this.shortSystemCode = shortSystemCode;
    }
    public String getShortSystemCode() {
        return shortSystemCode;
    }
    public String getSystemCode() {
        return systemCode;
    }
}
