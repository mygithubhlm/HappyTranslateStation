package com.marktony.translator.operate;

import android.util.Log;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by think on 2017/5/5.
 */

public class Operate {
    //处理得到的例句
    public static String operateSentence(String result){
        result = result.replaceAll("anyType","");
//        result = result.replaceAll("}","\n}");
        return result.replaceAll("string=","\n");
    }

    //获得所有的参数分割后的数组
    public static String[] getParameters(String result){
        String temp = result.substring(8,result.length()-1);
        Log.e("getParameters: ",temp );
        String[] temps1 = temp.split("string=");
        String[] temps = new String[temps1.length-1];
        for (int i = 0; i < temps.length; i++) {
            temps[i]=temps1[i+1];
        }
        for (String t:temps
                ) {
            t = t.substring(0,t.length()-1);
        }
        return temps;
    }
    public static String getTrans(String result){
        return getParameters(result)[3];
    }

    public static byte[] getBytes(String filePath){
        byte[] buffer = null;
        try {

            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

	//适用于String类型的参数,version=1:VER11 version=2:VER12
	public static String responseResult(String namespace,String method,String url,Map<String,String> parameters,int version,boolean dotNet){
		final String TURL = url;
        final String NAMESPACE = namespace;
        final String METHOD = method;
		
        SoapObject request = new SoapObject(NAMESPACE,METHOD);
        // 设置需调用WebService接口需要传入的两个参数mobi
        // leCode、userId

		int mapsize = parameters.size();
//        Log.e("paramSIze: ", mapsize+"");
        Iterator keyValuePairs = parameters.entrySet().iterator();
		for (int i = 0; i < mapsize; i++)
		{
			Map.Entry entry = (Map.Entry) keyValuePairs.next();
			String key = (String) entry.getKey();
			String value = (String)entry.getValue();

            Log.e( "value: ", key+
                    value);
            request.addProperty(key,value);
		}

        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope
                (version==1?SoapSerializationEnvelope.VER11:(version==2?SoapSerializationEnvelope.VER12:null));
        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
		if(dotNet){
			envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true
		}
        envelope.encodingStyle = "UTF-8";
        HttpTransportSE httpTransportSE = new HttpTransportSE(TURL);
        try {
            httpTransportSE.call(null, envelope);//调用
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
            Log.e("responseResult: ", envelope.bodyIn.toString());
            Log.e("responseResult: ", e.toString());
            return  "bad input";
        }
        // 获取返回的结果
//        String tt = "";
////        Log.d("debug",tt);
//        System.out.println(tt);
//        return tt;
	}
}
