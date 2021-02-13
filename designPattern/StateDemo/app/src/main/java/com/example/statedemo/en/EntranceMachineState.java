package com.example.statedemo.en;

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
public enum EntranceMachineState {
    LOCKED {
        @Override
        public String insertCoin(EntranceMachine entranceMachine) {
            entranceMachine.setState(UNLOCKED);
            return entranceMachine.open();
        }

        @Override
        public String pass(EntranceMachine entranceMachine) {
            entranceMachine.setState(this);
            return entranceMachine.alarm();
        }
    },
    UNLOCKED {
        @Override
        public String insertCoin(EntranceMachine entranceMachine) {
            entranceMachine.setState(this);
            return entranceMachine.refund();
        }

        @Override
        public String pass(EntranceMachine entranceMachine) {
            entranceMachine.setState(LOCKED);
            return entranceMachine.close();
        }
    };

    public abstract String insertCoin(EntranceMachine entranceMachine);

    public abstract String pass(EntranceMachine entranceMachine);
}