package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Challenge;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import spark.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static spark.Spark.halt;

public class PostHomeRoute implements TemplateViewRoute {

    //
    // Attributes
    //
    private final GameCenter gameCenter;
    static final String OPERATION_ATT = "requestedOperation";
    static final String OPPONENT_NAME_ATTR ="opponentName";
    static final String MESSAGE_ATTR = "errorMessage";
    static final String INVALID_CHOICE_ERR_MSG = "invalid choice.";

    //
    // Constructor
    //

    PostHomeRoute (final GameCenter gameCenter)
    {
        Objects.requireNonNull(gameCenter, "gameCenter must not be null");
        this.gameCenter = gameCenter;
    }

    @Override
    public ModelAndView handle(Request request, Response response) {
        Map<String, Object> vm = new HashMap<>();
        vm.put("title", HomeController.TITLE);
        Player currentPlayer = gameCenter.getPlayer(request.session());

        if (gameCenter.isPlayerLoggedIn(request.session())) {
            //user loggedin

            //reading user parameters
            // convert the input
            int operation = -1;
            try {
                operation = Integer.parseInt(request.queryParams(OPERATION_ATT));

                switch (operation){
                    case 1:
                        //challenge request
                        String opponentname = request.queryParams(OPPONENT_NAME_ATTR);
                        if(opponentname==null || opponentname.length() == 0){
                            throw new NullPointerException("invalid choice.");
                        }
                        if(gameCenter.challengePlayer(request.session(),opponentname) == false){
                            throw new NullPointerException("Sorry, player has already been challenged by another player.");
                        }
                        break;
                    case 2:
                        //cancel challenge request
                        if(gameCenter.getChallengeStatus(currentPlayer) == Challenge.State.CHALLENGE_PENDING){
                            gameCenter.updateChallengeStatus(request.session(), Challenge.State.CHALLENGE_CANCELLED);
                        }
                        break;
                    case 3:
                        //accept challenge request
                        if(gameCenter.getChallengeStatus(currentPlayer) == Challenge.State.CHALLENGE_PENDING){
                            return loadGame(request.session(), response);
                        }
                        break;
                    case 4:
                        //decline challenge request
                        if(gameCenter.getChallengeStatus(currentPlayer) == Challenge.State.CHALLENGE_PENDING){
                            gameCenter.updateChallengeStatus(request.session(), Challenge.State.CHALLENGE_DECLINED);
                        }
                        break;
                    default:
                        //shouldn't be possible
                        throw new NullPointerException(INVALID_CHOICE_ERR_MSG);
                }
            }
            catch (NumberFormatException e) {
                vm.put(MESSAGE_ATTR,INVALID_CHOICE_ERR_MSG);
            }catch (Exception e) {
                vm.put(MESSAGE_ATTR, INVALID_CHOICE_ERR_MSG);
            }

            //parameters
            //list of online players
            ArrayList<Player> onlinePlayers = gameCenter.getOnlinePlayers();
            vm.put(HomeController.PLAYER_LIST_ATTR, onlinePlayers);
            //number of online players (used for the loop)
            vm.put(HomeController.PLAYER_COUNT_ATTR, onlinePlayers.size() - 1);
            //sending the player name to be displayed
            vm.put(gameCenter.SESSION_USER, currentPlayer.getName());
            //sending the challenge status: no_challenge, challenge_pending,...etc
            vm.put(HomeController.PLAYER_CHALLENGE_STATUS, gameCenter.getChallengeStatus(currentPlayer));
            //a true/false flag to determine if current player is the 'challenger' or the 'opponent'.
            vm.put(HomeController.IS_CHALLENGED_ATTR, gameCenter.isPlayerBeingChallenged(request.session()));
            //the opponent name
            vm.put(HomeController.OPPONENT_NAME_ATTR, gameCenter.getOpponentName(request.session()));


            return new ModelAndView(vm, HomeController.VIEW_NAME);
        } else {
            //not logged in
            return new ModelAndView(vm, GetSignInRoute.VIEW_NAME);
        }
    }


    public synchronized ModelAndView loadGame(final Session session, Response response) {
        //Create game if not created
        gameCenter.createGame(session);
        gameCenter.updateChallengeStatus(session, Challenge.State.CHALLENGE_ACCEPTED);
        //redirect to game
        response.redirect(WebServer.GAME_URL);
        halt();
        return null;
    }
}
