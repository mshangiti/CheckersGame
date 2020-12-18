package com.webcheckers.model;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * The unit test suite for the {@link Piece} component.
 *
 */
public class PieceTest {

    private Crown single = Crown.SINGLE;
    private PieceColor colorWhite = PieceColor.WHITE;
    private PieceColor colorRed = PieceColor.RED;

    private Piece testPiece1 = new Piece(colorRed);
    private Piece testPiece2 = new Piece(colorWhite);


    /**
     * Test piece type is single
     */
    @Test
    public void test_Type(){
        final Crown test = testPiece1.getType();
        assertSame(single,test);
    }

    /**
     * Test piece color is red
     */
    @Test
    public void test_colorRed(){
        final PieceColor test = testPiece1.getColor();
        assertSame(colorRed,test);
    }


    /**
     * Test piece color is white
     */
    @Test
    public void test_colorWhite(){
        final PieceColor test = testPiece2.getColor();
        assertSame(colorWhite,test);
    }

}
