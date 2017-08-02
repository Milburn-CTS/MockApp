package com.continentaltechsolutions.dell.mockapp.Business;

/**
 * Created by DELL on 02-Aug-17.
 */

public class sosmsg {
    private String name;
    private String num;

    public sosmsg(String name ,String num){
        this.name=name;
        this.num=num;

    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;


    }

    public String getNum() {
        return num;
    }
    public void setNum(String num){
        this.num = num;
    }
}
