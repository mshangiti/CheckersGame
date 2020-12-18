package com.webcheckers.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class MoveTest {

    static Position START = new Position(0, 7);
    static Position END = new Position(1, 6);

    @Test
    public void test_emptyConstructor() {
        Move CuT = new Move();

        assertNotNull(CuT);
    }

    @Test
    public void test_ConstructorParameters() {
        Move CuT = new Move(START, END);

        assertEquals(START, CuT.getStart());
        assertEquals(END, CuT.getEnd());
    }

    @Test
    public void getStart() {
        Move CuT = new Move(START, END);

        assertEquals(START, CuT.getStart());
    }

    @Test
    public void getEnd() {
        Move CuT = new Move(START, END);

        assertEquals(END, CuT.getEnd());
    }
}