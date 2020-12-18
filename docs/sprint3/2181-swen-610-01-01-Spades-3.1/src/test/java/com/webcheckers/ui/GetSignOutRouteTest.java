package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
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
}