package io.base.utils;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.File;
import java.net.URI;

/**
 * Created by gaochao on 2015/11/9.
 */
public class FileUtils {
    private FileUtils(){}

    public static File getFile(Uri uri){
        if(!uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            throw new IllegalArgumentException("uri " + uri.toString() + " is illegal");
        }
        return new File(URI.create(uri.toString()));
    }

    public static void deleteDirectory(File file){
        clearDirectory(file);
        file.delete();
    }

    public static void clearDirectory(File file){
        for(File child : file.listFiles()){
            if(child.isDirectory()){
                deleteDirectory(child);
            }else{
                child.delete();
            }
        }
    }

    public static long getSize(File file){
        if(file.isDirectory()){
            long size = 0;
            for(File f : file.listFiles()){
                size += getSize(f);
            }
            return size;
        }else{
            return file.length();
        }
    }

    /*public static double getSize(long size, Unit unit){
        switch (unit){
            case K:
                return size/1024.0;

            case M:
                return size/(1024*1024.0);

            case G:
                return size/(1024*1024*1024.0);

            default:
                throw new IllegalArgumentException(unit.toString());
        }
    }

    public enum Unit{
        B("B"), K("K"), M("M"), G("G");

        Unit(String name){
            mName = name;
        }

        private String mName;

        @Override
        public String toString() {
            return mName;
        }
    }*/
}
