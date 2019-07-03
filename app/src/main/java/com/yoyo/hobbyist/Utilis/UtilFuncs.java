package com.yoyo.hobbyist.Utilis;

import java.util.ArrayList;

public class UtilFuncs {
    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {
        ArrayList<T> newList = new ArrayList<T>();

        for (T element : list) {

            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        return newList;
    }
}
