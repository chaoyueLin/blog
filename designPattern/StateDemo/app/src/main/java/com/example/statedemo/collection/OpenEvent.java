package com.example.statedemo.collection;

/*****************************************************************
 * * File: - OpenEvent
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/8/5
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/8/5    1.0         create
 ******************************************************************/
public class OpenEvent implements Event {
    @Override
    public String execute() {
        return "opened";
    }
}
