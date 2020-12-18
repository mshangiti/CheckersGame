package com.webcheckers.model;


/*
 * This class represents a challenge
 */
public class Challenge {
    /*
     * This enum represents a challenge status
     */

    public enum State {
        NO_CHALLENGES, CHALLENGE_PENDING, CHALLENGE_ACCEPTED, CHALLENGE_DECLINED, CHALLENGE_CANCELLED
    }

    /*
    class attributes
     */
    private String challenger;
    private String opponent;
    private State status;

    public Challenge(String challenger, String opponent){
        this.challenger = challenger;
        this.opponent = opponent;
        this.status = State.CHALLENGE_PENDING;
    }

    /* getters and setters*/
    public String getChallengerName(){ return this.challenger; }
    public String getOpponentName(){ return this.opponent; }
    public void setStatus(State param){this.status = param;}
    public State getStatus(){return this.status;}
}

