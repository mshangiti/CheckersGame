package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.*;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostBackupMoveRouteTest {
    /**
     * Test the the game renders correctly when game is started
     */
    private final GameCenter gameCenter = new GameCenter();
    private Game game;
    private final PostBackupMoveRoute CuT = new PostBackupMoveRoute(gameCenter);
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
    @Before//setting up a game
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


        //create a challenge
        gameCenter.addPlayer(challenger,session_ch);
        gameCenter.addPlayer(opponent,session_op);
        gameCenter.challengePlayer(session_ch,opponentName);
        //create a game
        gameCenter.createGame(session_op);
        gameCenter.createGame(session_ch);
        this.game = gameCenter.getActiveGame(session_ch);

        //Get Results
        when(session_ch.attribute(GameRoute.PLAYER_ID_ATT)).thenReturn(1);
        when(session_op.attribute(GameRoute.PLAYER_ID_ATT)).thenReturn(2);
    }


    @Test(expected = spark.HaltException.class)
    public void test_loggedoff_users_redirection() {

        //players are not added to list of online players
        when(session_ch.attribute(gameCenter.SESSION_USER)).thenReturn(null);
        final Response response = mock(Response.class);
        final Object result = CuT.handle(request_ch,response);

        //Verify result is not null
        assertNull(result);
    }

    @Test(expected = spark.HaltException.class)
    public void test_player_has_no_active_game() {

        //players are not added to list of online players
        game.setState(GameState.OVER);
        final Response response = mock(Response.class);
        final Object resultobject = CuT.handle(request_ch,response);

        //Verify result is not null
        assertNotNull(resultobject);
        boolean  result = (Boolean) resultobject;
        assertTrue(result);
    }

    @Test(expected = spark.HaltException.class)
    public void test_player_has_active_game_but_wrong_state() {

        //players are not added to list of online players
        Player myplayer = gameCenter.getPlayerByName(challengerName);
        myplayer.setStatus(Player.State.IN_HOME);
        final Response response = mock(Response.class);
        final Object resultobject = CuT.handle(request_ch,response);

        //Verify result is not null
        assertNotNull(resultobject);
        boolean  result = (Boolean) resultobject;
        assertTrue(result);
    }

    @Test
    public void test_player_can_backup_move_when_no_skip() {

        //update skip flag
        game.setIsPlayerSkippingFlag(false);

        final Response response = mock(Response.class);
        final Object resultobject = CuT.handle(request_ch,response);
        //Verify result is not null
        assertNotNull(resultobject);
        Message  result = (Message) resultobject;
        assertSame(result.getType(), MessageType.info);
    }

    @Test
    public void test_player_cannot_backup_move_when_skip() {

        //update skip flag
        game.setIsPlayerSkippingFlag(true);

        final Response response = mock(Response.class);
        final Object resultobject = CuT.handle(request_ch,response);
        //Verify result is not null
        assertNotNull(resultobject);
        Message  result = (Message) resultobject;
        assertSame(result.getType(), MessageType.error);
    }
}
