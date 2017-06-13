package com.marktony.translator.operate;


import android.util.Base64;
import android.util.Log;


import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Voice
{
	final static String URL = "http://120.77.146.241:8080/axis2/services/BaiduVoiceService?wsdl";
	final static String NAMESPACE = "http://baidu";
	final static int VERSION = 1;
	final static boolean DOTNET = false;
	//语音识别，传入参数为发送的语音文件文件名，后缀为amr；
	//另一个参数是当前的语音所属的语种，zh：中文，en：英文，返回识别结果
//	public static String voiceReco(String filename,String lan){
//        lan = "en";
//		Log.e("FileName",filename);
//        filename = "/storage/emulated/0/Download/test.amr";
//		byte[] temp = getBytes(filename);
//
//        /***/
////        try{ FileOutputStream b = new FileOutputStream(new File("/Users/lemonhuang/desktop/ss.amr"));
////            b.write(temp);
////            b.close();
////        }
////        catch (Exception e){
////            Log.i("error",e.toString());
////        }
//        /***/
//
//        String bytes = new String(org.apache.commons.codec.binary.Base64.encodeBase64(temp));
////        Log.e("bytes",bytes);
////        String bytes = new String(Base64.encode(temp,Base64.DEFAULT));
////		Log.e( "voiceReco: ",temp.length+"" );
////		String bytes = Base64.encodeToString(temp,Base64.DEFAULT);
////        String bytes = org.apache.tomcat.util.codec.binary.Base64.encodeBase64String(temp);
////        String bytes = org.apache.commons.
//		Map<String,String> param = new HashMap<String,String>();
//		param.put("bytes",bytes);
//		param.put("lan",lan);
//		String result = Operate.responseResult(NAMESPACE,"voiceReco",URL,param,VERSION,DOTNET);
//		return result;
//	}

    public static String voiceReco(String filename,String lan){
        lan = "zh";
		Log.e("FileName",filename);
        filename = "/storage/emulated/0/Download/abc.amr";
//        filename = "/storage/emulated/0/record/20170509124744.amr";
		byte[] temp = getBytes(filename);
//        final String TURL = url;
//        final String NAMESPACE = namespace;
//        final String METHOD = method;

        SoapObject request = new SoapObject(NAMESPACE,"voiceReco");
                String bytes = new String(org.apache.commons.codec.binary.Base64.encodeBase64(temp));
        request.addProperty("bytes",bytes);
        request.addProperty("lan",lan);
        // 设置需调用WebService接口需要传入的两个参数mobi
        // leCode、userId

//        int mapsize = parameters.size();
////        Log.e("paramSIze: ", mapsize+"");
//        Iterator keyValuePairs = parameters.entrySet().iterator();
//        for (int i = 0; i < mapsize; i++)
//        {
//            Map.Entry entry = (Map.Entry) keyValuePairs.next();
//            String key = (String) entry.getKey();
//            String value = (String)entry.getValue();
//
//            Log.e( "value: ", key+
//                    value);
//            request.addProperty(key,value);
//        }

        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope
                (SoapSerializationEnvelope.VER11);
        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
//        if(dotNet){
//            envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true
//        }
        envelope.encodingStyle = "UTF-8";
        HttpTransportSE httpTransportSE = new HttpTransportSE(URL,60*1000);
        try {
            httpTransportSE.call(null, envelope);//调用
            httpTransportSE.getConnection().disconnect();
        }
        catch (Exception e){
            Log.e("zh",e.toString());
//            showTransError();
        }

        // 获取返回的数据
        try {

            SoapObject object = (SoapObject) envelope.bodyIn;
            String ree = "";
            Log.e("responseResult: ", ree = object.getProperty(0).toString());
            return  ree;
        }
        catch (Exception e){
//            Log.e("responseResult: ", envelope.bodyIn.toString());
            Log.e("responseResult: ", e.toString());
            return  "bad input";
        }
    }
	//语音合成，传入参数为输入的文本文件，支持中文输入以及中英文混合输入
	//返回合成语音文件文件名
	public static String voiceSyn(String text){
		Map<String,String> param = new HashMap<String,String>();
		param.put("text",text);
		String result = Operate.responseResult(NAMESPACE,"voiceSyn",URL,param,VERSION,DOTNET);
		return result;
	}
	//调用该方法将文件转为byte数组
	private static byte[] getBytes(String filePath){
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
            Log.e("getBytes: ", e.toString());
            e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
            Log.e("getBytes: ", e.toString());
        }
		return buffer;
	}
}