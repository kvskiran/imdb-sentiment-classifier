package com.classifier.utilities;

import java.util.Comparator;
import java.util.HashMap;

// this class can be used to sort a hashmap based on value
public class ValueComparator implements Comparator<String> {
    HashMap<String,Double> map = new HashMap<String,Double>();

    public ValueComparator(HashMap<String,Double> map) {
        this.map.putAll(map);
    }

    @Override
    public int compare(String s1, String s2) {
        if (map.get(s1) >= map.get(s2)) {
            return -1;
        } else return 1;
    }
}
