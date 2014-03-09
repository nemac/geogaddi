package org.nemac.geogaddi.domain;

public class DateValue {
    
    private final String date;
    private final String value;
    
    public DateValue (String date, String value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public String getValue() {
        return value;
    }
    
}
