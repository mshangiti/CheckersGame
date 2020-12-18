package com.webcheckers.model;

import com.webcheckers.appl.GameCenter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Session;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test suite for the {@link Challenge} component.
 *
 */
public class ChallengeTest {

    /*
   class attributes initialized
    */
    private String challengerName = "Moe";
    private String opponentName = "Harsha";
    private Challenge CuT;

    /**
     * Setup new mock objects for each test.
     */
    @Before
    public void setup() {
        CuT = new Challenge(challengerName,opponentName);
    }


    /**
     * Test constructor
     */
    @Test
    public void test_new_challenge_creation(){

        //challenger name setup correct?
        assertSame(challengerName,CuT.getChallengerName());
        //opponent name setup correct?
        assertSame(opponentName,CuT.getOpponentName());
        //status setup correct?
        assertSame(Challenge.State.CHALLENGE_PENDING,CuT.getStatus());
    }

    /**
     * Test getters and setters
     */
    @Test
    public void test_status_change(){
        CuT.setStatus(Challenge.State.NO_CHALLENGES);
        assertSame(Challenge.State.NO_CHALLENGES, CuT.getStatus());

        CuT.setStatus(Challenge.State.CHALLENGE_PENDING);
        assertSame(Challenge.State.CHALLENGE_PENDING, CuT.getStatus());

        CuT.setStatus(Challenge.State.CHALLENGE_ACCEPTED);
        assertSame(Challenge.State.CHALLENGE_ACCEPTED, CuT.getStatus());

        CuT.setStatus(Challenge.State.CHALLENGE_DECLINED);
        assertSame(Challenge.State.CHALLENGE_DECLINED, CuT.getStatus());

        CuT.setStatus(Challenge.State.CHALLENGE_CANCELLED);
        assertSame(Challenge.State.CHALLENGE_CANCELLED, CuT.getStatus());
    }


}
