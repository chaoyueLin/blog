package com.example.statedemo.sw;

import com.example.statedemo.InvalidActionException;

import java.util.Objects;

/*****************************************************************
 * * File: - EntranceMachine
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/8/5
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/8/5    1.0         create
 ******************************************************************/
public class EntranceMachine {

    private EntranceMachineState state;

    public EntranceMachine(EntranceMachineState state) {
        this.state = state;
    }

    public void setState(EntranceMachineState state) {
        this.state = state;
    }

    public String execute(Action action) {
        if (Objects.isNull(action)) {
            throw new InvalidActionException();
        }

        if (EntranceMachineState.LOCKED.equals(state)) {
            switch (action) {
                case INSERT_COIN:
                    setState(EntranceMachineState.UNLOCKED);
                    return open();
                case PASS:
                    return alarm();
            }
        }

        if (EntranceMachineState.UNLOCKED.equals(state)) {
            switch (action) {
                case PASS:
                    setState(EntranceMachineState.LOCKED);
                    return close();
                case INSERT_COIN:
                    return refund();
            }
        }
        return null;
    }

    private String refund() {
        return "refund";
    }

    private String close() {
        return "closed";
    }

    private String alarm() {
        return "alarm";
    }

    private String open() {
        return "opened";
    }
}
