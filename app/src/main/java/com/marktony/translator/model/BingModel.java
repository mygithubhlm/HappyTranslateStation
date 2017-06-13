package com.marktony.translator.model;

import java.util.ArrayList;

/**
 * Created by think on 2017/5/3.
 */

public class BingModel {

    private String word;
    private Pronunciation pronunciation;
    private ArrayList<Definition> defs;
    private ArrayList<Sample> sams;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Pronunciation getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(Pronunciation pronunciation) {
        this.pronunciation = pronunciation;
    }

    public ArrayList<Definition> getDefs() {
        return defs;
    }

    public void setDefs(ArrayList<Definition> defs) {
        this.defs = defs;
    }

    public ArrayList<Sample> getSams() {
        return sams;
    }

    public void setSams(ArrayList<Sample> sams) {
        this.sams = sams;
    }

    public class Pronunciation {

        private String AmE;
        private String AmEmp3;
        private String BrE;
        private String BrEmp3;

        public String getAmE() {
            return AmE;
        }

        public void setAmE(String amE) {
            AmE = amE;
        }

        public String getAmEmp3() {
            return AmEmp3;
        }

        public void setAmEmp3(String amEmp3) {
            AmEmp3 = amEmp3;
        }

        public String getBrE() {
            return BrE;
        }

        public void setBrE(String brE) {
            BrE = brE;
        }

        public String getBrEmp3() {
            return BrEmp3;
        }

        public void setBrEmp3(String brEmp3) {
            BrEmp3 = brEmp3;
        }
    }

    public class Definition {

        private String pos;
        private String def;

        public String getPos() {
            return pos;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }

        public String getDef() {
            return def;
        }

        public void setDef(String def) {
            this.def = def;
        }
    }

    public class Sample {

        private String eng;
        private String chn;
        private String mp3Url;
        private String mp4Url;

        public String getEng() {
            return eng;
        }

        public void setEng(String eng) {
            this.eng = eng;
        }

        public String getChn() {
            return chn;
        }

        public void setChn(String chn) {
            this.chn = chn;
        }

        public String getMp3Url() {
            return mp3Url;
        }

        public void setMp3Url(String mp3Url) {
            this.mp3Url = mp3Url;
        }

        public String getMp4Url() {
            return mp4Url;
        }

        public void setMp4Url(String mp4Url) {
            this.mp4Url = mp4Url;
        }
    }

}
