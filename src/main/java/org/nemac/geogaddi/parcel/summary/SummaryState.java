package org.nemac.geogaddi.parcel.summary;

public enum SummaryState {
    
    OTHER(0),
    BACKLOG(1),
    UNCHANGED(2),
    APPEND(3),
    NEW(4);
    
    private final int value;
    
    SummaryState(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static SummaryState fromValue(final int value) {
        for (SummaryState summaryState : values()) {
            if (summaryState.value == value) {
                return summaryState;
            }
        }
        
        return getDefault();
    }
    
    public static SummaryState getDefault() {
        return NEW;
    }
    
}
