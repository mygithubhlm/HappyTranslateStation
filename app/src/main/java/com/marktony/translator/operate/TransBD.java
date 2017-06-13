package com.marktony.translator.operate;
import java.util.Map;
import java.util.HashMap;
public class TransBD
{
    final static String TURL = "http://120.77.146.241:8080/axis2/services/BaiduTransService?wsdl";
    final static String NAMESPACE = "http://mm";
    final static int VERSION = 1;
    final static boolean DOTNET = false;
    //·­Òë³ÉÖÐÎÄ£¬ÊäÈëÎªÓ¢ÎÄ
    public static String transIntoZh(String origin){
        Map<String,String> param = new HashMap<String,String>();
        param.put("origin",origin);
        String result = Operate.responseResult(NAMESPACE,"getTransZh",TURL,param,VERSION,DOTNET);
        return result;
    }
    //·­Òë³ÉÓ¢ÎÄ£¬ÊäÈëÎªÖÐÎÄ
    public static String transIntoEn(String origin){
        Map<String,String> param = new HashMap<String,String>();
        param.put("origin",origin);
        String result = Operate.responseResult(NAMESPACE,"getTransEn",TURL,param,VERSION,DOTNET);
        return result;
    }
}