package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Player;
import org.junit.After;
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


public class HomeControllerTest {
    HomeController CuT;

    // mock objects
    private Request request;
    private Session session;
    private Response response;

    private static final String TITLE = "Welcome";
    private static final String NAME = "Moe";
    private static final String NAME2 = "Moe2";
    private static final String NAME3 = "Moe3";

    private static final String VIEW_NAME = "home.ftl";

    private static final String TITLE_ATTR = "title";
    private static final String PLAYER_LIST_ATTR = "playersList";
    private static final String PLAYER_COUNT_ATTR = "playersCount";

    //friendly dependencies
    Player testPlayer;
    GameCenter HomeGameCenter;

    @Before
    public void setup() {
        request = mock(Request.class);
        session = mock(Session.class);
        response = mock(Response.class);
    }

    @After
    public void reset()
    {
        testPlayer = null;
        HomeGameCenter = null;
    }

    @Test
    public void test_LoggedIn() {
        //Arrange Scenario
        testPlayer = new Player(NAME);
        HomeGameCenter = new GameCenter();

        Player testPlayer2 = new Player(NAME2);
        Player testPlayer3 = new Player(NAME3);

        ArrayList<Player> expected_list = new ArrayList<>();
        expected_list.add(testPlayer);
        expected_list.add(testPlayer2);
        expected_list.add(testPlayer3);

        HomeGameCenter.addPlayer(testPlayer, session);
        HomeGameCenter.addPlayer(testPlayer2, session);
        HomeGameCenter.addPlayer(testPlayer3, session);

        when(request.session()).thenReturn(session);
        when(session.attribute(HomeGameCenter.SESSION_USER)).thenReturn(testPlayer);

        CuT = new HomeController(HomeGameCenter);

        //Invoke Test
        final ModelAndView result = CuT.handle(request, response);

        //Verify result is not null
        assertNotNull(result);

        // Verify model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);

        // View-Model Information is correct
        final Map<String, Object> vm = (Map<String, Object>) model;
        assertEquals(TITLE, vm.get(TITLE_ATTR));
        assertEquals(expected_list, vm.get(PLAYER_LIST_ATTR));
        assertEquals(2, vm.get(PLAYER_COUNT_ATTR));
        assertEquals(NAME, vm.get(HomeGameCenter.SESSION_USER));

        assertEquals(VIEW_NAME, result.getViewName());
    }

    @Test(expected = spark.HaltException.class)
    public void test_NotLoggedIn(){
        //Arrange Scenario
        HomeGameCenter = new GameCenter();

        when(request.session()).thenReturn(session);
        when(session.attribute(HomeGameCenter.SESSION_USER)).thenReturn(null);

        CuT = new HomeController(HomeGameCenter);

        //Invoke Test
        final ModelAndView result = CuT.handle(request, response);

        //Verify result is null (since user is not logged in
        assertNull(result);
    }

}