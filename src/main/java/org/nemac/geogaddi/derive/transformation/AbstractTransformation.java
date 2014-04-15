package org.nemac.geogaddi.derive.transformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class AbstractTransformation {
        
    public AbstractTransformation() {
        //
    }
    
    public SortedMap<String, Float> process(final SortedMap<String, Float> values, final SortedMap<String, Float> defaults) throws ParseException {
        return transform(fillGapsWithNormals(values, defaults));
    }
    
    protected SortedMap<String, Float> fillGapsWithNormals(final SortedMap<String, Float> values, final SortedMap<String, Float> defaults) throws ParseException {
        SortedMap<String, Float> filledMap = new TreeMap<>();
        
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar normalDate = Calendar.getInstance();
        normalDate.setTime(format.parse(defaults.firstKey()));
        
        Calendar runningDate = Calendar.getInstance();
        runningDate.setTime(format.parse(values.firstKey()));
        
        if (runningDate.get(Calendar.DAY_OF_YEAR) != 1) {
            runningDate.set(Calendar.DAY_OF_YEAR, 1);
        }
        
        for (Map.Entry<String, Float> entry : values.entrySet()) {
            if (format.parse(entry.getKey()) != runningDate.getTime()) {
                int d = runningDate.get(Calendar.DAY_OF_YEAR);
                normalDate.set(Calendar.DAY_OF_YEAR, d);
                String date = format.format(normalDate.getTime());
                filledMap.put(format.format(runningDate.getTime()), defaults.get(date));
            } else {
                filledMap.put(entry.getKey(), entry.getValue());
            }
            
            runningDate.add(Calendar.DATE, 1);
        }
        
        return filledMap;
    }
    
    protected SortedMap<String, Float> transform(final SortedMap<String, Float> values) {
        SortedMap<String, Float> transformedMap = new TreeMap<>();
        
        for (Map.Entry<String, Float> entry : values.entrySet()) {
            // do stuff
            transformedMap.put(entry.getKey(), entry.getValue());
        }
        
        return transformedMap;
    }
}
