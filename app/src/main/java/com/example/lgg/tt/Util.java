package com.example.lgg.tt;

import java.util.Locale;

public class Util {
    public static String format(String format,Object... args){
        return String.format(Locale.US,format,args);
    }
}
