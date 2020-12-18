package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.*;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static spark.Spark.halt;

public class SubmitRoute implements TemplateViewRoute {

    static final String VIEW_NAME = "game.ftl";
    static final String SKIPAVAILABBLE = "You still have some captures available.";

    static final String TITLE = "Checker Game";

    //
    // Attributes
    //
    private final GameCenter gameCenter;

    //
    // Constructor
    //

    SubmitRoute (final GameCenter gameCenter)
    {
        Objects.requireNonNull(gameCenter, "gameCenter must not be null");
        this.gameCenter = gameCenter;
    }

    @Override
    public synchronized ModelAndView handle(Request request, Response response) {
        Map<String, Object> vm = new HashMap<>();
        vm.put("title", this.TITLE);
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

        //validate player status
        if(currentPlayer.getStatus() != Player.State.IN_GAME){
            gameCenter.redirectPlayer(request.session(),response);
        }

        // Check if player has an active game
        Game game = gameCenter.getActiveGame(session);
        if(game == null){
            response.redirect(WebServer.HOME_URL);
            halt();
            return null;
        }

        //register that user has decided on a move
        game.setIsPlayerSkippingFlag(false);

        // Check if a game board is in skipping state
        if (game.getState() == GameState.SKIP)
        {
            Message msg = new Message(SKIPAVAILABBLE, MessageType.info);
            game.setMessage(msg);

            // Send game attributes to VIEW
            int playerId = session.attribute(GameRoute.PLAYER_ID_ATT);
            int opponentId = 2;
            if( playerId == 2 ){ opponentId = 1; }

            vm.put(WebServer.TITLE_ATTR, GameRoute.TITLE);
            vm.put(GameRoute.PLAYER_ATTR, game.getPlayer(playerId));
            vm.put(GameRoute.PLAYER_NAME_ATTR, game.getPlayerName(playerId));
            vm.put(GameRoute.OPPONENT_NAME_ATTR, game.getPlayerName(opponentId));
            vm.put(GameRoute.PLAYER_COLOR_ATTR, game.getPlayerColor(playerId));
            vm.put(GameRoute.OPPONENT_COLOR_ATTR, game.getPlayerColor(opponentId));
            vm.put(GameRoute.TURN_ATTR, game.isMyTurn(playerId));
            vm.put(GameRoute.BOARD_ATTR, game.getPendingBoard());
            vm.put(GameRoute.MSG_ATTR, game.getMessage());

            return new ModelAndView(vm, this.VIEW_NAME);
        }

        //check move type
        GameRules rules = new GameRules();
        if(rules.isCaptureAvailable(game.getBoard(),game.getPlayerColor(session.attribute(GameRoute.PLAYER_ID_ATT))) == false){
            //no capture is available, incerement count
            game.incrementNoCaptureMovesCounter();
        }else{
            //if capture is available = user will be forced to make capture = reset counter
            game.resetNoCaptureMovesCounter();
        }

        //Update Board
        game.commitPendingBoard();
        game.switchTurn();
        game.setState(GameState.IDLE);

        //check if game is complete
        //if opponent has no more pieces on the board, then declare current player as winner
        PieceColor opponentColor = game.getPlayerColor(game.getOpponentId(session.attribute(GameRoute.PLAYER_ID_ATT)));
        if(game.getBoard().checkBoardStillHasPiecesWithColor(opponentColor) == false && game.getState() != GameState.OVER){
            gameCenter.endGame(session,session.attribute(GameRoute.PLAYER_ID_ATT));
        }
        //if X no capture moves were done, then game is considered a tie
        if(game.getNoCaptureMovesCounter()>=Game.NON_CAPTURE_MOVES_TO_TIE_GAME && game.getState() != GameState.OVER){
            gameCenter.endGame(session,0);
        }
        //if opponent has no possible moves (blocked), then current player wins the game
        if(game.getBoard().checkMoveAvailable(opponentColor) == false && game.getState() != GameState.OVER){
            game.setBlockageFlag(true);
            gameCenter.endGame(session,session.attribute(GameRoute.PLAYER_ID_ATT));
        }


       //redirect user
        response.redirect(WebServer.GAME_URL);
        halt();
        return null;
    }
}
