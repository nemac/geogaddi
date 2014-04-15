package org.nemac.geogaddi.parcel.summary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.json.JSONObject;

public class Summarizer {
    
    private Map<String, Map<String, DataElement>> summaryMap = new TreeMap<String, Map<String, DataElement>>();
    
    public Summarizer() {
        
    }
    
    public void setElement(String folder, String file, DataElement element) {
        Map<String, DataElement> fileElement = new TreeMap<String, DataElement>();
        fileElement.put(file, element);
        summaryMap.put(folder, fileElement);
    }
    
    public void addElement(String folder, String file, String min, String max, SummaryState state) {
        // compare existing item
        if (summaryMap.containsKey(folder)) {
            Map<String, DataElement> fileElement = summaryMap.get(folder);
            if (fileElement.containsKey(file)) {
                if (min.compareTo(summaryMap.get(folder).get(file).getMin()) < 0) {
                    summaryMap.get(folder).get(file).setMin(min);
                }
                
                if (max.compareTo(summaryMap.get(folder).get(file).getMax()) > 0) {
                    summaryMap.get(folder).get(file).setMax(max);
                }
            } else {
                fileElement.put(file, new DataElement(min, max, state.getValue()));
            }
        } else {
            DataElement range = new DataElement(min, max, state.getValue());
            Map<String, DataElement> fileElement = new TreeMap<String, DataElement>();
            fileElement.put(file, range);
            summaryMap.put(folder, fileElement);
        }
    }
    
    public void addElement(String folder, String file, Set<String> lines, SummaryState state) {
        // compare existing item
        if (summaryMap.containsKey(folder)) {
            Map<String, DataElement> fileElement = summaryMap.get(folder);
            if (fileElement.containsKey(file)) {
                DataElement linesRange = getDataRange(lines, state);
                if (linesRange.getMin().compareTo(summaryMap.get(folder).get(file).getMin()) < 0) {
                    summaryMap.get(folder).get(file).setMin(linesRange.getMin());
                }
                
                if (linesRange.getMax().compareTo(summaryMap.get(folder).get(file).getMax()) > 0) {
                    summaryMap.get(folder).get(file).setMax(linesRange.getMax());
                }
            } else {
                fileElement.put(file, getDataRange(lines, state));
            }
        } else {
            DataElement range = getDataRange(lines, state);
            Map<String, DataElement> fileElement = new TreeMap<String, DataElement>();
            fileElement.put(file, range);
            summaryMap.put(folder, fileElement);
        }
    }
    
    public String jsonSummary() {
        JSONObject json = new JSONObject(summaryMap);
        return json.toString();
    }
    
    public static DataElement getDataRange(List<String> range, SummaryState state) {
        String start = range.get(0).split(",")[0];
        String end = range.get(range.size() - 1).split(",")[0];
        return new DataElement(start, end, state.getValue());
    }
    
    public static DataElement getDataRange(Set<String> lines, SummaryState state) {
        List<String> range = new ArrayList<String>(lines);
        return getDataRange(range, state);
    }
}
