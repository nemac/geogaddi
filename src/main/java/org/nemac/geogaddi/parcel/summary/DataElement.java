package org.nemac.geogaddi.parcel.summary;

public class DataElement {
    
    private String min;
    private String max;
    private int state;
    
    public DataElement(String min, String max, int state) {
        this.min = min;
        this.max = max;
        this.state = state;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }
    
    public int getState() {
        return state;
    }
    
    public void setState(int state) {
        this.state = state;
    }
}
