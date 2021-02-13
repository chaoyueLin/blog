package com.example.statedemo.state;

/*****************************************************************
 * * File: - LockedEntranceMachineState
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/8/5
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/8/5    1.0         create
 ******************************************************************/
public class LockedEntranceMachineState implements EntranceMachineState {

    @Override
    public String insertCoin(EntranceMachine entranceMachine) {
        return entranceMachine.open();
    }

    @Override
    public String pass(EntranceMachine entranceMachine) {
        return entranceMachine.alarm();
    }
}
