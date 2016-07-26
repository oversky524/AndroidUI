package io.base.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaochao on 2015/10/27.
 */
public class ListUtils {
    private ListUtils(){}

    public static <T> boolean isEmpty(List<T> list){
        return list == null || list.isEmpty();
    }

    public static <T extends Serializable> Serializable getSerializable(List<T> list){
        return isEmpty(list) ? "" : list.get(0);
    }

    public static <T> int addAllExcludingEquals(ArrayList<T> dst, List<T> src){
        List<T> temp = new ArrayList<>();
        for(T t : src){
            if(dst.indexOf(t) == -1) temp.add(t);
        }
        dst.addAll(temp);
        return temp.size();
    }
}
