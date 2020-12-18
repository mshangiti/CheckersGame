package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Challenge;
import com.webcheckers.model.Player;
import com.webcheckers.ui.PostHomeRoute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spark.ModelAndView;
import spark.Request;
import spark.Session;
import spark.Response;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostHomeRouteTest {

    /*
  class attributes initialized
   */


    static final String TITLE = "Welcome";
    private static final String TITLE_ATTR = "title";
    static final int CHALLENGE_OPERATION_NUM_zero= 0;
    static final int CHALLENGE_OPERATION_NUM_one= 1;
    static final int CHALLENGE_OPERATION_NUM_two= 2;
    static final int CHALLENGE_OPERATION_NUM_three= 3;
    static final int CHALLENGE_OPERATION_NUM_four= 4;
    static final int CHALLENGE_OPERATION_NUM_five= 5;



    private final GameCenter gameCenter = new GameCenter();
    private final PostHomeRoute CuT = new PostHomeRoute(gameCenter);
    private final String challengerName = "Moe";
    private final String opponentName = "Harsha";
    private Player challenger;
    private Player opponent;

    //mock objects

    private Request request_ch;
    private Session session_ch;
    private Request request_op;
    private Session session_op;


    //friendly dependencies
    private GameCenter Gcenter;



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


    /**
     * Test a case where the wrong operation code is submitted
     * for reference, here're the different possibilities: 1, 2,3,4
     * anything else should show an error
     */
    @Test
    public void test_operation_code_wrong_number(){

        //test sending operation = 0
        when(request_ch.queryParams(CuT.OPERATION_ATT)).thenReturn(String.valueOf(CHALLENGE_OPERATION_NUM_zero));
        final Response response = mock(Response.class);

        //Get Results
        final ModelAndView result = CuT.handle(request_ch,response);

        //Verify result is not null
        assertNotNull(result);

        // Verify model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);

        // View-Model Information is correct
        final Map<String, Object> vm = (Map<String, Object>) model;
        assertEquals(vm.get(CuT.MESSAGE_ATTR), CuT.INVALID_CHOICE_ERR_MSG);
        assertEquals(HomeController.VIEW_NAME, result.getViewName());

    }



    /**
     * Test a challege (challenger sending request for a challenge to opponent)
     */
    @Test
    public void test_challenge_request(){

        //mimic a POST request with OPERATION_ATT = 1 to page
        // validate PLAYER_CHALLENGE_STATUS, IS_CHALLENGED_ATTR, OPPONENT_NAME_ATTR
        // PLAYER_CHALLENGE_STATUS has the current challenge state for the player
        // IS_CHALLENGED_ATTR should be false for challenger and true for opponent
        // OPPONENT_NAME_ATTR should have the other player name
        when(request_ch.queryParams(CuT.OPERATION_ATT)).thenReturn(String.valueOf(CHALLENGE_OPERATION_NUM_one));
        when(request_ch.queryParams(CuT.OPPONENT_NAME_ATTR)).thenReturn(opponentName);
        final Response response = mock(Response.class);


        //Get Results for challenger
        final ModelAndView result = CuT.handle(request_ch,response);

        //Verify result is not null
        assertNotNull(result);

        // Verify model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);

        // View-Model Information is correct
        final Map<String, Object> vm = (Map<String, Object>) model;
        assertEquals(TITLE, vm.get(TITLE_ATTR));
        assertEquals(Challenge.State.CHALLENGE_PENDING, vm.get(HomeController.PLAYER_CHALLENGE_STATUS));
        assertEquals(false, vm.get(HomeController.IS_CHALLENGED_ATTR));
        assertEquals(opponentName, vm.get(HomeController.OPPONENT_NAME_ATTR));
        assertEquals(-1, vm.get(HomeController.PLAYER_COUNT_ATTR));



        //Get Results for opponent
        final ModelAndView result_op = CuT.handle(request_op,response);
        //Verify result is not null
        assertNotNull(result_op);
        // Verify model is a non-null Map
        final Object model_op = result_op.getModel();
        assertNotNull(model_op);
        assertTrue(model_op instanceof Map);
        // View-Model Information is correct
        final Map<String, Object> vm_op = (Map<String, Object>) model_op;
        assertEquals(TITLE, vm_op.get(TITLE_ATTR));
        assertEquals(Challenge.State.CHALLENGE_PENDING, vm_op.get(HomeController.PLAYER_CHALLENGE_STATUS));
        assertEquals(true, vm_op.get(HomeController.IS_CHALLENGED_ATTR));
        assertEquals(challengerName, vm_op.get(HomeController.OPPONENT_NAME_ATTR));
        assertEquals(-1, vm_op.get(HomeController.PLAYER_COUNT_ATTR));

    }

    /**
     * Test a cancel challenge request (challenger cancelling his request for a challenge)
     */
    @Test
    public void test_challenge_cancel_request(){

        //mimic a POST request with OPERATION_ATT = 2 to page and validate that the status of the challenge has been changed
        //creating a challenge
        gameCenter.challengePlayer(session_ch,opponentName);
        when(request_ch.queryParams(PostHomeRoute.OPERATION_ATT)).thenReturn(String.valueOf(CHALLENGE_OPERATION_NUM_two));
        final Response response = mock(Response.class);
        //Get Results
        final ModelAndView result = CuT.handle(request_ch,response);

        //Verify result is not null
        assertNotNull(result);

        // Verify model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);

        // View-Model Information is correct
        final Map<String, Object> vm = (Map<String, Object>) model;
        assertEquals(TITLE, vm.get(TITLE_ATTR));
        assertEquals(Challenge.State.CHALLENGE_CANCELLED, vm.get(HomeController.PLAYER_CHALLENGE_STATUS));
        assertEquals(opponentName, vm.get(HomeController.OPPONENT_NAME_ATTR));
        assertEquals(-1, vm.get(HomeController.PLAYER_COUNT_ATTR));

        //Get Results for opponent
        final ModelAndView result_op = CuT.handle(request_op,response);
        //Verify result is not null
        assertNotNull(result_op);
        // Verify model is a non-null Map
        final Object model_op = result_op.getModel();
        assertNotNull(model_op);
        assertTrue(model_op instanceof Map);
        // View-Model Information is correct
        final Map<String, Object> vm_op = (Map<String, Object>) model_op;
        assertEquals(TITLE, vm_op.get(TITLE_ATTR));
        assertEquals(Challenge.State.CHALLENGE_CANCELLED, vm_op.get(HomeController.PLAYER_CHALLENGE_STATUS));
        assertEquals(true, vm_op.get(HomeController.IS_CHALLENGED_ATTR));
        assertEquals(challengerName, vm_op.get(HomeController.OPPONENT_NAME_ATTR));
        assertEquals(-1, vm_op.get(HomeController.PLAYER_COUNT_ATTR));
    }


    /**
     * Test a decline challenge request (opponent declining the challenge)
     */
    @Test
    public void test_challenge_decline_request(){

        //mimic a POST request with OPERATION_ATT = 4 to page and validate that the status of the challenge has been changed
        //creating a challenge
        gameCenter.challengePlayer(session_ch,opponentName);
        when(request_ch.queryParams(PostHomeRoute.OPERATION_ATT)).thenReturn(String.valueOf(CHALLENGE_OPERATION_NUM_four));
        final Response response = mock(Response.class);
        //Get Results
        final ModelAndView result = CuT.handle(request_ch,response);

        //Verify result is not null
        assertNotNull(result);

        // Verify model is a non-null Map
        final Object model = result.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);

        // View-Model Information is correct
        final Map<String, Object> vm = (Map<String, Object>) model;
        assertEquals(TITLE, vm.get(TITLE_ATTR));
        assertEquals(Challenge.State.CHALLENGE_DECLINED, vm.get(HomeController.PLAYER_CHALLENGE_STATUS));
    }

}

