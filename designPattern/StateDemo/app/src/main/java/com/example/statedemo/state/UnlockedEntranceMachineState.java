package com.example.statedemo.state;

/*****************************************************************
 * * File: - UnlockedEntranceMachineState
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/8/5
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/8/5    1.0         create
 ******************************************************************/
public class UnlockedEntranceMachineState implements EntranceMachineState {

    @Override
    public String insertCoin(EntranceMachine entranceMachine) {
        return entranceMachine.refund();
    }

    @Override
    public String pass(EntranceMachine entranceMachine) {
        return entranceMachine.close();
    }
}
