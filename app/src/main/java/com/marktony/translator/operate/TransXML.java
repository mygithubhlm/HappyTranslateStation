package com.marktony.translator.operate;
import android.util.Log;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class TransXML
{
	final static String TURL = "http://fy.webxml.com.cn/webservices/EnglishChinese.asmx?wsdl";
	final static String NAMESPACE = "http://WebXml.com.cn/";
	final static int VERSION = 2;
	final static boolean DOTNET = true;


	//����Ϊ��Ӣ�Ĵ�������Ǿ��ӣ�������String����
	/**String[0] = ��Ҫ���з���ĵ��ʣ�����ĵ��ʣ�
    String[1] =���꣨Ӣ�ģ���ƴ���������֣�
    String[2] =�����ֵ�  �����롢���ס��ʻ� ����˳��Ϣ
    String[3] =���롢���ͣ��������ʹ�����ġ������ָ�
    String[4] =Ӣ�ĵ����ʶ�MP3�ļ���*/
	public static String[] getResults(String wordKey){
		Map<String,String> param = new HashMap<String,String>();
		param.put("wordKey",wordKey);
		String temp = Operate.responseResult(NAMESPACE,"TranslatorString",TURL,param,VERSION,DOTNET);
		String[] result = Operate.getParameters(temp);
		return result;
	}


	//����Ϊ��Ӣ�Ĵ�������Ǿ��ӣ������ذ����õ��ʵ�����
	public static String relatedSentence(String wordKey){
		Map<String,String> param = new HashMap<String,String>();
		param.put("wordKey",wordKey);
		String temp = Operate.responseResult(NAMESPACE,"TranslatorSentenceString",TURL,param,VERSION,DOTNET);
		String result = Operate.operateSentence(temp);
		return result;
	}



	//����ΪMp3�ļ��������ļ�������ͨ����һ��gerResults������ã����ص���MP3�ֽ���Byte[]���洢���ļ���
	public static String getMp3(String Mp3) throws SoapFault {
		  SoapObject request = new SoapObject(NAMESPACE,"GetMp3");
        // ���������WebService�ӿ���Ҫ�������������mobi
        // leCode��userId

		
			request.addProperty("Mp3",Mp3);
		

        //����SoapSerializationEnvelope ����ͬʱָ��soap�汾��(֮ǰ��wsdl�п�����)
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER12);
        envelope.bodyOut = request;//�����Ƿ�����������������bodyOut
		
			envelope.dotNet = true;//������.net������webservice����������Ҫ����Ϊtrue
		
        envelope.encodingStyle = "UTF-8";
        HttpTransportSE httpTransportSE = new HttpTransportSE(TURL);
        try {
            httpTransportSE.call(null, envelope);//����
        }
        catch (Exception e){
            Log.e("zh",e.toString());
//            showTransError();
        }

        // ��ȡ���ص�����
//        byte[] object = new byte[0];
//        try {
//            object = (byte[]) envelope.getResponse();
//        } catch (SoapFault soapFault) {
//            soapFault.printStackTrace();
//        }
		Object response = envelope.getResponse();
		byte[] object = org.kobjects.base64.Base64.decode(String.valueOf(response));
        // ��ȡ���صĽ��
		Date date = new Date();
        String result = date.getTime()+".mp3";
		try{
		File file = new File(result);
		FileOutputStream fos =new FileOutputStream(file);
//		BufferedOutputStream bos = new BufferedOutputStream(fos);
        fos.write(object);
		}
		catch(Exception e){
			Log.e("getMp3",e.toString());
		}
       // System.out.println(tt);
        return result;
	}
}