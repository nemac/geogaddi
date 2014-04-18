package org.nemac.geogaddi.derive.transformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class AbstractTransformation {
    
    protected final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        
    public AbstractTransformation() {
        //
    }
    
    public SortedMap<String, Float> process(final SortedMap<String, Float> values, final SortedMap<String, Float> defaults) throws ParseException {
        return transform(fillGapsWithNormals(values, transformNormals(defaults)));
    }
    
    protected SortedMap<String, Float> fillGapsWithNormals(final SortedMap<String, Float> values, final SortedMap<String, Float> defaults) throws ParseException {
        SortedMap<String, Float> filledMap = new TreeMap<>();
        
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        
        // normals date, used for date iteration transposition
        Calendar normalDate = Calendar.getInstance();
        normalDate.setTime(format.parse(defaults.firstKey()));
        
        // start date, used as an incrementer
        Calendar runningDate = Calendar.getInstance();
        runningDate.setTime(format.parse(values.firstKey()));
        if (runningDate.get(Calendar.DAY_OF_YEAR) != 1) {
            runningDate.set(Calendar.DAY_OF_YEAR, 1);
        }
        
        // end date
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(format.parse(values.lastKey()));
        if (endDate.get(Calendar.DAY_OF_YEAR) != endDate.getActualMaximum(Calendar.DAY_OF_YEAR)) {
            endDate.set(Calendar.DAY_OF_YEAR, endDate.getActualMaximum(Calendar.DAY_OF_YEAR));
        }
        
        while(endDate.after(runningDate)) {
            String key = format.format(runningDate.getTime());
            if (values.containsKey(key)) {
                filledMap.put(key, values.get(key));
            } else {
                // transpose day of year to normals
                int d = runningDate.get(Calendar.DAY_OF_YEAR);
                normalDate.set(Calendar.DAY_OF_YEAR, d);
                String dt = format.format(normalDate.getTime());
                
                // get from normals map
                filledMap.put(format.format(runningDate.getTime()), defaults.get(dt));
            }
            
            runningDate.add(Calendar.DATE, 1);
        }
        
        return filledMap;
    }
    
    protected SortedMap<String, Float> transform(final SortedMap<String, Float> values) throws ParseException {       
        return values;
    }
    
    protected SortedMap<String, Float> transformNormals(final SortedMap<String, Float> values) {
        return values;
    }
}
