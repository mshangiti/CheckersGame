package com.webcheckers.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidateRouteTest {

    private ValidateRoute CuT;

    private Player player;
    private Player opponent;

    private Game game;

    //mock objects
    private GameCenter gameCenter;
    private Request request;
    private Session session;
    private Set<String> JSONString;

    private Board baseBoard;
    private Board pendingBoard;
    private GameRules rules;

    @Before
    public void setup() {
        request = mock(Request.class);
        session = mock(Session.class);
        gameCenter = mock(GameCenter.class);
        baseBoard = mock(Board.class);
        pendingBoard = mock(Board.class);
        rules = mock(GameRules.class);


        JSONString = new HashSet<>();
        JSONString.add("{\"start\":{\"row\":\"5\",\"cell\":\"4\"},\"end\":{\"row\":\"4\",\"cell\":\"5\"}}");

        player = new Player("player");
        opponent = new Player("opponent");
        game = new Game(player, opponent);

        player.setStatus(Player.State.IN_GAME);
        game = new Game(player, opponent);
        CuT = new ValidateRoute(gameCenter);

        when(request.session()).thenReturn(session);
        when(request.queryParams()).thenReturn(JSONString);
        when(session.attribute(gameCenter.SESSION_USER)).thenReturn(player);
        when(gameCenter.getPlayer(request.session())).thenReturn(player);
        when(gameCenter.getActiveGame(session)).thenReturn(game);
        when(gameCenter.isPlayerLoggedIn(session)).thenReturn(true);
        when(session.attribute(GameRoute.PLAYER_ID_ATT)).thenReturn(1);

    }

    @Test
    public void test_GameStateIsPending() {
        game.setState(GameState.PENDING);

        //Get Results
        final Response response = mock(Response.class);
        final Message result = (Message)CuT.handle(request,response);

        assertEquals(ValidateRoute.MOVEPENDING, result.getText());
        assertEquals(MessageType.error, result.getType());
    }

    @Test
    public void test_GameStateIsSkip_NotCurrentStep() {
        game.setState(GameState.SKIP);
        game.setBoard(baseBoard);

        when(baseBoard.getCurrentStep()).thenReturn(new Position(5, 5));

        //Get Results
        final Response response = mock(Response.class);
        final Message result = (Message)CuT.handle(request,response);

        assertEquals(ValidateRoute.SKIPAVAILABLE, result.getText());
        assertEquals(MessageType.error, result.getType());
    }

    @Test
    public void test_GameStateIsSkip_CurrentStep_MessageIsInfo() {
        game.setState(GameState.SKIP);
        game.setBoard(baseBoard);

        when(baseBoard.getCurrentStep()).thenReturn(new Position(5, 4));
        when(pendingBoard.clone()).thenReturn(pendingBoard);
        when(rules.validateMove(any(Game.class), any(Move.class), any(Board.class), any(Board.class))).thenReturn(new Message("Valid move", MessageType.info));
        when(baseBoard.isMultiCap()).thenReturn(true);

        //Get Results
        final Response response = mock(Response.class);
        final Message result = (Message) CuT.handle(request, response);

        assertEquals("Valid move", result.getText());
        assertEquals(MessageType.info, result.getType());

        assertEquals(game.getMessage().getText(), result.getText());
        assertEquals(game.getMessage().getType(), result.getType());
    }
}