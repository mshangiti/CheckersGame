package com.webcheckers.appl;


import com.webcheckers.model.GameState;
import com.webcheckers.model.Player;
import com.webcheckers.model.Game;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GameCleanerTest {


    private Player player1;
    private Player player2;
    private GameCenter gameCenter;
    private Game game;

    /**
     * Setup new mock objects for each test.
     */
    @Before
    public void setup() {
        //players
        player1 = new Player("Moe");
        player2 = new Player("Harsha");
        //game
        game = new Game(player1,player2);
        //finish game
        game.setState(GameState.OVER);
        //add game to list of games
        gameCenter = new GameCenter();
        ArrayList <Game> games = new ArrayList();
        games.add(game);
        gameCenter.setGames(games);
    }

    @Test
    public void test_game_scheduled_to_be_removed_in_3_seconds(){

        //first check game exists
        assertTrue(gameCenter.getGames().size()==1);
        assertTrue(gameCenter.getGames().contains(game));

        //schedule to remove game in 3 seconds
        gameCenter.scheduleGameRemoval(game,3, TimeUnit.SECONDS);
        try{
            //wait 1 sec, game shoud still be available
            TimeUnit.SECONDS.sleep(1);
            assertTrue(gameCenter.getGames().size()==1);
            assertTrue(gameCenter.getGames().contains(game));
            //wait 3 seconds, game should not be around anymore
            TimeUnit.SECONDS.sleep(3);
            assertTrue(gameCenter.getGames().size()==0);
        }catch (Exception e){
            fail();
        }

    }
}
