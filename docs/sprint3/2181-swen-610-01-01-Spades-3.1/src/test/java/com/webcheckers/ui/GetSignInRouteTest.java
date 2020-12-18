package com.webcheckers.ui;
import com.webcheckers.appl.GameCenter;
import org.junit.Before;
import org.junit.Test;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;
import com.webcheckers.model.Player;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test suite for the {@link GetSignInRoute} component.
 */
public class GetSignInRouteTest {

    private GetSignInRoute CuT;

    // mock objects
    private Request request;
    private Session session;
    private Response response;

    private GameCenter center;



    /**
     * Setup new mock objects for each test.
     */
    @Before
    public void setup() {
        request = mock(Request.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);

        center = new GameCenter();
        CuT = new GetSignInRoute(center);
    }


    /**
     * Test that CuT shows the login view when the session is brand new
     */
    @Test
    public void test_user_not_loggedin() {
        //test if login-in page is viewed with new session
        when(session.isNew()).thenReturn(true);
        when(session.attribute(GameCenter.SESSION_USER)).thenReturn(null);

        //invoke test
        final Response response = mock(Response.class);

        // Invoke the test
        final ModelAndView result = CuT.handle(request, response);

        // Analyze the results:
        //   * result is non-null
        assertNotNull(result);
        //   * model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);
        //   * model contains all necessary View-Model data
        @SuppressWarnings("unchecked")
        final Map<String, Object> vm = (Map<String, Object>) model;
        assertEquals(GetSignInRoute.TITLE, vm.get(GetSignInRoute.TITLE_ATTR));
        //   * test view name
        assertEquals(GetSignInRoute.VIEW_NAME, result.getViewName());
    }


    /**
     * When user is loggedin, the '/sigin' page should redirected to home
     */

//    @Test(expected = spark.HaltException.class)
    @Test(expected = NullPointerException.class)
    public void test_user_is_loggedIn(){

        // Arrange the test scenario: There is an existing session with a name
        final Player player = new Player("Moe");
        when(request.session()).thenReturn(session);
        when(session.attribute(center.SESSION_USER)).thenReturn(player);
//        when(session.attribute(eq(GameCenter.SESSION_USER))).thenReturn(player);

        // Invoke the test
        final ModelAndView result = CuT.handle(request, response);

        // Analyze the results:
        assertNull(result);
    }


}
