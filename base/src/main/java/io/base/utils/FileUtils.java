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
}
