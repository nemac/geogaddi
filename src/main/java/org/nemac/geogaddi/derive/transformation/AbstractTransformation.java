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

    public final SortedMap<String, Float> process(final SortedMap<String, Float> values, final SortedMap<String, Float> defaults) throws ParseException {
        return transform(fillGapsWithNormals(values, transformNormals(defaults)));
    }
    
    protected final SortedMap<String, Float> fillGapsWithNormals(final SortedMap<String, Float> values, final SortedMap<String, Float> defaults) throws ParseException {
        SortedMap<String, Float> filledMap = new TreeMap<>();
        
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        
        // normals date, used for date iteration transposition
        Calendar normalDate = Calendar.getInstance();
        normalDate.setTime(format.parse(defaults.firstKey()));
        int year = normalDate.get(Calendar.YEAR);
        
        // start date, used as an incrementer
        Calendar runningDate = Calendar.getInstance();
        runningDate.setTime(format.parse(values.firstKey()));
        
        if (runningDate.get(Calendar.DAY_OF_YEAR) != 1) {
            runningDate.set(Calendar.DAY_OF_YEAR, 1);
        }
        
        // end date
        Calendar endDate = Calendar.getInstance();
        /* This logic fills for entire years -- even beyond the current day
        endDate.setTime(format.parse(values.lastKey()));
        if (endDate.get(Calendar.DAY_OF_YEAR) != endDate.getActualMaximum(Calendar.DAY_OF_YEAR)) {
            endDate.set(Calendar.DAY_OF_YEAR, endDate.getActualMaximum(Calendar.DAY_OF_YEAR));
        }
        */
        
        while(endDate.after(runningDate)) {
            String key = format.format(runningDate.getTime());
            if (values.containsKey(key)) {
                Float v = values.get(key);
                if (v == null || v.isNaN()) {
                    //System.out.println("Null value from data: " + runningDate.getTime());
                    filledMap.put(key, 0f);
                } else {
                    filledMap.put(key, values.get(key));
                }
                
            } else {
                // transpose day of year to normals
                int d = runningDate.get(Calendar.DAY_OF_YEAR);
                normalDate.set(Calendar.DAY_OF_YEAR, d);
                normalDate.set(Calendar.YEAR, year);
                String dt = format.format(normalDate.getTime());
                
                // get from normals map
                Float v = defaults.get(dt);
                if (v == null || v.isNaN()) {
                    //System.out.println("Null value from defaults: " + runningDate.getTime());
                    filledMap.put(format.format(runningDate.getTime()), 0f);
                } else {
                    filledMap.put(format.format(runningDate.getTime()), defaults.get(dt));
                }
            }
            
            runningDate.add(Calendar.DATE, 1);
        }
        
        return filledMap;
    }

    public SortedMap<String, Float> transform(final SortedMap<String, Float> values) throws ParseException {
        return values;
    }

    public SortedMap<String, Float> transformNormals(final SortedMap<String, Float> values) {
        return values;
    }
}
