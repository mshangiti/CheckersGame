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

public class PostResignRoute implements Route {

    //
    // Attributes
    //
    private final GameCenter gameCenter;

    //
    // Constructor
    //

    PostResignRoute(final GameCenter gameCenter) {
        Objects.requireNonNull(gameCenter, "gameCenter must not be null");
        this.gameCenter = gameCenter;
    }

    @Override
    public synchronized Object handle(Request request, Response response) {

        // Get Session
        Session session = request.session();

        //check if player is logged in
        if(gameCenter.isPlayerLoggedIn(request.session()) == false) {
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

        // Check if player has a game
        Game game = gameCenter.getActiveGame(session);
        if(game == null){
            response.redirect(WebServer.HOME_URL);
            halt();
            return null;
        }

        // end game
        game.setResignFlag(true);
        gameCenter.endGame(session,game.getOpponentId(session.attribute(GameRoute.PLAYER_ID_ATT)));

        //no need to return anything
        return null;
    }
}
