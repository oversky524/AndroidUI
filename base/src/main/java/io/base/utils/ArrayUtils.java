package io.base.utils;

/**
 * Created by gaochao on 2015/11/3.
 */
public class ArrayUtils {
    private ArrayUtils(){}

    public static <T> int findInArray(T[] array, T object){
        if(object == null || array.length < 1){
            return -1;
        }
        for(int i=0; i<array.length; ++i){
            if(array[i].equals(object)){
                return i;
            }
        }
        return -1;
    }
}
