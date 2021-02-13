package com.example.statedemo.state;

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

    private EntranceMachineState locked = new LockedEntranceMachineState();

    private EntranceMachineState unlocked = new UnlockedEntranceMachineState();

    private EntranceMachineState state;

    public EntranceMachine(EntranceMachineState state) {
        this.state = state;
    }

    public String execute(Action action) {
        if (Objects.isNull(action)) {
            throw new InvalidActionException();
        }

        if (Action.PASS.equals(action)) {
            return state.pass(this);
        }

        return state.insertCoin(this);
    }

    public boolean isUnlocked() {
        return state == unlocked;
    }

    public boolean isLocked() {
        return state == locked;
    }

    public String open() {
        setState(unlocked);
        return "opened";
    }

    public String alarm() {
        setState(locked);
        return "alarm";
    }

    public String refund() {
        setState(unlocked);
        return "refund";
    }

    public String close() {
        setState(locked);
        return "closed";
    }

    private void setState(EntranceMachineState state) {
        this.state = state;
    }
}
