package com.marktony.translator.model;

/**
 * Created by think on 2017/5/3.
 */

public class NotebookMarkItem {

    // 原文
    private String input = null;
    // 译文
    private String output = null;

    public NotebookMarkItem(String input,String output){
        this.input = input;
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }
}
