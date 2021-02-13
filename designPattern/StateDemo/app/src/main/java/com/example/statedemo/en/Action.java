package com.example.statedemo.en;

/*****************************************************************
 * * File: - Action
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/8/5
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/8/5    1.0         create
 ******************************************************************/
public enum Action {
    PASS {
        @Override
        public String execute(EntranceMachine entranceMachine, EntranceMachineState state) {
            return state.pass(entranceMachine);
        }
    },
    INSERT_COIN {
        @Override
        public String execute(EntranceMachine entranceMachine, EntranceMachineState state) {
            return state.insertCoin(entranceMachine);
        }
    };

    public abstract String execute(EntranceMachine entranceMachine, EntranceMachineState state);
}
