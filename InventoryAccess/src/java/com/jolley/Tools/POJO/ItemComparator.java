package com.jolley.Tools.POJO;

import java.util.Comparator;

public class ItemComparator implements Comparator<Item>{
    //Ascending order so that the most recent additions is applied last
    public int compare(Item o1, Item o2) {
        long t1 = o1.getTimestamp().getTime();
        long t2 = o2.getTimestamp().getTime();
        return t1 == t2 ? 0 : t1 < t2 ? -1 : 1;
    }
}
