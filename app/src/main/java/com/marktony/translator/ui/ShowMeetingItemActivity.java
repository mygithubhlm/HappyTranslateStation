package com.marktony.translator.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.marktony.translator.R;

public class ShowMeetingItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_meeting_item);

        Intent intent = getIntent();
//        LayoutInflater li = this.getLayoutInflater();
//        final View view = li.inflate(R.layout.edit_meeting,null);
        EditText showTitle = (EditText) findViewById(R.id.show_title);
        EditText showTime = (EditText) findViewById(R.id.show_time);
        EditText showInput = (EditText) findViewById(R.id.show_input);
        EditText showOutput = (EditText) findViewById(R.id.show_output);

        showTitle.setText(intent.getStringExtra("title"));
        showTime.setText(intent.getStringExtra("time"));
        showInput.setText(intent.getStringExtra("input"));
        showOutput.setText(intent.getStringExtra("output"));


    }
}
