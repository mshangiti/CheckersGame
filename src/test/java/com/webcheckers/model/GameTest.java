package com.webcheckers.model;

import com.webcheckers.appl.GameCenter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Session;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GameTest {


    /*
    class attributes initialized
    */
    private String player1_Name ="MOE";
    private String player2_Name = "HARSHA";


    private Player player1;
    private Player player2;

    private String player1_color ="WHITE";
    private String player2_color = "RED";


    private PieceColor player1color;
    private PieceColor player2color;

    private Game CuT;

    /**
     * Setup new mock objects for each test.
     */
    @Before
    public void setup() {

        player1 = new Player(player1_Name);
        player2 = new Player(player2_Name);

        CuT = new Game(player1,player2);
        CuT.InitializeGame();


    }

    //Test for players
    @Test
    public void test_players() {

        assertEquals(player1, CuT.getPlayer(1));
        assertEquals(player2, CuT.getPlayer(2));

    }

    //Test for player Names
    @Test
    public void test_playerNames() {

        assertEquals(player1_Name,CuT.getPlayerName(1));
        assertEquals(player2_Name,CuT.getPlayerName(2));

    }


    //Test if player available in game
    @Test
    public void test_playerAvailableInGame() {

        assertTrue(CuT.isPlayerInGame(player1_Name));
        assertTrue(CuT.isPlayerInGame(player2_Name));
    }

    //Test player color
    @Test
    public void test_player_Color() {
        //this is a random choice, so as long as we know one, we should be able to know the other
        if(CuT.getPlayerColor(1) == PieceColor.WHITE) {
            assertEquals(PieceColor.RED, CuT.getPlayerColor(2));
        }else{
            assertEquals(PieceColor.WHITE, CuT.getPlayerColor(2));
        }

    }

    //Test player turn
    @Test
    public void test_player_turn() {
        //this is a random choice, so as long as we know one, we should be able to know the other
        if(CuT.isMyTurn(1)) {
            assertEquals(false, CuT.isMyTurn(2));
        }else{
            assertEquals(true, CuT.isMyTurn(2));
        }
    }


}
