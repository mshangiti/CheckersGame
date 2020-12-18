package com.webcheckers.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import spark.*;
import static spark.Spark.halt;
/**
 * The Web Controller for the Home page.
 *
 *
 */
public class HomeController implements TemplateViewRoute {

  //
  // Constants
  //
  static final String VIEW_NAME = "home.ftl";
  static final String TITLE = "Welcome";
  static final String PLAYER_LIST_ATTR = "playersList";
  static final String PLAYER_COUNT_ATTR = "playersCount";
  static final String PLAYER_CHALLENGE_STATUS = "playerChallengeStatus";
  static final String IS_CHALLENGED_ATTR = "isPlayerChallenged";
  static final String OPPONENT_NAME_ATTR = "opponentName";

  //
  // Attributes
  //
  private final GameCenter gameCenter;

  //
  // Constructor
  //

    HomeController (final GameCenter gameCenter)
    {
        Objects.requireNonNull(gameCenter, "gameCenter must not be null");
        this.gameCenter = gameCenter;
    }

  @Override
  public ModelAndView handle(Request request, Response response) {
    Map<String, Object> vm = new HashMap<>();
    vm.put("title", HomeController.TITLE);


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
    if(currentPlayer.getStatus() != Player.State.IN_HOME){
      gameCenter.redirectPlayer(request.session(),response);
    }

    //at this point, player is both loggedin and has no active game
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

    switch (gameCenter.getChallengeStatus(currentPlayer)) {
      case NO_CHALLENGES:
        break;
      case CHALLENGE_PENDING:
        break;
      case CHALLENGE_ACCEPTED:
        return loadGame(request.session(),response);
        //break;
      case CHALLENGE_DECLINED:
        gameCenter.removeUserChallenge(request.session());
        break;
      case CHALLENGE_CANCELLED:
        gameCenter.removeUserChallenge(request.session());
        break;
    }//end of switch statement

    return new ModelAndView(vm, VIEW_NAME);
  }

  public synchronized ModelAndView loadGame(final Session session, Response response) {

    //Create game if not created
    gameCenter.createGame(session);

    //redirect to game
    response.redirect(WebServer.GAME_URL);
    halt();
    return null;
  }

}