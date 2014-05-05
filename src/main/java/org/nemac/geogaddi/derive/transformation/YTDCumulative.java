package org.nemac.geogaddi.derive.transformation;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class YTDCumulative extends AbstractTransformation {
    
    @Override
    public SortedMap<String, Float> transform(final SortedMap<String, Float> values) throws ParseException {
        SortedMap<String, Float> transformedMap = new TreeMap<>();
        Float runningCumulative = 0f;
        Calendar date = Calendar.getInstance();
        
        for (Map.Entry<String, Float> entry : values.entrySet()) {
            date.setTime(dateFormat.parse(entry.getKey()));

            if (date.get(Calendar.DAY_OF_YEAR) == 1) {
                runningCumulative = 0f;
            }            
            
            runningCumulative += entry.getValue();
            transformedMap.put(entry.getKey(), runningCumulative);
        }
                
        return transformedMap;
    }
    
    // the normals for YTD cumulative are themselves cumulatives, so we need to de-cumulative
    @Override
    public SortedMap<String, Float> transformNormals(final SortedMap<String, Float> values) {
        SortedMap<String, Float> transformedMap = new TreeMap<>();
        Float lastValue = 0f;
        
        for (Map.Entry<String, Float> entry : values.entrySet()) {
            Float uncumulativeValue = entry.getValue() - lastValue;
            transformedMap.put(entry.getKey(), normalPrecipTransform(uncumulativeValue));
            lastValue = entry.getValue();
        }
                
        return transformedMap;
    }
    
    private Float normalPrecipTransform(Float v) {
        return 25.4f * v; // convert hundredths of inches to cm?
    }
}
