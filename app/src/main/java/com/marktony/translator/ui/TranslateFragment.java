package com.marktony.translator.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


import com.marktony.translator.R;
import com.marktony.translator.adapter.SampleAdapter;
import com.marktony.translator.db.DBUtil;
import com.marktony.translator.db.NotebookDatabaseHelper;
import com.marktony.translator.model.BingModel;
import com.marktony.translator.util.NetworkUtil;
import com.marktony.translator.operate.*;

import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.os.AsyncTask;

/***/

public class TranslateFragment extends Fragment {

    private EditText editText;
    private TextView textViewClear;
    private ProgressBar progressBar;
    private TextView textViewResult;
    private ImageView imageViewMark;
    private View viewResult;
    private AppCompatButton button;

    private ArrayList<BingModel.Sample> samples;
    private RecyclerView recyclerView;
    private SampleAdapter adapter;

    private NotebookDatabaseHelper dbHelper;

    private String result = null;
    private BingModel model;

    private RequestQueue queue;

    private boolean isMarked = false;
    private boolean completeWithEnter;
    private boolean showSamples;

    // empty constructor required
    public TranslateFragment() {

    }

    public static TranslateFragment newInstance() {
        return new TranslateFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        dbHelper = new NotebookDatabaseHelper(getActivity(), "MyStore.db", null, 1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initViews(view);

        //在这里进行网络连接的判断，如果没有连接，则进行snackbar的提示
        //如果有网络连接，则不会有任何的操作
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            showNoNetwork();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!NetworkUtil.isNetworkConnected(getActivity())) {
                    showNoNetwork();
                } else if (editText.getText() == null || editText.getText().length() == 0) {
                    Snackbar.make(button, getString(R.string.no_input), Snackbar.LENGTH_SHORT)
                            .show();
                } else {

                    sendReq(editText.getText().toString());

                }
            }
        });

        textViewClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editText.getEditableText().toString().length() != 0) {
                    textViewClear.setVisibility(View.VISIBLE);
                } else {
                    textViewClear.setVisibility(View.INVISIBLE);
                }

                // handle the situation when text ends up with enter
                // 监听回车事件
                if (completeWithEnter) {
                    if (count == 1 && s.charAt(start) == '\n') {
                        editText.getText().replace(start, start + 1, "");
                        sendReq(editText.getEditableText().toString());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageViewMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 在没有被收藏的情况下
                if (!isMarked) {
                    imageViewMark.setImageResource(R.drawable.ic_grade_white_24dp);
                    Snackbar.make(button, R.string.add_to_notebook, Snackbar.LENGTH_SHORT)
                            .show();
                    isMarked = true;

                    ContentValues values = new ContentValues();
                    values.put("input", model.getWord());
                    values.put("output", result);
                    DBUtil.insertValue(dbHelper, values);

                    values.clear();

                } else {
                    imageViewMark.setImageResource(R.drawable.ic_star_border_white_24dp);
                    Snackbar.make(button, R.string.remove_from_notebook, Snackbar.LENGTH_SHORT)
                            .show();
                    isMarked = false;

                    DBUtil.deleteValue(dbHelper, model.getWord());
                }

            }
        });

        viewResult.findViewById(R.id.image_view_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, result);
                startActivity(Intent.createChooser(intent, getString(R.string.choose_app_to_share)));
            }
        });

        viewResult.findViewById(R.id.image_view_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", result);
                manager.setPrimaryClip(clipData);

                Snackbar.make(button, R.string.copy_done, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });


        return view;
    }

    private void initViews(View view) {

        editText = (EditText) view.findViewById(R.id.et_main_input);
        textViewClear = (TextView) view.findViewById(R.id.tv_clear);
        // 初始化清除按钮，当没有输入时是不可见的
        textViewClear.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        viewResult = view.findViewById(R.id.include);
        textViewResult = (TextView) view.findViewById(R.id.text_view_output);
        imageViewMark = (ImageView) view.findViewById(R.id.image_view_mark_star);
        imageViewMark.setImageResource(R.drawable.ic_star_border_white_24dp);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(false);

        button = (AppCompatButton) view.findViewById(R.id.buttonTranslate);
    }

    private void sendReq(String in) {

        progressBar.setVisibility(View.VISIBLE);
        viewResult.setVisibility(View.INVISIBLE);

        // 监听输入面板的情况，如果激活则隐藏
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(button.getWindowToken(), 0);
        }

        in = inputFormat(in);
        /**@dahude*/
        try {
            in = java.net.URLEncoder.encode(in, "utf-8");
        } catch (Exception e) {
            Log.e("sendReq: ", e.toString());
        }

        //启动后台异步线程进行连接webService操作，并且根据返回结果在主线程中改变UI
        QueryAddressTask queryAddressTask = new QueryAddressTask();
        //启动后台任务
        queryAddressTask.execute(in);

        /**end*/
        /** String url = Constants.BING_BASE + "?Word=" + in + "&Samples=";

         if (showSamples) {
         url += "true";
         } else {
         url += "false";
         }


         StringRequest request = new StringRequest(Request.Method.GET,
         url,
         new Response.Listener<String>() {
        @Override public void onResponse(String s) {

        try {

        Gson gson = new Gson();
        model = gson.fromJson(s, BingModel.class);

        if (model != null) {

        result = model.getWord() + "\n";

        if (DBUtil.queryIfItemExist(dbHelper, model.getWord())) {
        imageViewMark.setImageResource(R.drawable.ic_grade_white_24dp);
        isMarked = true;
        } else {
        imageViewMark.setImageResource(R.drawable.ic_star_border_white_24dp);
        isMarked = false;
        }

        if (model.getPronunciation() != null) {
        BingModel.Pronunciation p = model.getPronunciation();
        result = result + "\nAmE:" + p.getAmE() + "\nBrE:" + p.getBrE() + "\n";
        }

        for (BingModel.Definition def : model.getDefs()) {
        result = result + def.getPos() + "\n" + def.getDef() + "\n";
        }

        result = result.substring(0, result.length() - 1);

        if (model.getSams() != null && model.getSams().size() != 0) {

        if (samples == null) {
        samples = new ArrayList<>();
        }

        samples.clear();

        for (BingModel.Sample sample : model.getSams()) {
        samples.add(sample);
        }

        if (adapter == null) {
        adapter = new SampleAdapter(getActivity(), samples);
        recyclerView.setAdapter(adapter);
        } else {
        adapter.notifyDataSetChanged();
        }
        }

        progressBar.setVisibility(View.INVISIBLE);
        viewResult.setVisibility(View.VISIBLE);

        textViewResult.setText(result);
        }
        } catch (JsonSyntaxException ex) {
        showTransError();
        }

        progressBar.setVisibility(View.GONE);

        }
        }, new Response.ErrorListener() {
        @Override public void onErrorResponse(VolleyError volleyError) {
        progressBar.setVisibility(View.GONE);
        showTransError();
        }
        });

         queue.add(request);*/

    }

    //去掉输入文本中的回车符号
    private String inputFormat(String in) {
        in = in.replace("\n", "");
        return in;
    }

    private void showNoNetwork() {
        Snackbar.make(button, R.string.no_network_connection, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                }).show();
    }

    private void showTransError() {
        Snackbar.make(button, R.string.trans_error, Snackbar.LENGTH_SHORT)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        completeWithEnter = sp.getBoolean("enter_key", false);
        showSamples = sp.getBoolean("samples", true);
        if (samples != null) {
            samples.clear();
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * @dahude
     */

//    public String transSentence(String in){
//        final String TRAN_URL = "http://fy.webxml.com.cn/webservices/EnglishChinese.asmx?wsdl";
//        final String NAMESPACE = "http://WebXml.com.cn/";
//        final String TRAN_TO_STR = "TranslatorSentenceString";
//        final String PARAMETER = "wordKey";
//        SoapObject request = new SoapObject(NAMESPACE, TRAN_TO_STR);
//        // 设置需调用WebService接口需要传入的两个参数mobi
//        // leCode、userId
//        request.addProperty(PARAMETER, in);
//
//        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER12);
//        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
//        envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true
//
//        HttpTransportSE httpTransportSE = new HttpTransportSE(TRAN_URL);
//        try {
//            httpTransportSE.call(null, envelope);//调用
//        }
//        catch (Exception e){
//            Log.e("transSentence",e.toString());
//            showTransError();
//        }
//
//        // 获取返回的数据
//        SoapObject object = (SoapObject) envelope.bodyIn;
//        // 获取返回的结果
//        String tt = object.getProperty(0).toString();
////        result = getParameters(tt)[3];
//        tt=Operate.operateSentence(tt);
//        Log.d("debug",tt);
//        return tt;
//    }

//    public String[] getRemoteInfo(String in){
//        final String TRAN_URL = "http://fy.webxml.com.cn/webservices/EnglishChinese.asmx?wsdl";
//        final String NAMESPACE = "http://WebXml.com.cn/";
//        final String TRAN_TO_STR = "TranslatorString";
//        final String PARAMETER = "wordKey";
//        SoapObject request = new SoapObject(NAMESPACE, TRAN_TO_STR);
//        // 设置需调用WebService接口需要传入的两个参数mobi
//        // leCode、userId
//        request.addProperty(PARAMETER, in);
//
//        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER12);
//        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
//        envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true
//
//        HttpTransportSE httpTransportSE = new HttpTransportSE(TRAN_URL);
//        try {
//            httpTransportSE.call(null, envelope);//调用
//        }
//        catch (Exception e){
//            Log.e("getRemoteInfo",e.toString());
//            showTransError();
//        }
//
//        // 获取返回的数据
//        SoapObject object = (SoapObject) envelope.bodyIn;
//        // 获取返回的结果
//        String tt = object.getProperty(0).toString();
//        String[] temps = Operate.getParameters(tt);
//        Log.d("debug",tt);
//        return temps;
//    }
    private boolean isEnglish(String input) {
        if (input != null && !input.equals("")) {
            return input.substring(0, 1).matches("[a-z]|[A-Z]");
        }
        return false;
    }


    //    public String transToZh(String input){
//        final String TRAN_URL = "http://10.0.2.2:8080/axis2/services/BaiduTransService?wsdl";
//        final String NAMESPACE = "http://mm";
//        final String TRAN_TO_STR = "getTransZh";
//        final String PARAMETER = "origin";
//        SoapObject request = new SoapObject(NAMESPACE, TRAN_TO_STR);
//        // 设置需调用WebService接口需要传入的两个参数mobi
//        // leCode、userId
//        request.addProperty(PARAMETER, input);
//
//        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
//        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
////        envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportSE = new HttpTransportSE(TRAN_URL);
//        try {
//            httpTransportSE.call(null, envelope);//调用
//        }
//        catch (Exception e){
//            Log.e("zh",e.toString());
//            showTransError();
//        }
//
//        // 获取返回的数据
//        SoapObject object = (SoapObject) envelope.bodyIn;
//        // 获取返回的结果
//        String tt = object.getProperty(0).toString();
//        Log.d("debug",tt);
//        return tt;
//    }
//    public String transToEn(String input){
//        final String TRAN_URL = "http://10.0.2.2:8080/axis2/services/BaiduTransService?wsdl";
//        final String NAMESPACE = "http://mm";
//        final String TRAN_TO_STR = "getTransEn";
//        final String PARAMETER = "origin";
//        SoapObject request = new SoapObject(NAMESPACE, TRAN_TO_STR);
//        // 设置需调用WebService接口需要传入的两个参数mobi
//        // leCode、userId
//        request.addProperty(PARAMETER, input);
//
//        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
//        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
////        envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true
//        envelope.encodingStyle = "UTF-8";
//        HttpTransportSE httpTransportSE = new HttpTransportSE(TRAN_URL);
//        try {
//            httpTransportSE.call(null, envelope);//调用
//        }
//        catch (Exception e){
//            Log.e("en",e.toString());
//            showTransError();
//        }
//
//        // 获取返回的数据
//        SoapObject object = (SoapObject) envelope.bodyIn;
//        // 获取返回的结果
//        String tt = object.getProperty(0).toString();
//        Log.d("debug",tt);
//        return tt;
//    }
    class QueryAddressTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            // 查询翻译结果

            try {
                //尝试着将其作为单词识别
                String[] temp = TransXML.getResults(params[0]);
                result = temp[3] + "\n" + temp[1];
                result = result + "\n" + TransXML.relatedSentence(params[0]);
            } catch (Exception e) {
                String input = params[0];
                input = input.replaceAll("\\+", " ");
                boolean isEnglish = isEnglish(input);
                try {
                    if (isEnglish)
                        result = TransBD.transIntoZh(input);
                    else
                        result = TransBD.transIntoEn(input);
                    Log.e("result: ", result);
                }
                catch (Exception e1) {
                    showTransError();
                    Log.e("BackgroundException: ", e1.toString());
                }
            }
            //将结果返回给onPostExecute方法
            return result;
        }

        @Override
        //此方法可以在主线程改变UI
        protected void onPostExecute(String result) {
            if (result == null || result == "") {
                progressBar.setVisibility(View.GONE);
                showTransError();
                return;
            }
            // 将WebService返回的结果显示在TextView中
            progressBar.setVisibility(View.INVISIBLE);
            viewResult.setVisibility(View.VISIBLE);
            textViewResult.setText(result);
            progressBar.setVisibility(View.GONE);
        }
    }

    //获得所有的参数分割后的数组

    /**end*/
}
