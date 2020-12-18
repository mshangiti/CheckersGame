package com.webcheckers.ui;

import static spark.Spark.halt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;
import java.util.Objects;

import com.webcheckers.Application;
import com.webcheckers.model.Challenge;
import com.webcheckers.appl.GameCenter;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import com.webcheckers.model.Player;


public class PostSignInRoute implements TemplateViewRoute {

    //
    // Constants
    //
    static final String MESSAGE_ATTR = "errorMessage";
    static final String TITLE = "Welcome";

    //
    // Attributes
    //
    private final GameCenter gameCenter;

    //
    // Constructor
    //

    /**
     * The constructor for the {@code POST /signin} route handler.
     */
    PostSignInRoute(final GameCenter gameCenter)
    {
        Objects.requireNonNull(gameCenter, "gameCenter must not be null");
        this.gameCenter = gameCenter;
    }


    //
    // Error messages
    //

    /**
     * Make an error message when username is already in use
     */
    static String usernameAlreadyInUseMessage() {
        return String.format("Sorry, the username you selected is already in-use, kindly try a different username.");
    }

    /**
     * Make an error message when username is longer than allowed length
     */
    static String usernameTooLongMessage() {
        return String.format("Sorry, the username cannot be longer than 30 chars.");
    }

    /**
     * Make an error message when username is empty
     */
    static String usernameEmptyMessage() {
        return String.format("Sorry, the username cannot be empty.");
    }

    /**
     * Make an error message when username has a char that is not a number or a letter
     */
    static String usernameInvalidCharsMessage() {
        return String.format("Sorry, only letters or numbers are allowed in the username, kindly try a different username.");
    }

    //
    // TemplateViewRoute method
    //

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView handle(Request request, Response response) {
        // start building the View-Model
        final Map<String, Object> vm = new HashMap<>();
        vm.put("title", PostSignInRoute.TITLE);


        //get the username selected by user
        final String username = request.queryParams(gameCenter.SESSION_USER);

        //validate username is within allowed length
        if(username.length()==0){
            return error(vm, usernameEmptyMessage());
        }
        if(username.length()>30){
            return error(vm, usernameTooLongMessage());
        }

        //validate username contains only chars or numbers
        Pattern pattern = Pattern.compile("\\p{Alnum}+");
        Matcher matcher = pattern.matcher(username);
        if (!matcher.matches()) {
            // found invalid char
            return error(vm, usernameInvalidCharsMessage());
        }

        //check if username exists or not
        if(gameCenter.isPlayerNameAvailable(username)){
            //username is available

            //add user to active users list
            Player player = new Player(username);
            gameCenter.addPlayer(player, request.session());

            //redirect to lobby
            vm.put(gameCenter.SESSION_USER, username);
            return signInSuccessful(vm, request);
        }else{
            //username is not available
            return error(vm, usernameAlreadyInUseMessage());
        }
    }

    /**
     * private methods
     */

    private ModelAndView signInSuccessful(final Map<String, Object> vm, Request req) {
        ArrayList<Player> onlinePlayers = gameCenter.getOnlinePlayers();
        vm.put(HomeController.PLAYER_LIST_ATTR, onlinePlayers);
        vm.put(HomeController.PLAYER_COUNT_ATTR, onlinePlayers.size()-1);
        vm.put(HomeController.PLAYER_CHALLENGE_STATUS, Challenge.State.NO_CHALLENGES);
        vm.put(HomeController.IS_CHALLENGED_ATTR, false);
        vm.put(HomeController.OPPONENT_NAME_ATTR, "");
        return new ModelAndView(vm, HomeController.VIEW_NAME);
    }

    private ModelAndView error(final Map<String, Object> vm, final String message) {
        vm.put(MESSAGE_ATTR, message);
        return new ModelAndView(vm, GetSignInRoute.VIEW_NAME);
    }
}
