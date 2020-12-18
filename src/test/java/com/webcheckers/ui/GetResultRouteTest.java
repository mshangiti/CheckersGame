package com.webcheckers.ui;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.ui.GetResultRoute;
import com.webcheckers.model.Challenge;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import org.junit.Before;
import org.junit.Test;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetResultRouteTest {


    /**
     * Test the the game renders correctly when game is started
     */
    private final GameCenter gameCenter = new GameCenter();
    private final GetResultRoute CuT = new GetResultRoute(gameCenter);
    private Player player1;
    private Player player2;
    private Request request_p1;
    private Session session_p1;
    private Request request_p2;
    private Session session_p2;
    private Response response;
    private Game game;
    private Challenge challenge;

    /**
     * Setup new mock objects for each test.
     */
    @Before
    public void setup() {

        //player1
        player1 = new Player("Moe");
        request_p1 = mock(Request.class);
        session_p1 = mock(Session.class);
        when(request_p1.session()).thenReturn(session_p1);
        when(session_p1.attribute(gameCenter.SESSION_USER)).thenReturn(player1);
        gameCenter.addPlayer(player1,session_p1);
        //player2
        player2 = new Player("Harsha");
        request_p2 = mock(Request.class);
        session_p2 = mock(Session.class);
        when(request_p2.session()).thenReturn(session_p2);
        when(session_p2.attribute(gameCenter.SESSION_USER)).thenReturn(player2);
        gameCenter.addPlayer(player2,session_p2);

        //create challenge
        challenge = new Challenge(player1.getName(), player2.getName());
        challenge.setStatus(Challenge.State.CHALLENGE_ACCEPTED);
        //add challenge to list of challenge
        ArrayList <Challenge> challenges = new ArrayList();
        challenges.add(challenge);
        gameCenter.setChallenges(challenges);

        //create game
        gameCenter.createGame(session_p1);
        gameCenter.createGame(session_p2);
        game = gameCenter.getActiveGame(session_p1);
    }

    /*
     * testing all three states of the result page
     */
    @Test
    public void test_rendering_you_won_page(){
        //ending game with player 1 as winner
        when(session_p1.attribute(GameRoute.PLAYER_ID_ATT)).thenReturn(1);
        when(session_p2.attribute(GameRoute.PLAYER_ID_ATT)).thenReturn(2);
        gameCenter.endGame(session_p1,1);

        //Get Results
        final ModelAndView result = CuT.handle(request_p1, response);

        //Verify result is not null
        assertNotNull(result);

        // Verify model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);

        // View-Model Information is correct
        final Map<String, Object> vm = (Map<String, Object>) model;
        assertEquals(GetResultRoute.PLAYER_WON, vm.get(GetResultRoute.GAME_DECISION_ATTR));
    }

    @Test
    public void test_rendering_you_lost_page(){
        //ending game with player 1 as winner
        when(session_p1.attribute(GameRoute.PLAYER_ID_ATT)).thenReturn(1);
        when(session_p2.attribute(GameRoute.PLAYER_ID_ATT)).thenReturn(2);
        gameCenter.endGame(session_p1,2);

        //Get Results
        final ModelAndView result = CuT.handle(request_p1, response);

        //Verify result is not null
        assertNotNull(result);

        // Verify model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);

        // View-Model Information is correct
        final Map<String, Object> vm = (Map<String, Object>) model;
        assertEquals(GetResultRoute.PLAYER_LOST, vm.get(GetResultRoute.GAME_DECISION_ATTR));
    }

    @Test
    public void test_rendering_tie_game_page(){
        //ending game with player 1 as winner
        when(session_p1.attribute(GameRoute.PLAYER_ID_ATT)).thenReturn(1);
        when(session_p2.attribute(GameRoute.PLAYER_ID_ATT)).thenReturn(2);
        gameCenter.endGame(session_p1,0);

        //Get Results
        final ModelAndView result = CuT.handle(request_p1, response);

        //Verify result is not null
        assertNotNull(result);

        // Verify model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);

        // View-Model Information is correct
        final Map<String, Object> vm = (Map<String, Object>) model;
        assertEquals(GetResultRoute.NO_WINNERS, vm.get(GetResultRoute.GAME_DECISION_ATTR));
    }

}
