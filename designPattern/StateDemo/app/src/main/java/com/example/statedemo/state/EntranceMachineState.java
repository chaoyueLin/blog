package com.example.statedemo.state;

/*****************************************************************
 * * File: - EntranceMachineState
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/8/5
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/8/5    1.0         create
 ******************************************************************/
public interface EntranceMachineState {

    String insertCoin(EntranceMachine entranceMachine);

    String pass(EntranceMachine entranceMachine);
}
