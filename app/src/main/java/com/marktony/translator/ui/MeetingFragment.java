package com.marktony.translator.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marktony.translator.R;
import com.marktony.translator.adapter.MeetingItemAdapter;
import com.marktony.translator.db.MeetingDBUtil;
import com.marktony.translator.db.MeetingDatebaseHelper;
import com.marktony.translator.interfaze.OnRecyclerViewOnClickListener;
import com.marktony.translator.model.MeetingItem;
import com.marktony.translator.operate.Operate;
import com.marktony.translator.operate.TransBD;
import com.marktony.translator.operate.TransXML;
import com.marktony.translator.operate.Voice;
import com.marktony.translator.util.AudioRecorderUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MeetingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeetingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeetingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int EDIT = 1;
    private final int CREATE = 2;
    static final int VOICE_REQUEST_CODE = 66;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static String oo;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerViewMeeting;
    private FloatingActionButton fabMeeting;
    private FloatingActionButton fabMeetingCreate;
    private FloatingActionButton fabMeetingEdit;
    private ArrayList<MeetingItem> list = new ArrayList<MeetingItem>();
    private MeetingItemAdapter adapter;
    private TextView tvNoNoteMeeting;
    private boolean isClicked = false;
    private boolean editSpeakClicked = false;
    private boolean isCreateButton = false;
    private MeetingDatebaseHelper dbHelper;

    private Context context;
    private AudioRecorderUtils mAudioRecoderUtils = new AudioRecorderUtils();
    private long temp = 0;
    public MeetingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeetingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeetingFragment newInstance(String param1, String param2) {
        MeetingFragment fragment = new MeetingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        dbHelper = new MeetingDatebaseHelper(getActivity(),"MyStoreMeeting2.db",null,1);

        //录音回调函数
        mAudioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecorderUtils.OnAudioStatusUpdateListener() {

            //录音中....db为声音分贝，time为录音时长

            @Override
            public void onUpdate(double db, long time) {
                //mImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));

                //mTextView.setText(TimeUtils.long2String(time));
                if(time - temp > 1000) {

                    if (db<33) {
                        //保存文件
                        Log.i("update","结束录音，时长："+(time-temp));
                        mAudioRecoderUtils.stopRecord();
                        //创建下一段录音
//                        mAudioRecoderUtils.startRecord();
                    }
                    Log.i("lessThan500","此时分贝："+db);
                    temp = time;
                }


            }

            //录音结束，filePath为保存路径
            @Override
            public void onStop(String filePath) {
                Log.i("Save","录音保存在：" + filePath);
                //String param = new String(org.kobjects.base64.Base64.encode(Operate.getBytes(filePath)));
//                org.kobjects.base64.Base64.
//                param = java.net.URLEncoder.encode(param,"utf-8");
//
                //Log.e("onStop: ", param);
                //启动后台异步线程进行连接webService操作，并且根据返回结果在主线程中改变UI
                MeetingFragment.QueryAddressTask queryAddressTask = new MeetingFragment.QueryAddressTask();
                //启动后台任务
//                File newFile = new File(filePath);
                queryAddressTask.execute(filePath);

//                Log.i("result",re);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meeting,container,false);
        initView(view);
        fabMeeting.setOnClickListener(new FabMeetingListener());
        getDataFromDB();

        if (list.isEmpty()){
            tvNoNoteMeeting.setVisibility(View.VISIBLE);
        }

        Collections.reverse(list);
        adapter = new MeetingItemAdapter(getActivity(),list);
        recyclerViewMeeting.setAdapter(adapter);
        adapter.setItemClickListener(new OnRecyclerViewOnClickListener() {

            @Override
            public void OnItemClick(View view, int position) {
                Toast.makeText(getActivity(), "meeting item clicked", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void OnSubViewClick(View view, int position) {
                //点击编辑图标弹出对话框
                if(view.getId()==R.id.image_view_edit){
                    Toast.makeText(getActivity(), "meeting item edit clicked", Toast.LENGTH_SHORT).show();
                    createDialog(position);
                }else if(view.getId()==R.id.text_view_title){
                    Toast.makeText(getActivity(), "meeting item title clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),ShowMeetingItemActivity.class);
                    MeetingItem tempItem = list.get(position);
                    intent.putExtra("title",tempItem.getTitle());
                    intent.putExtra("time",tempItem.getTime());
                    intent.putExtra("input",tempItem.getInput());
                    intent.putExtra("output",tempItem.getOutput());

                    startActivity(intent);

                }
            }

        });
        return view;//inflater.inflate(R.layout.fragment_meeting, container, false);
    }
    public void initView(View view){
        recyclerViewMeeting = (RecyclerView) view.findViewById(R.id.recycler_view_meeting);
        recyclerViewMeeting.setLayoutManager(new LinearLayoutManager(getActivity()));

        fabMeeting = (FloatingActionButton) view.findViewById(R.id.fab_meeting);

        tvNoNoteMeeting = (TextView) view.findViewById(R.id.tv_no_note_meeting);
    }


    @Override
    public void onResume() {
        super.onResume();
        getDataFromDB();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        getDataFromDB();
    }

    private void getDataFromDB() {
        if (list != null) {
            list.clear();
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("meeting",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String in = cursor.getString(cursor.getColumnIndex("input"));
                String out = cursor.getString(cursor.getColumnIndex("output"));

                MeetingItem item = new MeetingItem(title,time,in,out);
                list.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    //点击编辑图标之后弹出对话框
    public void createDialog(final int position){
        final AlertDialog dialogMeeting = new AlertDialog.Builder(getActivity()).create();
        dialogMeeting.setMessage("你想干啥？");
        //dialogMeeting.setTitle("edit this meeting！");

        //添加按钮"Edit"
        dialogMeeting.setButton(DialogInterface.BUTTON_NEUTRAL, "编辑", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Edit is clicked!", Toast.LENGTH_SHORT).show();
                editMeetingItem(position);
            }
        });

        //添加按钮"Cancel"
        dialogMeeting.setButton(DialogInterface.BUTTON_NEGATIVE, "取消" , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        //添加按钮"Delete"
        dialogMeeting.setButton(DialogInterface.BUTTON_POSITIVE, "删除", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Delete is clicked!", Toast.LENGTH_SHORT).show();

                final MeetingItem toDelete = list.get(position);
                MeetingDBUtil.deleteValue(dbHelper,toDelete.getTitle());
                list.remove(position);

                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position,list.size());
            }
        });

        //显示对话框
        dialogMeeting.show();
    }

    TextInputEditText editTitle;// = (TextInputEditText) v.findViewById(R.id.edit_meeting_title);
    TextInputEditText editTime;// = (TextInputEditText) v.findViewById(R.id.edit_meeting_time);
    EditText editInput;// = (EditText) v.findViewById(R.id.edit_meeting_input);
    EditText editOutput;// = (EditText) v.findViewById(R.id.edit_meeting_output);
    String editInputStr;
    String editOutpuStr;
    public void editMeetingItem(final int position){
        final AlertDialog editMeeting = new AlertDialog.Builder(getActivity()).create();
        //标题
        editMeeting.setTitle("Edit this meeting!");
        //设置页面
        LayoutInflater li = getActivity().getLayoutInflater();
        final View v = li.inflate(R.layout.edit_meeting,null);

        //初始化标题和时间
        final MeetingItem toEdit = list.get(position);


        editTitle = (TextInputEditText) v.findViewById(R.id.edit_meeting_title);
        editTime = (TextInputEditText) v.findViewById(R.id.edit_meeting_time);
        editInput = (EditText) v.findViewById(R.id.edit_meeting_input);
        editOutput = (EditText) v.findViewById(R.id.edit_meeting_output);

        //设置编辑页面下的fab按钮属性。
        fabMeetingEdit = (FloatingActionButton) v.findViewById(R.id.fab_edit);
        fabMeetingEdit.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        fabMeetingEdit.setClickable(true);
        //fabMeetingEdit.setOnClickListener(new FabCreateListener());
        fabMeetingEdit.setOnClickListener(new FabEditListener());
        editTitle.setText(toEdit.getTitle());
        editTime.setText(toEdit.getTime());
        editInputStr = toEdit.getInput();
        editOutpuStr = toEdit.getOutput();
        editInput.setText(editInputStr);
        editOutput.setText(editOutpuStr);


        editMeeting.setView(v);
        //添加按钮"保存"
        editMeeting.setButton(DialogInterface.BUTTON_POSITIVE, "保存", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editTitle.getText().toString().isEmpty() || editTime.getText().toString().isEmpty()){

                    Snackbar.make(fabMeeting, R.string.no_input, Snackbar.LENGTH_SHORT).show();

                }else {
                    //将原有的删除
                    MeetingDBUtil.deleteValue(dbHelper, toEdit.getTitle());
                    list.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, list.size());
                    //插入修改后的item

                    MeetingItem toInsert = new MeetingItem(editTitle.getText().toString(), editTime.getText().toString(), editInput.getText().toString(), editOutput.getText().toString());
                    insertItem(toInsert);
                }
            }
        });
        //添加按钮"取消"
        editMeeting.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                StopListener();
            }
        });
        editMeeting.show();
    }

//    插入一条会议
    public void insertItem(MeetingItem item){
        ContentValues values = new ContentValues();
        values.put("title",item.getTitle());
        values.put("time",item.getTime());

        if(item.getInput().isEmpty()){
            values.put("input","");
        }else{
            values.put("input",item.getInput());
        }

        if(item.getOutput().isEmpty()){
            values.put("output","");
        }else{
            values.put("output",item.getOutput());
        }

        MeetingDBUtil.insertValue(dbHelper,values);

        values.clear();

        list.add(0,item);
        adapter.notifyItemInserted(0);
        recyclerViewMeeting.smoothScrollToPosition(0);
    }


    TextInputEditText etInputTitle;// = (TextInputEditText) view1.findViewById(R.id.create_meeting_title);
    TextInputEditText etOutputTime;// = (TextInputEditText) view1.findViewById(R.id.create_meeting_time);
    EditText etInput;
    EditText etoutput;
    //创建会议按钮点击事件
    class FabMeetingListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            final AlertDialog dialogMeeting = new AlertDialog.Builder(getActivity()).create();
            dialogMeeting.setTitle("添加会议");
            LayoutInflater li = getActivity().getLayoutInflater();
            final View view1 = li.inflate(R.layout.create_meeting,null);

            TextInputEditText textViewTime = (TextInputEditText) view1.findViewById(R.id.create_meeting_time);
            fabMeetingCreate = (FloatingActionButton) view1.findViewById(R.id.fab_create);
            fabMeetingCreate.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            fabMeetingCreate.setClickable(true);
            fabMeetingCreate.setOnClickListener(new FabCreateListener());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String time = df.format(new Date());
            textViewTime.setText(time);

            dialogMeeting.setView(view1);
            etInputTitle = (TextInputEditText) view1.findViewById(R.id.create_meeting_title);
            etOutputTime = (TextInputEditText) view1.findViewById(R.id.create_meeting_time);
            etInput = (EditText) view1.findViewById(R.id.create_meeting_input);
            etoutput = (EditText) view1.findViewById(R.id.create_meeting_output);


            //添加按钮"OK"
            dialogMeeting.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Toast.makeText(getActivity(), "hahah2", Toast.LENGTH_SHORT).show();


                    String title = etInputTitle.getText().toString();
                    String time =  etOutputTime.getText().toString();
                    String input = etInput.getText().toString();
                    String output = etoutput.getText().toString();

                    if (title.isEmpty() || time.isEmpty()){

                        Snackbar.make(fabMeeting, R.string.no_input, Snackbar.LENGTH_SHORT).show();

                    } else {

                        if (tvNoNoteMeeting.getVisibility() == View.VISIBLE){
                            tvNoNoteMeeting.setVisibility(View.GONE);
                        }

                        MeetingItem item = new MeetingItem(title,time,input,output);
                        insertItem(item);
                    }
                }
            });
            //添加按钮"Cancel"
            dialogMeeting.setButton(DialogInterface.BUTTON_NEGATIVE, "取消" , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    StopListener();
                }
            });
            //显示对话框
            dialogMeeting.show();
        }
    }

    //创建会议内的fab按钮点击事件
    class FabCreateListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Log.i("Create","点击创建按钮");
            isCreateButton = true;
            requestPermissions(CREATE);
//            if (!isClicked) {
//                isClicked = true;
//                fabMeetingCreate.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
//            }else{
//                isClicked = false;
//                fabMeetingCreate.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
//            }
        }
    }

    //编辑会议内的fab按Edit钮点击事件
    class FabEditListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Log.i("Create","点击编辑按钮");
            isCreateButton = false;
            requestPermissions(EDIT);
        }
    }

   //判断权限是不是打开
    private void requestPermissions(int type) {

        //判断是否开启麦克风权限, 如果开了
        if ((ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            //当编辑按钮点击时
            if(type==EDIT){
                if (editSpeakClicked) {
                    Log.i("EditMrrting","按钮变白");
                    fabMeetingEdit.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    editSpeakClicked = false;
                    StopListener();

                }else{
                    Log.i("EditMrrting","按钮变红");
                    fabMeetingEdit.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    editSpeakClicked = true;
                    Log.i("editSpeak",editSpeakClicked+"");
                    StartListener();
                }
            }else{//当创建按钮点击时
                if (isClicked) {
                    Log.i("CreateMeeting","按钮变白");
                    fabMeetingCreate.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    isClicked = false;
                    StopListener();
                }else{
                    Log.i("CreateMeeting","按钮变红");
                    fabMeetingCreate.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    isClicked = true;
                    Log.i("createSpeak",isClicked+"");
                    StartListener();
                }
            }


            //判断是否开启语音权限， 如果没有
        } else {
            Toast.makeText(context, "没有麦克风权限", Toast.LENGTH_SHORT).show();
            //请求获取麦克风权限
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, VOICE_REQUEST_CODE);
        }

    }

    //请求权限的回调函数
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == VOICE_REQUEST_CODE) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED) ) {
                StartListener();
            } else {
                Toast.makeText(context, "已拒绝权限！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //开始录音
    public void StartListener(){
        //mPop.showAtLocation(rl, Gravity.CENTER, 0, 0);
        //mButton.setText("松开保存");
        mAudioRecoderUtils.startRecord();
    }
    //结束录音
    public void StopListener(){
        mAudioRecoderUtils.stopRecord();
    }






    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    class QueryAddressTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
//            Log.e("doInBackground:", params[0]);
            // 查询翻译结果
            String re = Voice.voiceReco(params[0],"zh");

            Log.e("doInBackground: ", re);
            oo=TransBD.transIntoZh(re);

            return re;

//            try {
//                //尝试着将其作为单词识别
//                String[] temp = TransXML.getResults(params[0]);
//                result = temp[3] + "\n" + temp[1];
//                result = result + "\n" + TransXML.relatedSentence(params[0]);
//            } catch (Exception e) {
//                String input = params[0];
//                input = input.replaceAll("\\+", " ");
//                boolean isEnglish = isEnglish(input);
//                try {
//                    if (isEnglish)
//                        result = TransBD.transIntoZh(input);
//                    else
//                        result = TransBD.transIntoEn(input);
//                    Log.e("result: ", result);
//                }
//                catch (Exception e1) {
//                    showTransError();
//                    Log.e("BackgroundException: ", e1.toString());
//                }
//            }
//            //将结果返回给onPostExecute方法
//            return result;
        }

        @Override
        //此方法可以在主线程改变UI
        protected void onPostExecute(String result) {

            String temp = result;
            Log.i("editbutton",editSpeakClicked+"");
            Log.i("createbutton",isClicked+"");
            if(!isCreateButton) {
                editInput.setText(temp);
                editOutput.setText(editOutpuStr + oo);
            }else{
                etInput.setText(temp);
                etoutput.setText(oo);
            }
//            if (isClicked){
//                etInput.setText(temp);
//                etInput.setText(editInputStr + oo);
//            }
//            if (result == null || result == "") {
//                progressBar.setVisibility(View.GONE);
//                showTransError();
//                return;
//            }
//            // 将WebService返回的结果显示在TextView中
//            progressBar.setVisibility(View.INVISIBLE);
//            viewResult.setVisibility(View.VISIBLE);
//            textViewResult.setText(result);
//            progressBar.setVisibility(View.GONE);
        }
    }
}
