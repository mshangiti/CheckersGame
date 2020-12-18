package com.webcheckers.appl;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;
import com.webcheckers.model.Challenge;
import com.webcheckers.model.Game;
import com.webcheckers.model.GameState;
import com.webcheckers.ui.GameRoute;
import com.webcheckers.ui.WebServer;
import spark.Response;
import spark.Session;
import com.webcheckers.model.Player;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static spark.Spark.halt;

/**
 * The object to coordinate the state of the Web Application.
 */

public class GameCenter {

    //globals
    public static final String SESSION_USER = "username";


    //list of online/active users
    private ArrayList<Player> onlinePlayers = new ArrayList<>();
    private ArrayList<Challenge> challenges = new ArrayList<>();
    private ArrayList<Game> games = new ArrayList<>();


    /**
     * Adding a new player (at sign-in)
     *
     * @param player, Session
     *    The player to add
     *    The current session
     */

    public synchronized void addPlayer(Player player, final Session session){
        //add user to activeUsers
        onlinePlayers.add(player);

        //add to session
        session.attribute(SESSION_USER, player);
    }

    /**
     * Removing a player (at sign-out)
     *
     * @param session
     *    The current session
     */
    public synchronized void removePlayer(final Session session){

        //get user
        Player player = session.attribute(SESSION_USER);

        //check if player in game
        Game game = getActiveGame(session);
        if(game != null){
            //if player in game
            endGame(session,game.getOpponentId(session.attribute(GameRoute.PLAYER_ID_ATT)));
        }

        //remove player from any active challenges
        removeUserChallenge(session);

        //remove user from activeUsers
        onlinePlayers.remove(player);


        //remove from session
        session.removeAttribute(SESSION_USER);
        if(session.attribute(GameRoute.PLAYER_ID_ATT) != null){
            session.removeAttribute(GameRoute.PLAYER_ID_ATT);
        }

    }

    /**
     * Check if username is in use
     *
     * @param playername
     *    The username to check
     */
    public synchronized boolean isPlayerNameAvailable(String playername){
        for (Player temp:onlinePlayers){
            if(temp.getName().equalsIgnoreCase(playername)){
                return false;
            }
        }
        return true;
    }


    /**
     * Get Player object for current session
     * @param session
     * @return
     */
    public Player getPlayer(Session session){
        Player player = session.attribute(SESSION_USER);
        return player;
    }

    public synchronized Player getPlayerByName(String name){
        for(Player player:onlinePlayers) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }

        return null;
    }

    /**
     * Check if player is loggedin
     *
     * @param session
     *    The user's session
     */
    public boolean isPlayerLoggedIn(Session session){
        Player player = session.attribute(SESSION_USER);
        if(player != null){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * get the list of online players
     */

    public synchronized ArrayList<Player> getOnlinePlayers(){
        ArrayList<Player> availablePlayers = new ArrayList<>();
        for(Player player:onlinePlayers){
            if((getChallengeStatus(player) == Challenge.State.NO_CHALLENGES) && (player.getStatus()==Player.State.IN_HOME)){
                //if player is not part of any active challenge, then show him as available player
                availablePlayers.add(player);
            }
        }
        return availablePlayers;
    }

    /**
     * redirect the player when state does not match accessed page
     */

    public Object redirectPlayer(Session session, Response response){

        //validate user state
        Player currentPlayer = getPlayer(session);
        switch (currentPlayer.getStatus()) {
            case IN_HOME:
                response.redirect(WebServer.HOME_URL);
                halt();
                return null;
            case IN_GAME:
                response.redirect(WebServer.GAME_URL);
                halt();
                return null;
            case IN_RESULT:
                response.redirect(WebServer.RESULT_URL);
                halt();
                return null;
            default:
                response.redirect(WebServer.SIGNIN_URL);
                halt();
                return null;
        }//end of switch statement
    }


    /**
     * returns player challenge status
     *
     * @param player
     *    The player's to check
     */
    public synchronized  Challenge.State getChallengeStatus(Player player){
        for (Challenge ch:challenges){
            if(ch.getChallengerName().equalsIgnoreCase(player.getName()) || ch.getOpponentName().equalsIgnoreCase(player.getName())){
                return ch.getStatus();
            }
        }
        //no match
        return Challenge.State.NO_CHALLENGES;
    }

    /**
     * returns player challenge status
     *
     * @param session
     *    The player's session to check
     */
//    public synchronized  Challenge.State getChallengeStatus(Session session){
//        Player player = session.attribute(SESSION_USER);
//        for (Challenge ch:challenges){
//            if(ch.getChallengerName().equalsIgnoreCase(player.getName()) || ch.getOpponentName().equalsIgnoreCase(player.getName())){
//                return ch.getStatus();
//            }
//        }
//        //no match
//        return Challenge.State.NO_CHALLENGES;
//    }

    /**
     * Get an active challenge where the current user is a participant
     *
     * @param name
     *    The player name
     */
    public synchronized Challenge getActiveChallenge(String name){
        for (Challenge ch:challenges) {
            if (ch.getChallengerName().equalsIgnoreCase(name) || ch.getOpponentName().equalsIgnoreCase(name))
                return ch;
        }

        return null;
    }

    /**
     * Check if a player is being challenged
     *
     * @param session
     *    The user's session
     */
    public synchronized  boolean isPlayerBeingChallenged(Session session){
        Player player = session.attribute(SESSION_USER);
        for (Challenge ch:challenges){
            if(ch.getOpponentName().equalsIgnoreCase(player.getName())){
                return true;
            }
        }
        //if no match found
        return false;
    }

    /**
     * Creates a new challenge
     *
     * @param session, opponent
     *    The user's session
     *    The opponent's name
     */
    public synchronized  boolean challengePlayer(Session session, String opponent){
        Player player = session.attribute(SESSION_USER);
        //make sure the player and the opponent do not have an active challenge
        if((getChallengeStatus(player)==Challenge.State.NO_CHALLENGES) && getChallengeStatus(getPlayerByName(opponent))==Challenge.State.NO_CHALLENGES){
            Challenge ch = new Challenge(player.getName(),opponent);
            challenges.add(ch);
            return true;
        }
       return false;
    }

    /**
     * Removing a challenge
     *
     * @param session
     *    The current session
     */
    public synchronized void removeUserChallenge(final Session session){
        //get user
        Player player = session.attribute(SESSION_USER);

        //remove player from any active challenges
        List<Challenge> toRemove = new ArrayList<>();
        for (Challenge ch : challenges) {
            if(ch.getOpponentName().equalsIgnoreCase(player.getName()) || ch.getChallengerName().equalsIgnoreCase(player.getName())) {
                toRemove.add(ch);
            }
        }
        challenges.removeAll(toRemove);
    }


    /**
     * updates the challenge status
     *
     * @param session, newstatus
     *    The user's session
     *    The challenge status
     */
    public synchronized  void updateChallengeStatus(Session session, Challenge.State newstatus){
        Player player = session.attribute(SESSION_USER);
        for (Challenge ch:challenges){
            if(ch.getOpponentName().equalsIgnoreCase(player.getName()) || ch.getChallengerName().equalsIgnoreCase(player.getName())){
                ch.setStatus(newstatus);
            }
        }
    }

    /**
     * get opponent name (matchmaking)
     *
     * @param session
     *    The user's session
     */
    public synchronized String getOpponentName(Session session) {
        Player player = session.attribute(SESSION_USER);
        for (Challenge ch : challenges) {
            if (ch.getOpponentName().equalsIgnoreCase(player.getName())) {
                return ch.getChallengerName();
            }

            if (ch.getChallengerName().equalsIgnoreCase(player.getName())) {
                return ch.getOpponentName();
            }
        }
        return "";
    }



    /**
     * Create Game
     */
    public synchronized void createGame(Session session)
    {
        //check if game is already created
        Game game = getActiveGame(session);

        //note regarding PLAYER_ID_ATT value:
        //PLAYER_ID_ATT=1 will be given to game creator (player accepting challenge)
        //PLAYER_ID_ATT=2 will be given to the other player

        if(game == null)
        {//new game
            Player currentPlayer = getPlayer(session);
            Challenge challenge = getActiveChallenge(currentPlayer.getName());
            Player opponentPlayer;
            if (challenge.getChallengerName().equalsIgnoreCase(currentPlayer.getName())) {
                opponentPlayer = getPlayerByName(challenge.getOpponentName());
            } else {
                opponentPlayer = getPlayerByName(challenge.getChallengerName());
            }

            //create game
            Game myGame = new Game(currentPlayer, opponentPlayer);
            myGame.InitializeGame();

            //set player as in game
            currentPlayer.setStatus(Player.State.IN_GAME);

            //set as main player
            session.attribute(GameRoute.PLAYER_ID_ATT, 1);

            //add game to list
            games.add(myGame);
        }
        else
        {
            //game was already created

            //set as opponent player
            session.attribute(GameRoute.PLAYER_ID_ATT, 2);

            //remove challenge
            removeUserChallenge(session);

            //set as in game
            Player currentPlayer = getPlayer(session);
            currentPlayer.setStatus(Player.State.IN_GAME);

        }
    }


    public synchronized Game getGameByPlayer(String playerName)
    {
        //return latest game
        Game mygame = null;
        for (Game game:games) {
            if (game.isPlayerInGame(playerName))
            {
                mygame = game;
            }
        }

        return mygame;
    }

    /**
     * Return active game for current session
     * @param session
     * @return
     */
    public synchronized Game getActiveGame(Session session)
    {
        String name = getPlayer(session).getName();
        for (Game game:games) {
            if (game.isPlayerInGame(name) && game.getState() != GameState.OVER)
            {
                return game;
            }
        }
        return null;
    }


    /**
     * method will end a game (user will be redirected to result game)
     * @param session
     * @return
     */
    public synchronized void endGame(Session session, int winnerPlayerId)
    {
        //get Game
        Game game = getActiveGame(session);
        //get player1
        Player player1 = game.getPlayer(session.attribute(GameRoute.PLAYER_ID_ATT));
        //get player2
        Player player2 = game.getPlayer(game.getOpponentId(session.attribute(GameRoute.PLAYER_ID_ATT)));

        //set a winner
        game.setWinnerPlayerId(winnerPlayerId);

        //putting game and user in the right state
        // change game status
        game.setState(GameState.OVER);
        //set user state (player ended game, state used to redirect to result page)
        player1.setStatus(Player.State.IN_RESULT);
        player2.setStatus(Player.State.IN_RESULT);

        //remove game object after 5 minutes if it wasn't already removed
        scheduleGameRemoval(game, 5,TimeUnit.MINUTES);
    }

    /**
     * method will removed completed games after a specificed time period
     * @param game, timeDelay, waitTimeUnit
     * @return
     */
    public synchronized void scheduleGameRemoval(Game game, long timeDelay,TimeUnit waitTimeUnit){

        //make sure game is still in memory
        if(games.contains(game) && game.getState() == GameState.OVER){
            //start scheduler
            int threadCount = 1;
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(threadCount);

            //start cleaning job
            Runnable cleaner = new GameCleaner(this,game);

            //add job to scheduler
            scheduler.schedule(cleaner,timeDelay,waitTimeUnit);

        }//end of if-statement

    }//end of removeGame

    /**
     * method will removed given game
     * @param game
     * @return
     */
    public synchronized void removeGame(Game game){
        games.remove(game);
    }//end of removeGame


    /**
     * getters/setters used for testing purposes
     * @return
     */
    public synchronized ArrayList getGames(){return this.games;}
    public synchronized void setGames(ArrayList games){ this.games = games;}
    public synchronized ArrayList getChallenges(){return this.challenges;}
    public synchronized void setChallenges(ArrayList challenges){this.challenges=challenges;}

}
