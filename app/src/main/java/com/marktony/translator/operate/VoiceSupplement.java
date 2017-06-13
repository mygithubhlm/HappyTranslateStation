package com.marktony.translator.operate;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import net.sf.json.JSONObject;


public class VoiceSupplement {
	private static final String serverURL = "http://tsn.baidu.com/text2audio";
    private static String token = "";
    private static final String testFileName = "test.mp3";
    //put your own params here
    private static final String apiKey = "4R5g3X1G8CmBZ7bUUNxtcilfaNhroNLu";
    private static final String secretKey = "IDUFDYFIG8AgKpkkSfyAEECVOlCdt90W";
    private static final String cuid = "9E-D2-1E-F7-F6-83";
    private static final String zh = "zh";
    private static final String en = "en";
    private static String printResponse(HttpURLConnection conn) throws Exception {
        if (conn.getResponseCode() != 200) {
        	return null;
            // request error
        }
        InputStream is = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is,"UTF-8"));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
//        	System.out.println(line);
            response.append(line);
            response.append('\r');
        }
        
        rd.close();
        return JSONObject.fromObject(response.toString()).toString(4);
    }
	private static void getToken() throws Exception {
        String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" + 
            "&client_id=" + apiKey + "&client_secret=" + secretKey;
        HttpURLConnection conn = (HttpURLConnection) new URL(getTokenURL).openConnection();
        token = JSONObject.fromObject(printResponse(conn)).getString("access_token");
        System.out.println(token);
    }
	public static byte[] getBytes(String text) throws Exception{
		getToken();
		System.setProperty("sun.net.client.defaultConnectTimeout", 10 * 60 * 1000 + "");
    	System.setProperty("sun.net.client.defaultReadTimeout", 10 * 60 * 1000 + "");
//        File pcmFile = new File(testFileName);
    	String ttString = java.net.URLEncoder.encode(java.net.URLEncoder.encode(text,"utf-8"),"utf-8");
        HttpURLConnection conn = (HttpURLConnection) new URL(serverURL
                + "?tex="+ttString+"&lan=zh&cuid=" + cuid + "&ctp=1&tok=" + token).openConnection();

        // add request header
        conn.setRequestMethod("GET");
//        conn.setRequestProperty("Content-Type", "audio/amr; rate=8000; charset=utf-8");
        conn.setConnectTimeout(10*60*1000);
        conn.setReadTimeout(10*60*1000);
        conn.setDoInput(true);
//        conn.setDoOutput(true);

        // send request
//        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
//        wr.write(loadFile(pcmFile));
//        wr.flush();
//        wr.close();
        conn.connect();
        
        InputStream inputStream = conn.getInputStream();
        return toByteArray(inputStream);
//        String fileName = new Date().getTime()+".mp3";
//        FileOutputStream fos = new FileOutputStream(fileName);
//        fos.write(toByteArray(inputStream));
////        byte[] b = new byte[1024];
////
////        while((inputStream.read(b)) != -1){
////            fos.write(b);
////        }
////        inputStream.close();
////        fos.close();
//        fos.close();
//        conn.disconnect();
//        System.out.println(printResponse(conn));
	}
	public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}
