package com.example.statedemo.collection;

import android.app.AlertDialog.Builder;

/*****************************************************************
 * * File: - EntranceMachineTransaction
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/8/4
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/8/4    1.0         create
 ******************************************************************/
public class EntranceMachineTransaction {

    private EntranceMachineState currentState;

    private Action action;

    private EntranceMachineState nextState;

    private Event event;


    public static EntranceMachineTransaction.Builder builder() {
        return new EntranceMachineTransaction.Builder();
    }

    public Action getAction() {
        return action;
    }

    public EntranceMachineState getCurrentState() {
        return currentState;
    }

    public EntranceMachineState getNextState() {
        return nextState;
    }

    public Event getEvent() {
        return event;
    }

    public static class Builder {
        private EntranceMachineState currentState;

        private Action action;

        private EntranceMachineState nextState;

        private Event event;


        public Builder currentState(EntranceMachineState state) {
            this.currentState = state;
            return this;
        }

        public Builder action(Action action) {
            this.action = action;
            return this;
        }

        public Builder nextState(EntranceMachineState state) {
            this.nextState = state;
            return this;
        }

        public Builder event(Event event) {
            this.event = event;
            return this;
        }

        public EntranceMachineTransaction build() {
            return new EntranceMachineTransaction(this);
        }

    }

    private EntranceMachineTransaction(Builder builder) {
        currentState = builder.currentState;
        action = builder.action;
        nextState = builder.nextState;
        event = builder.event;
    }
}
