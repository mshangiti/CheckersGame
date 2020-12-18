package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Board;
import com.webcheckers.model.Challenge;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import org.junit.Before;
import org.junit.Test;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameRouteTest {

    /**
     * Test the the game renders correctly when game is started
     */
    private final GameCenter gameCenter = new GameCenter();
    private final GameRoute CuT = new GameRoute(gameCenter);
    private final String challengerName = "Moe";
    private final String opponentName = "Harsha";
    private Player challenger;
    private Player opponent;
    private Request request_ch;
    private Session session_ch;
    private Request request_op;
    private Session session_op;

    /**
     * Setup new mock objects for each test.
     */
    @Before
    public void setup() {
        challenger = new Player(challengerName);
        request_ch = mock(Request.class);
        session_ch = mock(Session.class);
        when(request_ch.session()).thenReturn(session_ch);
        when(session_ch.attribute(gameCenter.SESSION_USER)).thenReturn(challenger);

        opponent = new Player(opponentName);
        request_op = mock(Request.class);
        session_op = mock(Session.class);
        when(request_op.session()).thenReturn(session_op);
        when(session_op.attribute(gameCenter.SESSION_USER)).thenReturn(opponent);
    }

    @Test
    public void test_game_page_renders_correctly_with_active_game(){

        //create a challenge
        gameCenter.addPlayer(challenger,session_ch);
        gameCenter.addPlayer(opponent,session_op);
        gameCenter.challengePlayer(session_ch,opponentName);
        //create a game
        gameCenter.createGame(session_op);
        gameCenter.createGame(session_ch);
        final Game game = gameCenter.getActiveGame(session_ch);

        //Get Results
        when(session_ch.attribute(GameRoute.PLAYER_ID_ATT)).thenReturn(1);
        final Response response = mock(Response.class);
        final ModelAndView result = CuT.handle(request_ch,response);

        //Verify result is not null
        assertNotNull(result);

        // Verify model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);

        // View-Model Information is correct
        final Map<String, Object> vm = (Map<String, Object>) model;
        assertEquals(GameRoute.VIEW_NAME, result.getViewName());
        assertEquals(opponent, vm.get(GameRoute.PLAYER_ATTR));
        assertEquals(opponent.getName(), vm.get(GameRoute.PLAYER_NAME_ATTR));
        assertEquals(challenger.getName(), vm.get(GameRoute.OPPONENT_NAME_ATTR));
        assertEquals(game.getPlayerColor(1), vm.get(GameRoute.PLAYER_COLOR_ATTR));
        assertEquals(game.getPlayerColor(2), vm.get(GameRoute.OPPONENT_COLOR_ATTR));
        assertEquals(game.isMyTurn(1), vm.get(GameRoute.TURN_ATTR));
        assertTrue(vm.get(GameRoute.BOARD_ATTR) instanceof Board);

    }

    @Test(expected = spark.HaltException.class)
    public void test_game_page_redirects_correctly_loggedoff_players() {

        //players are not added to list of online players
        when(session_ch.attribute(gameCenter.SESSION_USER)).thenReturn(null);
        final Response response = mock(Response.class);
        final ModelAndView result = CuT.handle(request_ch,response);

        //Verify result is not null
        assertNull(result);
    }

    @Test(expected = spark.HaltException.class)
    public void test_game_page_redirects_correctly_players_with_incorrect_state() {
        //setting incorrect state
        challenger.setStatus(Player.State.IN_HOME);
        gameCenter.addPlayer(challenger,session_ch);
        final Response response = mock(Response.class);
        final ModelAndView result = CuT.handle(request_ch,response);

        //Verify result is not null
        assertNotNull(result);
    }

    @Test(expected = spark.HaltException.class)
    public void test_game_page_redirects_correctly_players_with_no_active_game() {

        //setting correct state but no active game
        challenger.setStatus(Player.State.IN_GAME);
        gameCenter.addPlayer(challenger,session_ch);
        final Response response = mock(Response.class);
        final ModelAndView result = CuT.handle(request_ch,response);

        //Verify result is not null
        assertNotNull(result);


    }

    @Test(expected = spark.HaltException.class)
    public void test_game_page_redirects_correctly_players_with_completed_game() {
        //setting correct state
        challenger.setStatus(Player.State.IN_GAME);
        opponent.setStatus(Player.State.IN_GAME);
        //create a challenge
        gameCenter.addPlayer(challenger,session_ch);
        gameCenter.addPlayer(opponent,session_op);
        gameCenter.challengePlayer(session_ch,opponentName);
        //create a game
        gameCenter.createGame(session_op);
        gameCenter.createGame(session_ch);
        final Game game = gameCenter.getActiveGame(session_ch);
        when(session_ch.attribute(GameRoute.PLAYER_ID_ATT)).thenReturn(1);
        //complete game
        gameCenter.endGame(session_ch,1);
        //completed game
        final Response response = mock(Response.class);
        final ModelAndView result = CuT.handle(request_ch,response);

        //Verify result is not null
        assertNotNull(result);
    }
}
