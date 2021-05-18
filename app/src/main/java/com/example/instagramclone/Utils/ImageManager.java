package com.example.instagramclone.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class ImageManager {
    public static Bitmap getBitmap(String imgUrl) {
        File imageFile = new File(imgUrl);
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try{
            fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        }catch (Exception e){

        }finally {
            try{
                fis.close();
            }catch (Exception e) {

            }
        }
        return bitmap;
    }
    public static byte[] getBytesFromBitmap(Bitmap bm,int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,quality,stream);
        return stream.toByteArray();
    }
}
