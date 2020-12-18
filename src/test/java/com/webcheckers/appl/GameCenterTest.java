package com.webcheckers.appl;

import com.webcheckers.model.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Session;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameCenterTest {

    //mock objects
    private Request request;
    private Session session;

    // static variables
    private static final String USERNAME = "TestUser1";
    private static final String USERNAME_CASEINSENSITIVE = "TeSTUSEr1";
    private static final String USERNAME_Alternate = "UserTest2";
    private static final String SESSION_USER = "username";

    //friendly dependencies
    private Player testPlayer;
    private Player testPlayer2;

    /**
     * Setup new mock objects for each test.
     */
    @Before
    public void setup() {
        testPlayer = new Player(USERNAME);
        testPlayer2 = new Player(USERNAME_Alternate);

        request = mock(Request.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);
        when(session.attribute(SESSION_USER)).thenReturn(testPlayer);
    }

    /**
     * CheckUsername Tests
     */
    @Test
    public void test_CheckUsername_exists() {
        GameCenter CuT = new GameCenter();
        CuT.addPlayer(testPlayer, session);
        Assert.assertFalse(CuT.isPlayerNameAvailable(USERNAME));
    }

    @Test
    public void test_CheckUsername_exists_caseInsensitive() {
        GameCenter CuT = new GameCenter();
        CuT.addPlayer(testPlayer, session);
        Assert.assertFalse(CuT.isPlayerNameAvailable(USERNAME_CASEINSENSITIVE));
    }

    @Test
    public void test_CheckUsername_notExists() {
        GameCenter CuT = new GameCenter();
        CuT.addPlayer(testPlayer, session);
        Assert.assertTrue(CuT.isPlayerNameAvailable(USERNAME_Alternate));
    }

    @Test
    public void test_CheckUsername_Empty_notExists() {
        GameCenter CuT = new GameCenter();
        Assert.assertTrue(CuT.isPlayerNameAvailable(USERNAME));
    }

    /**
     * RemoveUser Tests
     */
    @Test
    public void test_removeUser_userExists() {
        GameCenter CuT = new GameCenter();
        CuT.addPlayer(testPlayer, session);
        CuT.removePlayer(session);
        Assert.assertTrue(CuT.isPlayerNameAvailable(USERNAME));
    }

    @Test
    public void test_removeUser_userNotExists() {
        GameCenter CuT = new GameCenter();
        CuT.removePlayer(session);
        Assert.assertTrue(CuT.isPlayerNameAvailable(USERNAME));
    }

    @Test
    public void test_removeUser_otherUserRemoved() {
        GameCenter CuT = new GameCenter();
        CuT.addPlayer(testPlayer, session);
        CuT.addPlayer(testPlayer2, session);
        CuT.removePlayer(session);
        Assert.assertTrue(CuT.isPlayerNameAvailable(USERNAME));
        Assert.assertFalse(CuT.isPlayerNameAvailable(USERNAME_Alternate));
    }
}
