package com.webcheckers.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.*;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.Objects;
import java.util.Set;

import static spark.Spark.halt;

public class PostBackupMoveRoute implements Route {

    static final String VIEW_NAME = "game.ftl";

    static final String TITLE = "Checker Game";
    static final String SUCCESS_MSG = "The last move was cancelled successfully";
    static final String ERROR_MSG = "Sorry, we couldn't cancel your last move.";
    //
    // Attributes
    //
    private final GameCenter gameCenter;

    //
    // Constructor
    //

    PostBackupMoveRoute (final GameCenter gameCenter)
    {
        Objects.requireNonNull(gameCenter, "gameCenter must not be null");
        this.gameCenter = gameCenter;
    }

    @Override
    public synchronized Object handle(Request request, Response response) {

        // Get Session
        Session session =  request.session();

                //check if player is NOT logged in
        if(gameCenter.isPlayerLoggedIn( session) == false) {
            //not logged in
            response.redirect(WebServer.SIGNIN_URL);
            halt();
            return null;
        }


        //validate user state
        Player currentPlayer = gameCenter.getPlayer(request.session());

        //validate player status
        if(currentPlayer.getStatus() != Player.State.IN_GAME){
            gameCenter.redirectPlayer(request.session(),response);
        }

        // Get Game
        Game game = gameCenter.getActiveGame(session);

        //check if player has an active game
        if(game == null){
            response.redirect(WebServer.HOME_URL);
            halt();
            return null;
        }
        //System.out.println("state="+ game.getState()+",boad multi=" +  game.getBoard().isMultiCap()+",isskipping="+game.getIsPlayerSkippingFlag());
        if(game.getIsPlayerSkippingFlag() == false){
            game.resetPendingBoard();
            game.setState(GameState.IDLE);
            return new Message(SUCCESS_MSG,MessageType.info);
        }
        else{
            return new Message(ERROR_MSG,MessageType.error);
        }
    }
}
