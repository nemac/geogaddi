package org.nemac.geogaddi.derive.transformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YTDCumulative extends AbstractTransformation {
    
    @Override
    protected SortedMap<String, Float> transform(final SortedMap<String, Float> values) {
        SortedMap<String, Float> transformedMap = new TreeMap<>();
        
        float runningCumulative = 0;
        
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar date = Calendar.getInstance();
        
        for (Map.Entry<String, Float> entry : values.entrySet()) {
            try {
                date.setTime(format.parse(entry.getKey()));
                if (date.get(Calendar.DAY_OF_YEAR) != 1) {
                    runningCumulative = 0;
                }
            } catch (ParseException ex) {
                Logger.getLogger(YTDCumulative.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (entry.getValue() != null) { // TODO: this won't be necessary once incorporate the normals
                runningCumulative += entry.getValue();
            }
            
            transformedMap.put(entry.getKey(), runningCumulative);
        }
        
        System.out.println(runningCumulative);
        
        return transformedMap;
    }
    
}
