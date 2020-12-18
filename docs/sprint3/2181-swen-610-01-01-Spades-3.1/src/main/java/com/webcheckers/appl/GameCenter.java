package com.webcheckers.appl;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;
import com.webcheckers.model.Challenge;
import com.webcheckers.model.Game;
import spark.Session;
import com.webcheckers.model.Player;
import java.util.List;

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

        //remove player from any active challenges
        removeUserChallenge(session);

        //add user to activeUsers
        onlinePlayers.remove(player);

        //remove from session
        session.removeAttribute(SESSION_USER);
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
            if((getChallengeStatus(player) == Challenge.State.NO_CHALLENGES) && (player.getStatus()==Player.State.IN_LOBBY)){
                //if player is not part of any active challenge, then show him as available player
                availablePlayers.add(player);
            }
        }
        return availablePlayers;
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
            session.attribute(Game.PLAYER_ID_ATT, 1);

            //add game to list
            games.add(myGame);
        }
        else
        {
            //game was already created

            //set as opponent player
            session.attribute(Game.PLAYER_ID_ATT, 2);

            //remove challenge
            removeUserChallenge(session);

            //set as in game
            Player currentPlayer = getPlayer(session);
            currentPlayer.setStatus(Player.State.IN_GAME);

        }
    }


    public synchronized Game getGameByPlayer(String playerName)
    {
        for (Game game:games) {
            if (game.isPlayerInGame(playerName))
            {
                return game;
            }
        }

        return null;
    }

    /**
     * Return active game for current session
     * @param session
     * @return
     */
    public synchronized Game getActiveGame(Session session)
    {
        String name = getPlayer(session).getName();
        return getGameByPlayer(name);
    }


}
