package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Game;
import com.webcheckers.model.GameState;
import com.webcheckers.model.Player;

import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static spark.Spark.halt;

public class GameRoute implements TemplateViewRoute {

    static final String VIEW_NAME = "game.ftl";
    public static  String PLAYER_ID_ATT = "playerId";
    static final String TITLE = "Checker Game";
    static final String PLAYER_ATTR = "currentPlayer";
    static final String PLAYER_NAME_ATTR = "playerName";
    static final String PLAYER_COLOR_ATTR = "playerColor";
    static final String OPPONENT_NAME_ATTR = "opponentName";
    static final String OPPONENT_COLOR_ATTR = "opponentColor";
    static final String TURN_ATTR = "isMyTurn";
    static final String BOARD_ATTR = "board";
    static final String MSG_ATTR = "message";

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
    public synchronized ModelAndView handle(Request request, Response response) {
        Map<String, Object> vm = new HashMap<>();
        vm.put("title", this.TITLE);
        Session session =  request.session();

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
        Game game = gameCenter.getGameByPlayer(currentPlayer.getName());
        if(game == null){
            response.redirect(WebServer.HOME_URL);
            halt();
            return null;
        }


        //check if game is completed
        if(game.getState() == GameState.OVER){
            response.redirect(WebServer.RESULT_URL);
            halt();
            return null;
        }


        //send game attributes to VIEW
        int playerId = session.attribute(PLAYER_ID_ATT);
        int opponentId = 2;
        if( playerId == 2 ){ opponentId = 1; }

        //If Pending/Skip (board refreshed automatically or forced) and your turn, reset state to Idle
        if (game.isMyTurn(playerId) && (game.getState() == GameState.PENDING || game.getState() == GameState.SKIP)) { game.setState(GameState.IDLE); }

        //Only reset skip and pending board if IDLE
        if (game.getState() == GameState.IDLE) {
            game.getBoard().setMultiCap(false);
            game.resetPendingBoard();
        }
        vm.put(WebServer.TITLE_ATTR, GameRoute.TITLE);
        vm.put(GameRoute.PLAYER_ATTR, game.getPlayer(playerId));
        vm.put(GameRoute.PLAYER_NAME_ATTR, game.getPlayerName(playerId));
        vm.put(GameRoute.OPPONENT_NAME_ATTR, game.getPlayerName(opponentId));
        vm.put(GameRoute.PLAYER_COLOR_ATTR, game.getPlayerColor(playerId));
        vm.put(GameRoute.OPPONENT_COLOR_ATTR, game.getPlayerColor(opponentId));
        vm.put(GameRoute.TURN_ATTR, game.isMyTurn(playerId));

        // If there's a skip and it's the player's turn, return the pending board instead of the actual board
        if ((game.getState() == GameState.SKIP) && (game.isMyTurn(session.attribute(PLAYER_ID_ATT))))
        {
            vm.put(GameRoute.BOARD_ATTR, game.getPendingBoard());
            vm.put(GameRoute.MSG_ATTR, game.getMessage());
        }
        else
        {
            vm.put(GameRoute.BOARD_ATTR, game.getBoard());
        }

        return new ModelAndView(vm, this.VIEW_NAME);
    }
}