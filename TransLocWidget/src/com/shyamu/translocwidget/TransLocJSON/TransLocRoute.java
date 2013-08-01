package com.shyamu.translocwidget.TransLocJSON;

public class TransLocRoute{

    public TransLocRoute(int id, String shortName, String longName){
        this.id=id;
        this.shortName=shortName;
        this.longName=longName;
    }

    public int id;
    public String shortName;
    public String longName;

    public String toString(){
        return shortName+" "+longName;
    }
}