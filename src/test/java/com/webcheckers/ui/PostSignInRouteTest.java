package com.webcheckers.ui;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostSignInRouteTest {
    PostSignInRoute CuT;

    // mock objects
    private Request request;
    private Session session;
    private Response response;

    //static strings
    private static final String MESSAGE_ATTR = "errorMessage";
    private static final String TITLE_ATTR = "title";

    private static final String USERNAME_IN_USE = "Sorry, the username you selected is already in-use, kindly try a different username.";
    private static final String USERNAME_TOO_LONG = "Sorry, the username cannot be longer than 30 chars.";
    private static final String USERNAME_EMPTY = "Sorry, the username cannot be empty.";
    private static final String USERNAME_INVALID_CHARS = "Sorry, only letters or numbers are allowed in the username, kindly try a different username.";

    private static final String NAME_EMPTY = "";
    private static final String NAME_TOO_LONG = "ABCDEFGHJKLMNOPQRTUVWXYZASASCVFE123456789";
    private static final String NAME_HAS_ILLEGAL_CHARS = "mshang#$@";
    private static final String NAME_VALID = "Moe";
    private static final String NAME_VALID2 = "Moe2";

    private static final String HOMECONTROLLER_VIEW = "home.ftl";
    private static final String SIGNIN_VIEW = "signin.ftl";

    private static final String TITLE = "Welcome";

    //friendly dependencies
    private GameCenter center;
    private Player testPlayer;

    @Before
    public void setup() {
        request = mock(Request.class);
        session = mock(Session.class);
        response = mock(Response.class);
        when(request.session()).thenReturn(session);

        center = new GameCenter();
        CuT = new PostSignInRoute(center);
    }

    @After
    public void reset()
    {
        Mockito.reset(request);
        Mockito.reset(session);
        Mockito.reset(response);

        center = null;
        CuT = null;
        testPlayer = null;
    }

    @Test
    public void test_UserNameValid() {
        when(request.queryParams(center.SESSION_USER)).thenReturn(NAME_VALID);
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
        assertEquals(TITLE, vm.get(TITLE_ATTR));
        assertEquals(HOMECONTROLLER_VIEW, result.getViewName());
    }

    @Test
    public void test_UserNameEmpty() {
        when(request.queryParams(center.SESSION_USER)).thenReturn(NAME_EMPTY);

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
        assertEquals(TITLE, vm.get(TITLE_ATTR));
        assertEquals(USERNAME_EMPTY, vm.get(MESSAGE_ATTR));
        assertEquals(SIGNIN_VIEW, result.getViewName());
    }

    @Test
    public void test_UserNameTooLong() {
        when(request.queryParams(center.SESSION_USER)).thenReturn(NAME_TOO_LONG);

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
        assertEquals(TITLE, vm.get(TITLE_ATTR));
        assertEquals(USERNAME_TOO_LONG, vm.get(MESSAGE_ATTR));
        assertEquals(SIGNIN_VIEW, result.getViewName());
    }

    @Test
    public void test_UserNameInvalid() {
        when(request.queryParams(center.SESSION_USER)).thenReturn(NAME_HAS_ILLEGAL_CHARS);

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
        assertEquals(TITLE, vm.get(TITLE_ATTR));
        assertEquals(USERNAME_INVALID_CHARS, vm.get(MESSAGE_ATTR));
        assertEquals(SIGNIN_VIEW, result.getViewName());
    }

    @Test
    public void test_UserNameInUse() {
        testPlayer = new Player(NAME_VALID2);

        //Add User
        center.addPlayer(testPlayer, session);

        when(request.queryParams(center.SESSION_USER)).thenReturn(NAME_VALID2);

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
        assertEquals(TITLE, vm.get(TITLE_ATTR));
        assertEquals(USERNAME_IN_USE, vm.get(MESSAGE_ATTR));
        assertEquals(SIGNIN_VIEW, result.getViewName());
    }
}