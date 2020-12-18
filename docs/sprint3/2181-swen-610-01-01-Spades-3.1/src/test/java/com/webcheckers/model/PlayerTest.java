package com.webcheckers.model;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * The unit test suite for the {@link Player} component.
 *
 */
public class PlayerTest {

    /**
     * Test name has correct length
     */

    private static final String NAME_TOO_SHORT = "";
    private static final String NAME_TOO_LONG = "ABCDEFGHJKLMNOPQRTUVWXYZASASCVFE123456789";
    private static final String NAME_HAS_ILLEGAL_CHARS = "mshang#$@";
    private static final String NAME_VALID = "Moe";

    /**
     * Test player's name is not empty
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_empty_username() {
        new Player(NAME_TOO_SHORT);
    }

    /**
     * Test player's name is not too long
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_too_long_username() {
        new Player(NAME_TOO_LONG);
    }

    /**
     * Test player's name does not have illegal chars
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_illegal_chars_username() {
        new Player(NAME_HAS_ILLEGAL_CHARS);
    }

    /**
     * Test that name assignment is done correctly when name is valid
     */
    @Test
    public void test_valid_name() {
        final Player CuT = new Player(NAME_VALID);
        assertEquals(CuT.getName(),NAME_VALID);
    }

    /**
     * Test that player status change is done correctly
     */
    @Test
    public void test_playerStateChange() {
        final Player CuT = new Player(NAME_VALID);
        CuT.setStatus(Player.State.IN_GAME);
        assertEquals(CuT.getStatus(),Player.State.IN_GAME);

        CuT.setStatus(Player.State.IN_LOBBY);
        assertEquals(CuT.getStatus(),Player.State.IN_LOBBY);
    }
}
