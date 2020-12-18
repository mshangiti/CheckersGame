package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Game;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static spark.Spark.halt;

public class GameRoute implements TemplateViewRoute {

    static final String VIEW_NAME = "game.ftl";

    static final String TITLE = "Checker Game";
    static final String PLAYER_ATTR = "currentPlayer";
    static final String PLAYER_NAME_ATTR = "playerName";
    static final String PLAYER_COLOR_ATTR = "playerColor";
    static final String OPPONENT_NAME_ATTR = "opponentName";
    static final String OPPONENT_COLOR_ATTR = "opponentColor";
    static final String TURN_ATTR = "isMyTurn";
    static final String BOARD_ATTR = "board";

    //
    // Attributes
    //
    private final GameCenter gameCenter;

    //
    // Constructor
    //

    GameRoute (final GameCenter gameCenter)
    {
        Objects.requireNonNull(gameCenter, "gameCenter must not be null");
        this.gameCenter = gameCenter;
    }

    @Override
    public ModelAndView handle(Request request, Response response) {
        Map<String, Object> vm = new HashMap<>();
        vm.put("title", this.TITLE);
        Session session =  request.session();
        //check if player is NOT logged in
        if(gameCenter.isPlayerLoggedIn(session) == false) {
          //not logged in
          response.redirect(WebServer.SIGNIN_URL);
          halt();
          return null;
        }

        //check if player has an active game
        Game game = gameCenter.getActiveGame(session);
        if(game == null){
            response.redirect(WebServer.HOME_URL);
            halt();
            return null;
        }


        //send game attributes to VIEW
        int playerId = session.attribute(Game.PLAYER_ID_ATT);
        int opponentId = 2;
        if(playerId==2){opponentId=1;}
        vm.put(WebServer.TITLE_ATTR, GameRoute.TITLE);
        vm.put(GameRoute.PLAYER_ATTR, game.getPlayer(playerId));
        vm.put(GameRoute.PLAYER_NAME_ATTR, game.getPlayerName(playerId));
        vm.put(GameRoute.OPPONENT_NAME_ATTR, game.getPlayerName(opponentId));
        vm.put(GameRoute.PLAYER_COLOR_ATTR, game.getPlayerColor(playerId));
        vm.put(GameRoute.OPPONENT_COLOR_ATTR, game.getPlayerColor(opponentId));
        vm.put(GameRoute.TURN_ATTR, game.isMyTurn(playerId));
        vm.put(GameRoute.BOARD_ATTR, game.getBoard());

        return new ModelAndView(vm, this.VIEW_NAME);
    }
}
