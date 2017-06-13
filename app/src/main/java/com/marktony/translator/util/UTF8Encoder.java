package com.marktony.translator.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by handsome on 2017/5/5.
 */
public class UTF8Encoder {

    public static String encode(String url){
        if (url == null){
            return null;
        }
        try {
            url = URLEncoder.encode(url,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return url;
    }

}
