package com.example.instagramclone.Utils;

import android.os.Environment;

public class FilePaths {
    //"storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();
    public String CAMERA = ROOT_DIR + "/DCIM/camera";
    public String PICTURES = ROOT_DIR + "/Pictures";
    public String WHATSAPP = ROOT_DIR + "/WhatsApp/Media/Whatsapp Images";

    //Firebase
    public String FIREBASE_IMAGE_STORAGE = "photos/users";

}
