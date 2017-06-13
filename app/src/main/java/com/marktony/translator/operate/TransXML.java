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


	//输入为中英文词语（不能是句子），返回String数组
	/**String[0] = 需要进行翻译的单词（输入的单词）
    String[1] =音标（英文）、拼音（中文字）
    String[2] =中文字的  国标码、部首、笔画 、笔顺信息
    String[3] =翻译、解释，多个翻译使用中文“；”分隔
    String[4] =英文单词朗读MP3文件名*/
	public static String[] getResults(String wordKey){
		Map<String,String> param = new HashMap<String,String>();
		param.put("wordKey",wordKey);
		String temp = Operate.responseResult(NAMESPACE,"TranslatorString",TURL,param,VERSION,DOTNET);
		String[] result = Operate.getParameters(temp);
		return result;
	}


	//输入为中英文词语（不能是句子），返回包含该单词的例句
	public static String relatedSentence(String wordKey){
		Map<String,String> param = new HashMap<String,String>();
		param.put("wordKey",wordKey);
		String temp = Operate.responseResult(NAMESPACE,"TranslatorSentenceString",TURL,param,VERSION,DOTNET);
		String result = Operate.operateSentence(temp);
		return result;
	}



	//输入为Mp3文件名，该文件名可以通过第一个gerResults方法获得，返回的是MP3字节流Byte[]所存储的文件名
	public static String getMp3(String Mp3) throws SoapFault {
		  SoapObject request = new SoapObject(NAMESPACE,"GetMp3");
        // 设置需调用WebService接口需要传入的两个参数mobi
        // leCode、userId

		
			request.addProperty("Mp3",Mp3);
		

        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER12);
        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
		
			envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true
		
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
//        byte[] object = new byte[0];
//        try {
//            object = (byte[]) envelope.getResponse();
//        } catch (SoapFault soapFault) {
//            soapFault.printStackTrace();
//        }
		Object response = envelope.getResponse();
		byte[] object = org.kobjects.base64.Base64.decode(String.valueOf(response));
        // 获取返回的结果
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