package com.webcheckers.model;
import org.junit.Test;

import static org.junit.Assert.*;

public class PositionTest {

    static int ROW = 0;
    static int CELL = 7;

    @Test
    public void test_EmptyConstructorr() {
        Position CuT = new Position();

        //Assert object was created
        assertNotNull(CuT);
    }

    @Test
    public void test_getRow() {
        Position CuT = new Position(ROW, CELL);
        assertEquals(ROW, CuT.getRow());
    }

    @Test
    public void test_getCell() {
        Position CuT = new Position(ROW, CELL);
        assertEquals(CELL, CuT.getCell());
    }

    @Test
    public void test_clone() {
        Position CuT = new Position(ROW, CELL);

        Position result = CuT.clone();

        assertNotEquals(result.hashCode(), CuT.hashCode());
        assertEquals(result.getRow(), CuT.getRow());
        assertEquals(result.getCell(), CuT.getCell());
    }

    @Test
    public void test_equals() {
        Position CuT = new Position(ROW, CELL);
        Position true_result = new Position(ROW, CELL);
        Position full_false_result = new Position(CELL, ROW);
        Position half_row_false_result = new Position(ROW, ROW);
        Position half_cell_false_result = new Position(CELL, CELL);

        // Check that false is return when a non Position object is provided
        assertFalse(CuT.equals(ROW));

        //Check that false is return when the Position row and cell attributes are not the same
        assertFalse(CuT.equals(full_false_result));

        //Check that false is return when the Position cell attributes is not the same
        assertFalse(CuT.equals(half_row_false_result));

        //Check that false is return when the Position row attributes is not the same
        assertFalse(CuT.equals(half_cell_false_result));

        //Check that true is return when the Position row and cell attributes are the same
        assertTrue(CuT.equals(true_result));

    }
}