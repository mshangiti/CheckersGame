package com.webcheckers.model;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Iterator;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
/**
 * The unit test suite for the {@link Space} component.
 *
 */
public class SpaceTest {

    private int test_cellIdx;
    private boolean test_isValid;
    private Piece test_piece;
    PieceColor colorRed = PieceColor.RED;
    PieceColor colorWhite = PieceColor.WHITE;

    @Test
    public void test_cellID(){

        Space test_sp = new Space(3,1);
        assertEquals(1,test_sp.getCellIdx());
    }

    //Check if space is available condition
    @Test
    public void test_space_available(){

        Space test_sp = new Space(4,1);
        assertTrue("true",test_sp.isValid());
    }

    //check space is not available when there is red coin placed in square
    @Test
    public void test_space_notAvailable_redPiecePlaced(){

        Space test_sp = new Space(2,1);
        assertFalse("false",test_sp.isValid());
    }

    //check space is not available when there is white coin placed in square
    @Test
    public void test_space_notAvailable_whitePiecePlaced(){

        Space test_sp = new Space(6,1);
        assertFalse("false",test_sp.isValid());
    }


    //When space not available with the no. of rows and no. of coloumns
    @Test
    public void test_space_notAvailable_even_rowsAndCols(){

        Space test_sp = new Space(2,2);
        assertFalse("false",test_sp.isValid());

    }

    //Check if red piece has occupied
    @Test
    public void test_space_occupiedByRED(){

        Space test_sp = new Space(2,7);
        assertSame(colorRed,test_sp.getPiece().getColor());

    }

    //Check if White piece has occupied
    @Test
    public void test_space_occupiedByWHITE(){

        Space test_sp = new Space(6,1);
        assertSame(colorWhite,test_sp.getPiece().getColor());

    }


}
