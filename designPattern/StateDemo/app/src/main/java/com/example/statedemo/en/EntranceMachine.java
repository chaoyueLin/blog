package com.example.statedemo.en;

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
        setState(state);
    }

    public void setState(EntranceMachineState state) {
        this.state = state;
    }

    public String execute(Action action) {
        if (Objects.isNull(action)) {
            throw new InvalidActionException();
        }

        return action.execute(this, state);
    }

    public String open() {
        return "opened";
    }

    public String alarm() {
        return "alarm";
    }

    public String refund() {
        return "refund";
    }

    public String close() {
        return "closed";
    }
}
