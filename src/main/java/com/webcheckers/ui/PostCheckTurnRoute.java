package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.*;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.Objects;

import static spark.Spark.halt;

public class PostCheckTurnRoute implements Route {


    //
    // Attributes
    //
    private final GameCenter gameCenter;

    //
    // Constructor
    //

    PostCheckTurnRoute (final GameCenter gameCenter)
    {
        Objects.requireNonNull(gameCenter, "gameCenter must not be null");
        this.gameCenter = gameCenter;
    }

    @Override
    public synchronized Object handle(Request request, Response response) {
        // Get Session
        Session session =  request.session();

        //  Check if player is NOT logged in
        if(gameCenter.isPlayerLoggedIn(session) == false) {
            //not logged in
            response.redirect(WebServer.SIGNIN_URL);
            halt();
            return null;
        }

        //validate user state
        Player currentPlayer = gameCenter.getPlayer(request.session());


        // Get Game
        Game game = gameCenter.getActiveGame(session);

        //check if player has an active game, if yes, then return true to refresh the page nad redirect the user
        if(game == null){
           return true;
        }

        //check if it is the current player's turn
        if(game.isMyTurn(session.attribute(GameRoute.PLAYER_ID_ATT))){
            return true;
        }

        //if game is not over, and it not the current player turn, then do nothing by returning false
        return false;
    }
}
