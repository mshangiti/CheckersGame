package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Challenge;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.ArrayList;
import java.util.Map;

public class GetSignOutRouteTest {

    private GetSignOutRoute CuT;

    // mock objects
    private Request request;
    private Session session;
    private Response response;

    //static strings
    private static final String TITLE_KEY = "title";
    private static final String TITLE = "Welcome";
    private static final String VIEW = "signin.ftl";
    private static final String USERNAME = "TestUser1";
    private static final String SESSION_USER = "username";

    //friendly dependencies
    private GameCenter center;
    private Player testPlayer;

    @Before
    public void setup() {
        testPlayer = new Player(USERNAME);
        request = mock(Request.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);
        when(session.attribute(SESSION_USER)).thenReturn(testPlayer);

        center = new GameCenter();
        CuT = new GetSignOutRoute(center);
    }

    @Test
    public void test_handle_existingUserSigningOut() {
        //Add User
        center.addPlayer(testPlayer, session);

        //Get Results
        final ModelAndView result = CuT.handle(request,response);

        //Verify result is not null
        assertNotNull(result);

        // Verify model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);

        // View-Model Information is correct
        final Map<String, Object> vm = (Map<String, Object>) model;
        assertEquals(TITLE, vm.get(TITLE_KEY));
        assertEquals(VIEW, result.getViewName());
    }

    @Test
    public void test_handle_nonExistingUserSigningOut() {
        //Get Results
        final ModelAndView result = CuT.handle(request, response);

        //Verify result is not null
        assertNotNull(result);

        // Verify model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);

        // View-Model Information is correct
        final Map<String, Object> vm = (Map<String, Object>) model;
        assertEquals(TITLE, vm.get(TITLE_KEY));
        assertEquals(VIEW, result.getViewName());
    }

    @Test
    public void test_handle_signOutWithActiveChallenge() {

        //add players
        center.addPlayer(testPlayer, session);
        Player player2 = new Player("Moe");
        Request request2 = mock(Request.class);
        Session session2 = mock(Session.class);
        when(request2.session()).thenReturn(session2);
        when(session2.attribute(SESSION_USER)).thenReturn(player2);
        center.addPlayer(player2, session2);
        //check players exist
        assertTrue(center.getOnlinePlayers().contains(player2));
        assertTrue(center.getOnlinePlayers().contains(testPlayer));

        //create challenge
        Challenge ch = new Challenge(testPlayer.getName(),player2.getName());
        ArrayList<Challenge> challenges = new ArrayList();
        challenges.add(ch);
        center.setChallenges(challenges);

        //check challenge exists
        assertTrue(center.getChallenges().size()==1);
        assertEquals(center.getChallengeStatus(testPlayer),Challenge.State.CHALLENGE_PENDING);
        assertEquals(center.getChallengeStatus(player2),Challenge.State.CHALLENGE_PENDING);

        //signout user
        //Get Results
        final ModelAndView result = CuT.handle(request, response);

        //Verify result is not null
        assertNotNull(result);

        // Verify model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);

        // View-Model Information is correct
        final Map<String, Object> vm = (Map<String, Object>) model;
        assertEquals(TITLE, vm.get(TITLE_KEY));
        assertEquals(VIEW, result.getViewName());

        //check challenge is removed
        assertTrue(center.getChallenges().size()==0);
        assertEquals(center.getChallengeStatus(player2),Challenge.State.NO_CHALLENGES);
        //check players removed
        assertFalse(center.getOnlinePlayers().contains(testPlayer));
        assertTrue(center.getOnlinePlayers().contains(player2));

    }

    @Test
    public void test_handle_signOutWithActiveGame() {

        //add players
        center.addPlayer(testPlayer, session);
        Player player2 = new Player("Moe");
        Request request2 = mock(Request.class);
        Session session2 = mock(Session.class);
        when(request2.session()).thenReturn(session2);
        when(session2.attribute(SESSION_USER)).thenReturn(player2);
        center.addPlayer(player2, session2);
        //check players exist
        assertTrue(center.getOnlinePlayers().contains(player2));
        assertTrue(center.getOnlinePlayers().contains(testPlayer));

        //create challenge
        Challenge challenge = new Challenge(testPlayer.getName(), player2.getName());
        challenge.setStatus(Challenge.State.CHALLENGE_ACCEPTED);
        //add challenge to list of challenge
        ArrayList <Challenge> challenges = new ArrayList();
        challenges.add(challenge);
        center.setChallenges(challenges);

        //create game
        center.createGame(session);
        center.createGame(session2);
        Game game = center.getActiveGame(session);
        when(session.attribute(GameRoute.PLAYER_ID_ATT)).thenReturn(1);
        when(session2.attribute(GameRoute.PLAYER_ID_ATT)).thenReturn(2);

        //check game exists
        assertTrue(center.getGames().contains(game));
        //check user status
        assertEquals(testPlayer.getStatus(),Player.State.IN_GAME);
        assertEquals(player2.getStatus(),Player.State.IN_GAME);

        //signout user
        //Get Results
        final ModelAndView result = CuT.handle(request, response);

        //Verify result is not null
        assertNotNull(result);

        // Verify model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);

        // View-Model Information is correct
        final Map<String, Object> vm = (Map<String, Object>) model;
        assertEquals(TITLE, vm.get(TITLE_KEY));
        assertEquals(VIEW, result.getViewName());

        //check user status changed
        assertEquals(player2.getStatus(),Player.State.IN_RESULT);
        //check player removed
        assertFalse(center.getOnlinePlayers().contains(testPlayer));
    }

}