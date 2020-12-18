package com.webcheckers.model;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * The unit test suite for the {@link Board} component.
 *
 */

public class BoardTest {

    private int size = 8;
    Board CuT = new Board();

    //test the board size
    @Test
    public void test_BoardSize() {
        assertEquals(size,CuT.getBoardSize());
    }
    @Test
    public void test_Iterator() {
        Iterator IteratorRows = CuT.iterator();
        assertNotNull(IteratorRows);
        assertTrue(IteratorRows instanceof Iterator);
    }
}