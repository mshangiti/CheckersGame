package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Game;
import com.webcheckers.model.GameState;
import com.webcheckers.model.Player;
import spark.*;
import com.webcheckers.appl.GameCenter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static spark.Spark.halt;

public class GetResultRoute implements TemplateViewRoute {
    //
    // constants
    //
    static final String VIEW_NAME = "result.ftl";
    static final String TITLE = "Welcome";
    static final String TITLE_ATTR = "title";
    static final String GAME_DECISION_ATTR = "gameDecision";
    static final String GAME_TIE_COUNT_ATT = "numberOfNoCaptureMoves";
    static final String GAME_MSG_ATT = "gameMessage";
    static final int NO_WINNERS = -1;
    static final int PLAYER_WON = 1;
    static final int PLAYER_LOST = 0;
    static final String PLAYER_CAPTURE_WON_MSG = "The game concluded as you have captured all your opponent pieces.";
    static final String PLAYER_CAPTURE_LOST_MSG = "The game concluded since all your pieces have been captured.";
    static final String PLAYER_BLOCKAGE_WON_MSG = "The game concluded as you have completely blocked your opponent movement.";
    static final String PLAYER_BLOCKAGE_LOST_MSG = "The game concluded since you have been completely blocked by your opponent (no possible moves left to make).";
    static final String PLAYER_RESIGN_WON_MSG = "The game concluded because your opponent quit the game.";
    static final String PLAYER_RESIGN_LOST_MSG = "The game concluded because you quit the game.";

    //
    // Attributes
    //
    private final GameCenter gameCenter;

    //
    // TemplateViewRoute method
    //

    //
    // Constructor
    //

    GetResultRoute (final GameCenter gameCenter)
    {
        Objects.requireNonNull(gameCenter, "gameCenter must not be null");
        this.gameCenter = gameCenter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView handle(Request request, Response response){
        // start building the View-Model
        final Map<String, Object> vm = new HashMap<>();
        vm.put(TITLE_ATTR, TITLE);
        Session session =  request.session();
        Player player = gameCenter.getPlayer(session);

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
        if(currentPlayer.getStatus() != Player.State.IN_RESULT){
            gameCenter.redirectPlayer(request.session(),response);
        }

        //update player status (player has seen result, state used to redirect to home or lobby)
        player.setStatus(Player.State.IN_HOME);

        //check if player has an active game
        Game game = gameCenter.getGameByPlayer(player.getName());
        if(game == null){
            response.redirect(WebServer.HOME_URL);
            halt();
            return null;
        }

        //check if game is completed
        if(game.getState() == GameState.OVER){
            //check if game is a tie
            if(game.getWinnerPlayerId()==0){
                vm.put(GetResultRoute.GAME_DECISION_ATTR, GetResultRoute.NO_WINNERS);
                vm.put(GetResultRoute.GAME_TIE_COUNT_ATT, Game.NON_CAPTURE_MOVES_TO_TIE_GAME);
            }
            else{
                if(game.isWinner(session.attribute(GameRoute.PLAYER_ID_ATT))){
                    //player won
                    vm.put(GetResultRoute.GAME_DECISION_ATTR, GetResultRoute.PLAYER_WON);
                    //if game won because of a blockage
                    if(game.getBlockageFlag()){
                        vm.put(GetResultRoute.GAME_MSG_ATT, GetResultRoute.PLAYER_BLOCKAGE_WON_MSG);
                    }else if(game.getResignFlag()){
                        vm.put(GetResultRoute.GAME_MSG_ATT, GetResultRoute.PLAYER_RESIGN_WON_MSG);
                    }else{
                        vm.put(GetResultRoute.GAME_MSG_ATT, GetResultRoute.PLAYER_CAPTURE_WON_MSG);
                    }
                }else{
                    //player lost
                    vm.put(GetResultRoute.GAME_DECISION_ATTR, GetResultRoute.PLAYER_LOST);
                    //if game lost because of a blockage
                    if(game.getBlockageFlag()){
                        vm.put(GetResultRoute.GAME_MSG_ATT, GetResultRoute.PLAYER_BLOCKAGE_LOST_MSG);
                    }else if(game.getResignFlag()){
                        vm.put(GetResultRoute.GAME_MSG_ATT, GetResultRoute.PLAYER_RESIGN_LOST_MSG);
                    }else{
                        vm.put(GetResultRoute.GAME_MSG_ATT, GetResultRoute.PLAYER_CAPTURE_LOST_MSG);
                    }
                }

            }//end of game is a tie if statement
        }//end of game.getStatus if statement

        return new ModelAndView(vm, VIEW_NAME);
    }

}
