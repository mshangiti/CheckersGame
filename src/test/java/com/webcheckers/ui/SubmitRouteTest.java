package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.*;
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

public class SubmitRouteTest {

    private SubmitRoute CuT;

    private Player player;
    private Player opponent;

    private Game game;

    //mock objects
    private GameCenter gameCenter;
    private Request request;
    private Session session;

    @Before
    public void setup() {
        request = mock(Request.class);
        session = mock(Session.class);
        gameCenter = mock(GameCenter.class);

        player = new Player("player");
        opponent = new Player("opponent");

        player.setStatus(Player.State.IN_GAME);
        game = new Game(player, opponent);
        CuT = new SubmitRoute(gameCenter);

        when(request.session()).thenReturn(session);
        when(session.attribute(gameCenter.SESSION_USER)).thenReturn(player);
        when(gameCenter.getPlayer(request.session())).thenReturn(player);
        when(gameCenter.getActiveGame(session)).thenReturn(game);
        when(gameCenter.isPlayerLoggedIn(session)).thenReturn(true);
        when(session.attribute(GameRoute.PLAYER_ID_ATT)).thenReturn(1);
    }

    @Test
    public void test_PlayerIsSkipping() {
        game.setState(GameState.SKIP);

        //Get Results
        final Response response = mock(Response.class);
        final ModelAndView result = CuT.handle(request,response);

        //Verify result is not null
        assertNotNull(result);

        // Verify model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);

        // View-Model Information is correct
        final Map<String, Object> vm = (Map<String, Object>) model;
        Message msg = (Message)vm.get(GameRoute.MSG_ATTR);


        assertEquals(SubmitRoute.VIEW_NAME, result.getViewName());
        assertEquals(SubmitRoute.TITLE, vm.get(WebServer.TITLE_ATTR));
        assertEquals(player, vm.get(GameRoute.PLAYER_ATTR));
        assertEquals(player.getName(), vm.get(GameRoute.PLAYER_NAME_ATTR));
        assertEquals(opponent.getName(), vm.get(GameRoute.OPPONENT_NAME_ATTR));
        assertEquals(game.getPlayerColor(1), vm.get(GameRoute.PLAYER_COLOR_ATTR));
        assertEquals(game.getPlayerColor(2), vm.get(GameRoute.OPPONENT_COLOR_ATTR));
        assertEquals(game.isMyTurn(1), vm.get(GameRoute.TURN_ATTR));
        assertTrue(vm.get(GameRoute.BOARD_ATTR) instanceof Board);
        assertTrue(vm.get(GameRoute.MSG_ATTR) instanceof Message);

        //Check Message attribute
        assertEquals(SubmitRoute.SKIPAVAILABBLE, msg.getText());
        assertEquals(MessageType.info, msg.getType());
    }


    @Test(expected = spark.HaltException.class)
    public void test_loggedoff_users_redirection() {

        //players are not added to list of online players
        when(session.attribute(gameCenter.SESSION_USER)).thenReturn(null);
        when(gameCenter.isPlayerLoggedIn(session)).thenReturn(false);
        final Response response = mock(Response.class);
        final Object result = CuT.handle(request,response);

        //Verify result is not null
        assertNull(result);
    }

    @Test(expected = spark.HaltException.class)
    public void test_player_has_no_active_game() {

        //players are not added to list of online players
        game.setState(GameState.OVER);
        final Response response = mock(Response.class);
        final Object resultobject = CuT.handle(request,response);

        //Verify result is not null
        assertNotNull(resultobject);
        boolean  result = (Boolean) resultobject;
        assertTrue(result);
    }

    @Test(expected = spark.HaltException.class)
    public void test_player_has_active_game_but_wrong_state() {

        //players are not added to list of online players
        player.setStatus(Player.State.IN_HOME);
        final Response response = mock(Response.class);
        final Object resultobject = CuT.handle(request,response);

        //Verify result is not null
        assertNotNull(resultobject);
        boolean  result = (Boolean) resultobject;
        assertTrue(result);
    }
}