package com.webcheckers.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.*;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static spark.Spark.halt;

public class ValidateRoute implements Route {

    static final String VIEW_NAME = "game.ftl";

    static final String TITLE = "Checker Game";
    static final String SKIPAVAILABLE = "Invalid move. You're in the process of capturing multiple pieces.";
    static final String MOVEPENDING = "You have a move pending to be submitted";

    //
    // Attributes
    //
    private final GameCenter gameCenter;

    //
    // Constructor
    //

    ValidateRoute (final GameCenter gameCenter)
    {
        Objects.requireNonNull(gameCenter, "gameCenter must not be null");
        this.gameCenter = gameCenter;
    }

    @Override
    public synchronized Object handle(Request request, Response response) {

        // Get Session
        Session session =  request.session();

        // Get Game
        Game game = gameCenter.getActiveGame(session);

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

        //check if player has an active game
        if(game == null){
            response.redirect(WebServer.HOME_URL);
            halt();
            return null;
        }

        if (game.getState() == GameState.PENDING)
        {
            return new Message(MOVEPENDING, MessageType.error);
        }

        // Get Move
        Set<String> JSONString = request.queryParams();
        Move move = new Gson().fromJson(JSONString.toArray()[0].toString(), new TypeToken<Move>(){}.getType());

        // Get unchanged Board
        Board baseBoard = game.getBoard();

        //Make sure pendingBoard initially matches current board
        //if (!baseBoard.isMultiCap()) { game.resetPendingBoard(); }


        // Get Pending Board
        Board pendingBoard = game.getPendingBoard();

        //Validate move
        GameRules rules = new GameRules();
        Message msg = null;

        // Check if we're in the process of a skip
        if (game.getState() == GameState.SKIP)
        {
            //register that user is deciding on a second move
            game.setIsPlayerSkippingFlag(true);

            if (!baseBoard.getCurrentStep().equals(move.getStart()))
            {
                msg = new Message(SKIPAVAILABLE, MessageType.error);
                return msg;
            }
            else
            {
                baseBoard = pendingBoard.clone();
            }
        }

        msg =  rules.validateMove(game, move, pendingBoard, baseBoard);

        if (msg.getType() == MessageType.info)
        {
            game.getBoard().setMultiCap(baseBoard.isMultiCap());
        }

        game.setMessage(msg);

        return msg;
    }
}
