package com.webcheckers.model;
import java.util.regex.*;
/*
 * This class represents a Player playing a game
 */
public class Player {

    /*
     * This enum represents a challenge status
     */

    public enum State {
        IN_LOBBY,IN_GAME
    }

    //CONSTANTS
    private final String NAME_TOO_LONG = "Sorry, the username cannot be longer than 30 chars.";
    private final String NAME_TOO_SHORT = "Sorry, the username cannot be empty.";
    private final String NAME_ILLG_CHARS = "Sorry, only letters or numbers are allowed in the username, kindly try a different username.";

    //
    // Attributes
    //
    private String name;
    private State state;

    /**
     * Create a new player
     *
     * @param name
     *          The username selected by the player
     *
     */
    public Player(String name){

        //validate username is within allowed length
        if(name.length()==0){
            throw new IllegalArgumentException(NAME_TOO_SHORT);
        }
        if(name.length()>30){
            throw new IllegalArgumentException(NAME_TOO_LONG);
        }

        //validate username contains only chars or numbers
        Pattern pattern = Pattern.compile("\\p{Alnum}+");
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            // found invalid char
            throw new IllegalArgumentException(NAME_ILLG_CHARS);
        }

        //all good, assign name
        this.name = name;
        this.state = State.IN_LOBBY;
    }

    //
    // Getters and Setters
    //

    //name
    public String getName(){
        return this.name;
    }

    //availability
    public void setStatus(State param){this.state = param;}
    public State getStatus(){return this.state;}


}
