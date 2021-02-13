package com.example.statedemo.collection;

/*****************************************************************
 * * File: - AlarmEvent
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/8/5
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/8/5    1.0         create
 ******************************************************************/
public class AlarmEvent implements Event {
    @Override
    public String execute() {
        return "alarm";
    }
}
