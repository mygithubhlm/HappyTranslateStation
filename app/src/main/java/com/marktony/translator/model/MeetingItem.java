package com.marktony.translator.model;

/**
 * Created by think on 2017/5/3.
 */

public class MeetingItem {
    private String title;
    private String time;
    private String input;
    private String output;
    public MeetingItem(String title, String time, String input, String output){
        this.title = title;
        this.time = time;
        this.input = input;
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
