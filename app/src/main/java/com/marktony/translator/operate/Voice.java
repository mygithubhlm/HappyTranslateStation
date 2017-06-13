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
	//����ʶ�𣬴������Ϊ���͵������ļ��ļ�������׺Ϊamr��
	//��һ�������ǵ�ǰ���������������֣�zh�����ģ�en��Ӣ�ģ�����ʶ����
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
        // ���������WebService�ӿ���Ҫ�������������mobi
        // leCode��userId

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

        //����SoapSerializationEnvelope ����ͬʱָ��soap�汾��(֮ǰ��wsdl�п�����)
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope
                (SoapSerializationEnvelope.VER11);
        envelope.bodyOut = request;//�����Ƿ�����������������bodyOut
//        if(dotNet){
//            envelope.dotNet = true;//������.net������webservice����������Ҫ����Ϊtrue
//        }
        envelope.encodingStyle = "UTF-8";
        HttpTransportSE httpTransportSE = new HttpTransportSE(URL,60*1000);
        try {
            httpTransportSE.call(null, envelope);//����
            httpTransportSE.getConnection().disconnect();
        }
        catch (Exception e){
            Log.e("zh",e.toString());
//            showTransError();
        }

        // ��ȡ���ص�����
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
	//�����ϳɣ��������Ϊ������ı��ļ���֧�����������Լ���Ӣ�Ļ������
	//���غϳ������ļ��ļ���
	public static String voiceSyn(String text){
		Map<String,String> param = new HashMap<String,String>();
		param.put("text",text);
		String result = Operate.responseResult(NAMESPACE,"voiceSyn",URL,param,VERSION,DOTNET);
		return result;
	}
	//���ø÷������ļ�תΪbyte����
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