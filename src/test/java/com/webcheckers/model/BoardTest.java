package com.webcheckers.model;

import org.junit.Before;
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

    private void setup_blockage_board(boolean crownPiece)
    {
        //keeping only 1 red piece
        CuT.capturePiece(0,1);
        CuT.capturePiece(0,3);
        CuT.capturePiece(0,5);
        CuT.capturePiece(0,7);

        CuT.capturePiece(1,0);
        CuT.capturePiece(1,2);
        CuT.capturePiece(1,4);
        CuT.capturePiece(1,6);

        CuT.capturePiece(2,1);
        CuT.capturePiece(2,3);
        CuT.capturePiece(2,5);//keeping this piece
        //CuT.capturePiece(2,7);//keeping this piece

        //keep only 2 white pieces
        CuT.capturePiece(5,0);
        CuT.capturePiece(5,2);
        CuT.capturePiece(5,4);
        //CuT.capturePiece(5,6);//keeping this one

        CuT.capturePiece(6,1);
        CuT.capturePiece(6,3);
        //CuT.capturePiece(6,5);//keeping this one
        CuT.capturePiece(6,7);

        CuT.capturePiece(7,0);
        CuT.capturePiece(7,2);
        CuT.capturePiece(7,4);
        CuT.capturePiece(7,6);

        //moving remaining pieces to location
        Position blockedRedPiecePosition = new Position(4,7);

        // simulate a blockage
        //move red piece to location
        CuT.updateBoard(new Move(new Position(2,7), blockedRedPiecePosition));

        if(crownPiece){
            //crowning the two white pieces
            CuT.getPiece(5,6).setType(Crown.KING);
            CuT.getPiece(6,5).setType(Crown.KING);
        }
    }

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

    //testing the move
    @Test
    public void test_check_single_piece_move_available_when_available(){
        //init setup
        assertTrue(CuT.checkMoveAvailable(PieceColor.RED));
        assertTrue(CuT.checkMoveAvailable(PieceColor.WHITE));

        //check move available with capture
        Position redPieceInitPosition = new Position(2,1);
        Position redPieceCapturablePosition = new Position(4,3);
        CuT.updateBoard(new Move(redPieceInitPosition,redPieceCapturablePosition));
        assertTrue(CuT.checkMoveAvailable(PieceColor.WHITE));

        //specific cases for RED
        //case 1: if piece in the middle of the board
        CuT.capturePiece(0,1);
        CuT.capturePiece(0,3);
        CuT.capturePiece(0,5);
        CuT.capturePiece(0,7);
        assertTrue(CuT.checkMoveAvailable(PieceColor.RED));
        //case 2: if the piece was at the end of the board
        CuT.capturePiece(1,0);
        CuT.capturePiece(1,2);
        CuT.capturePiece(1,4);
        CuT.capturePiece(1,6);
        CuT.capturePiece(2,1);
        CuT.capturePiece(2,3);
        CuT.capturePiece(2,5);//keeping this piece
        //CuT.capturePiece(2,7);//keeping this piece
        assertTrue(CuT.checkMoveAvailable(PieceColor.RED));


        //specific cases for WHITE
        //case 1: if piece in the middle of the board
        CuT.capturePiece(5,0);
        CuT.capturePiece(5,2);
        CuT.capturePiece(5,4);
        CuT.capturePiece(5,6);
        assertTrue(CuT.checkMoveAvailable(PieceColor.WHITE));
        //case 2: if the piece was at the end of the board
        CuT.capturePiece(6,1);
        CuT.capturePiece(6,3);
        CuT.capturePiece(6,5);
        //CuT.capturePiece(6,7);//keeping this one
        CuT.capturePiece(7,0);
        CuT.capturePiece(7,2);
        CuT.capturePiece(7,4);
        CuT.capturePiece(7,6);
        assertTrue(CuT.checkMoveAvailable(PieceColor.WHITE));

    }

    @Test
    public void test_check_single_piece_move_available_when_not_available(){
        setup_blockage_board(false);
        //red piece is blocked
        assertFalse(CuT.checkMoveAvailable(PieceColor.RED));
        //white piece can move
        assertTrue(CuT.checkMoveAvailable(PieceColor.WHITE));
    }

    @Test
    public void test_check_king_piece_move_available_when_available(){
        //crowning 1 red piece
        CuT.getPiece(1,0).setType(Crown.KING);
        //crowning 1 white piece
        CuT.getPiece(5,0).setType(Crown.KING);
        assertTrue(CuT.checkMoveAvailable(PieceColor.RED));
        assertTrue(CuT.checkMoveAvailable(PieceColor.WHITE));
    }

    @Test
    public void test_check_king_piece_move_available_when_not_available(){
        setup_blockage_board(true);
        //red piece is blocked
        assertFalse(CuT.checkMoveAvailable(PieceColor.RED));
        //white piece can move
        assertTrue(CuT.checkMoveAvailable(PieceColor.WHITE));
    }

    @Test
    public void test_board_still_has_pieces_with_red_color(){

        //init case
        assertTrue(CuT.checkBoardStillHasPiecesWithColor(PieceColor.RED));

        //removing all red pieces
        CuT.capturePiece(0,1);
        CuT.capturePiece(0,3);
        CuT.capturePiece(0,5);
        CuT.capturePiece(0,7);

        CuT.capturePiece(1,0);
        CuT.capturePiece(1,2);
        CuT.capturePiece(1,4);
        CuT.capturePiece(1,6);

        CuT.capturePiece(2,1);
        CuT.capturePiece(2,3);
        CuT.capturePiece(2,5);
        CuT.capturePiece(2,7);

        //no more
        assertFalse(CuT.checkBoardStillHasPiecesWithColor(PieceColor.RED));
    }

    @Test
    public void test_board_still_has_pieces_with_white_color(){

        //init case
        assertTrue(CuT.checkBoardStillHasPiecesWithColor(PieceColor.WHITE));

        //removing all white pieces
        CuT.capturePiece(5,0);
        CuT.capturePiece(5,2);
        CuT.capturePiece(5,4);
        CuT.capturePiece(5,6);

        CuT.capturePiece(6,1);
        CuT.capturePiece(6,3);
        CuT.capturePiece(6,5);
        CuT.capturePiece(6,7);

        CuT.capturePiece(7,0);
        CuT.capturePiece(7,2);
        CuT.capturePiece(7,4);
        CuT.capturePiece(7,6);

        //no more
        assertFalse(CuT.checkBoardStillHasPiecesWithColor(PieceColor.WHITE));
    }
//
//    @Test void test_check_if_upward_movement_possible(){
//
//    }
//
//    @Test void test_check_if_board_still_has_pieces(){
//
//    }
//
//    @Test void test_check_if_player_movement_is_blocked(){
//
//    }
}