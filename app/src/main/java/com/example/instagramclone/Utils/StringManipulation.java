package com.example.instagramclone.Utils;

public class StringManipulation {
    public static String expandUsername(String username) {
        return username.replace("."," ");
    }
    public static String condenseUsername(String username) {
        return username.replace(" ",".");
    }

    //for extracting tags from the caption
    public static String getTags(String caption){
        if (caption.indexOf("#")>0) {
            StringBuilder sb = new StringBuilder();
            char[] charArray = caption.toCharArray();
            boolean foundWord = false;
            for (char c : charArray) {
                if(c == '#') {
                    foundWord = true;
                    sb.append(c);
                } else {
                    if (foundWord) {
                        sb.append(c);
                    }
                }
                if (c == ' ') {
                    foundWord = false;
                }
            }
            String finalTags = sb.toString().replace(" ","").replace("#",",#");
            return finalTags.substring(1);
        }
        return caption;
    }

}
