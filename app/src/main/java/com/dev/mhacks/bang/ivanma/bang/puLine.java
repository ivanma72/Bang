package com.dev.mhacks.bang.ivanma.bang;

public class puLine{
    private int key;
    private String line;

    public puLine(){
        key = 0;
        line = "";
    }

    public puLine(String line){
        super();
        this.line = line;
    }
    public int getKey(){
        return this.key;
    }
    public void setKey(int key){
        this.key = key;
    }
    public String getLine(){
        return this.line;
    }
    public void setLine(String line){
        this.line = line;
    }
    //is this needed?
    @Override
    public String toString(){
        return "Line [id=" + key + ", line" + line + "]";
    }
}