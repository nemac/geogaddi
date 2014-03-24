package org.nemac.geogaddi.parcel.summary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.json.JSONObject;

public class Summarizer {
    
    private Map<String, Map<String, DataRange>> summaryMap = new TreeMap<String, Map<String, DataRange>>();
    
    public Summarizer() {
        
    }
    
    public void addElement(String folder, String file, Set<String> lines) {
        // compare existing item
        if (summaryMap.containsKey(folder)) {
            Map<String, DataRange> fileElement = summaryMap.get(folder);
            if (fileElement.containsKey(file)) {
                DataRange linesRange = getDataRange(lines);
                if (linesRange.getMin().compareTo(summaryMap.get(folder).get(file).getMin()) < 0) {
                    summaryMap.get(folder).get(file).setMin(linesRange.getMin());
                }
                
                if (linesRange.getMax().compareTo(summaryMap.get(folder).get(file).getMax()) > 0) {
                    summaryMap.get(folder).get(file).setMax(linesRange.getMax());
                }
            } else {
                fileElement.put(file, getDataRange(lines));
            }
        } else {
            DataRange range = getDataRange(lines);
            Map<String, DataRange> fileElement = new TreeMap<String, DataRange>();
            fileElement.put(file, range);
            summaryMap.put(folder, fileElement);
        }
    }
    
    public String jsonSummary() {
        JSONObject json = new JSONObject(summaryMap);
        return json.toString();
    }
    
    public static DataRange getDataRange(Set<String> lines) {
        List<String> range = new ArrayList<String>(lines);
        String start = range.get(0).split(",")[0];
        String end = range.get(range.size() - 1).split(",")[0];
        return new DataRange(start, end);
    }
}
