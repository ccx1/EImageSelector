package com.example.e.eimageselector;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2018/7/22.
 */

public class PhotoUtils {

    public static void deleteIsDeleted(List<Photo> photoList){
        File file = null;
        for (int i = 0; i < photoList.size(); i++) {
            Photo photo = photoList.get(i);
            file = null;
            file = new File(photo.imageFilePath);
            if (!file.exists()) {
                photoList.remove(photo);
                i--;
            }
        }
    }
}
