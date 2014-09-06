package com.dev.mhacks.bang.ivanma.bang;

public class puLine{
    private long key;
    private String line;

    public puLine(){
        key = 0;
        line = "";
    }

    public puLine(long key_in,String line_in){
        key = key_in;
        line = line_in;
    }

    public long getKey(){
        return this.key;
    }
    public void setKey(long key){
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