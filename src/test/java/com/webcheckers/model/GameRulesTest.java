package com.webcheckers.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameRulesTest {
    //Static variables
    private static final String VALID = "Valid move";
    private static final String SKIPAVAILABLE = "Valid move, but there are more pieces to be captured. Keep skipping.";

    //Mock Dependencies
    private Game game = new Game(new Player("Player1"), new Player("Player2"));
    private Board pendingBoard;
    private Board baseBoard;

    //Dependencies
    private Move downwardLeft_nonCapturing_move= new Move(new Position(0,7), new Position(1,6));
    private Move downwardLeft_Capturing_move= new Move(new Position(0,7), new Position(2,5));

    private Move downwardRight_nonCapturing_move= new Move(new Position(0,1), new Position(1,2));
    private Move downwardRight_Capturing_move= new Move(new Position(0,1), new Position(2,3));

    private Move upwardLeft_nonCapturing_move= new Move(new Position(7,6), new Position(6,5));
    private Move upwardLeft_Capturing_move= new Move(new Position(7,6), new Position(5,4));

    private Move upwardRight_nonCapturing_move= new Move(new Position(7,0), new Position(6,1));
    private Move upwardRight_Capturing_move= new Move(new Position(7,0), new Position(5,2));

    //CuT
    GameRules CuT = new GameRules();

    //Set up for board
    private void setupBoard(Move move, Board board)
    {
        //Set board to desired state
        Iterator iteratorRow = board.iterator();

        while (iteratorRow.hasNext()) {
            // Get next row and its index
            Row row = (Row)iteratorRow.next();
            int rowIndex = row.getIndex();

            for (Space space : row) {

                // Get next cell and its index
                int spaceIndex = space.getCellIdx();

                if (spaceIndex != move.getStart().getCell() || rowIndex != move.getStart().getRow())
                {
                    board.capturePiece(rowIndex, spaceIndex);
                }
            }
        }
    }

    private void setupBoard_Capture(Move move, Position intermediate, Position enemyPiece, Board board)
    {
        //Set board to desired state
        Iterator iteratorRow = board.iterator();

        //Delete current piece in captures position
        board.capturePiece(intermediate.getRow(), intermediate.getCell());

        //Replace with capturable piece
        board.updateBoard(new Move(enemyPiece, intermediate));

        while (iteratorRow.hasNext()) {
            // Get next row and its index
            Row row = (Row)iteratorRow.next();
            int rowIndex = row.getIndex();

            for (Space space : row) {

                // Get next cell and its index
                int spaceIndex = space.getCellIdx();

                if (spaceIndex != move.getStart().getCell() || rowIndex != move.getStart().getRow())
                {
                    if (rowIndex != intermediate.getRow() || spaceIndex != intermediate.getCell())
                    {
                        board.capturePiece(rowIndex, spaceIndex);
                    }
                }
            }
        }
    }

    //Set up for board - Crown movements
    private void setupBoard_NonCapturingCrown(Move move, Position correctPiece, Board board)
    {
        //Set board to desired state
        Iterator iteratorRow = board.iterator();

        //Crown
        board.getPiece(correctPiece.getRow(), correctPiece.getCell()).setType(Crown.KING);

        //Delete current piece
        board.capturePiece(move.getStart().getRow(), move.getStart().getCell());

        //Replace with correct piece
        board.updateBoard(new Move(correctPiece, move.getStart()));

        while (iteratorRow.hasNext()) {
            // Get next row and its index
            Row row = (Row)iteratorRow.next();
            int rowIndex = row.getIndex();

            for (Space space : row) {

                // Get next cell and its index
                int spaceIndex = space.getCellIdx();

                if (spaceIndex != move.getStart().getCell() || rowIndex != move.getStart().getRow())
                {
                    board.capturePiece(rowIndex, spaceIndex);
                }
            }
        }
    }

    //Set up for board - Crown movements
    private void setupBoard_CapturingCrown(Move move, Position correctPiece, Position intermediate, Position enemyPiece, Board board)
    {
        //Set board to desired state
        Iterator iteratorRow = board.iterator();

        //Crown
        board.getPiece(correctPiece.getRow(), correctPiece.getCell()).setType(Crown.KING);

        //Delete current piece
        board.capturePiece(move.getStart().getRow(), move.getStart().getCell());

        //Replace with correct piece
        board.updateBoard(new Move(correctPiece, move.getStart()));

        //Delete current piece in captures position
        board.capturePiece(intermediate.getRow(), intermediate.getCell());

        //Replace with capturable piece
        board.updateBoard(new Move(enemyPiece, intermediate));

        while (iteratorRow.hasNext()) {
            // Get next row and its index
            Row row = (Row)iteratorRow.next();
            int rowIndex = row.getIndex();

            for (Space space : row) {

                // Get next cell and its index
                int spaceIndex = space.getCellIdx();

                if (spaceIndex != move.getStart().getCell() || rowIndex != move.getStart().getRow())
                {
                    if (rowIndex != intermediate.getRow() || spaceIndex != intermediate.getCell())
                    {
                        board.capturePiece(rowIndex, spaceIndex);
                    }
                }
            }
        }
    }

    //Set up for board - Skipping
    private void setupBoard_Skipping(Board board)
    {
        Position start = new Position(0, 1);

        Position capture1 = new Position(1, 2);
        Position capture2 = new Position(3, 4);

        Position enemy1 = new Position(7, 6);
        Position enemy2 = new Position(7, 0);

        //Set enemies
        board.capturePiece(capture1.getRow(), capture1.getCell());
        board.capturePiece(capture2.getRow(), capture2.getCell());
        board.updateBoard(new Move(enemy1, capture1));
        board.updateBoard(new Move(enemy2, capture2));

        //Set board to desired state
        Iterator iteratorRow = board.iterator();

        while (iteratorRow.hasNext()) {
            // Get next row and its index
            Row row = (Row)iteratorRow.next();
            int rowIndex = row.getIndex();

            for (Space space : row) {

                // Get next cell and its index
                int spaceIndex = space.getCellIdx();

                if (    (spaceIndex == start.getCell() && rowIndex == start.getRow()) ||
                        (spaceIndex == capture1.getCell() && rowIndex == capture1.getRow()) ||
                        (spaceIndex == capture2.getCell() && rowIndex == capture2.getRow())
                )
                {
                    continue;
                }
                else
                {
                    board.capturePiece(rowIndex, spaceIndex);
                }
            }
        }
    }

    @Before
    public void setup() {

        game.InitializeGame();
        baseBoard = game.getBoard();
    }

    @Test
    public void test_validateMove_nonCaptureWhiteLeft() {
        setupBoard(upwardLeft_nonCapturing_move, baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, upwardLeft_nonCapturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_nonCaptureWhiteRight() {
        setupBoard(upwardRight_nonCapturing_move, baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, upwardRight_nonCapturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_nonCaptureRedLeft() {
        setupBoard(downwardLeft_nonCapturing_move, baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, downwardLeft_nonCapturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_nonCaptureRedRight() {
        setupBoard(downwardRight_nonCapturing_move, baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, downwardRight_nonCapturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_CaptureWhiteLeft() {
        setupBoard_Capture(upwardLeft_Capturing_move, upwardLeft_nonCapturing_move.getEnd(), downwardLeft_nonCapturing_move.getStart(), baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, upwardLeft_Capturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_CaptureWhiteRight() {
        setupBoard_Capture(upwardRight_Capturing_move, upwardRight_nonCapturing_move.getEnd(), downwardLeft_nonCapturing_move.getStart(), baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, upwardRight_Capturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_CaptureRedLeft() {
        setupBoard_Capture(downwardLeft_Capturing_move, downwardLeft_nonCapturing_move.getEnd(), upwardLeft_nonCapturing_move.getStart(), baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, downwardLeft_Capturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_CaptureRedRight() {
        setupBoard_Capture(downwardRight_Capturing_move, downwardRight_nonCapturing_move.getEnd(), upwardLeft_nonCapturing_move.getStart(), baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, downwardRight_Capturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_nonCaptureCrownedWhiteUpLeft() {
        setupBoard_NonCapturingCrown(upwardLeft_nonCapturing_move, upwardRight_Capturing_move.getStart(), baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, upwardLeft_nonCapturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_nonCaptureCrownedWhiteUpRight() {
        setupBoard_NonCapturingCrown(upwardRight_nonCapturing_move, upwardLeft_nonCapturing_move.getStart(), baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, upwardRight_nonCapturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_nonCaptureCrownedWhiteDownLeft() {
        setupBoard_NonCapturingCrown(downwardLeft_nonCapturing_move, upwardLeft_nonCapturing_move.getStart(), baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, downwardLeft_nonCapturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_nonCaptureCrownedWhiteDownRight() {
        setupBoard_NonCapturingCrown(downwardRight_nonCapturing_move, upwardLeft_nonCapturing_move.getStart(), baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, downwardRight_nonCapturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_nonCaptureCrownedRedUpLeft() {
        setupBoard_NonCapturingCrown(upwardLeft_nonCapturing_move, downwardLeft_Capturing_move.getStart(), baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, upwardLeft_nonCapturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_nonCaptureCrownedRedUpRight() {
        setupBoard_NonCapturingCrown(upwardRight_nonCapturing_move, downwardLeft_Capturing_move.getStart(), baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, upwardRight_nonCapturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_nonCaptureCrownedRedDownLeft() {
        setupBoard_NonCapturingCrown(downwardLeft_nonCapturing_move, downwardRight_Capturing_move.getStart(), baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, downwardLeft_nonCapturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_nonCaptureCrownedRedDownRight() {
        setupBoard_NonCapturingCrown(downwardRight_nonCapturing_move, downwardLeft_Capturing_move.getStart(), baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, downwardRight_nonCapturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_CaptureCrownedWhiteUpLeft() {
        Move captureMove = upwardLeft_Capturing_move;
        Position correctPiece = upwardRight_Capturing_move.getStart();
        Position intermediate = upwardLeft_nonCapturing_move.getEnd();
        Position enemy = downwardLeft_Capturing_move.getStart();
        setupBoard_CapturingCrown(captureMove, correctPiece, intermediate, enemy, baseBoard);

        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, upwardLeft_Capturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_CaptureCrownedWhiteUpRight() {
        Move captureMove = upwardRight_Capturing_move;
        Position correctPiece = upwardLeft_nonCapturing_move.getStart();
        Position intermediate = upwardRight_nonCapturing_move.getEnd();
        Position enemy = downwardLeft_Capturing_move.getStart();
        setupBoard_CapturingCrown(captureMove, correctPiece, intermediate, enemy, baseBoard);

        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, upwardRight_Capturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_CaptureCrownedWhiteDownLeft() {
        Move captureMove = downwardLeft_Capturing_move;
        Position correctPiece = upwardLeft_nonCapturing_move.getStart();
        Position intermediate = downwardLeft_nonCapturing_move.getEnd();
        Position enemy = downwardRight_Capturing_move.getStart();
        setupBoard_CapturingCrown(captureMove, correctPiece, intermediate, enemy, baseBoard);

        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, downwardLeft_Capturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_CaptureCrownedWhiteDownRight() {
        Move captureMove = downwardRight_Capturing_move;
        Position correctPiece = upwardLeft_nonCapturing_move.getStart();
        Position intermediate = downwardRight_nonCapturing_move.getEnd();
        Position enemy = downwardLeft_Capturing_move.getStart();
        setupBoard_CapturingCrown(captureMove, correctPiece, intermediate, enemy, baseBoard);

        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, downwardRight_Capturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_CaptureCrownedRedUpLeft() {
        Move captureMove = upwardLeft_Capturing_move;
        Position correctPiece = downwardLeft_Capturing_move.getStart();
        Position intermediate = upwardLeft_nonCapturing_move.getEnd();
        Position enemy = upwardRight_Capturing_move.getStart();
        setupBoard_CapturingCrown(captureMove, correctPiece, intermediate, enemy, baseBoard);

        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, upwardLeft_Capturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_CaptureCrownedRedUpRight() {
        Move captureMove = upwardRight_Capturing_move;
        Position correctPiece = downwardLeft_Capturing_move.getStart();
        Position intermediate = upwardRight_nonCapturing_move.getEnd();
        Position enemy = upwardLeft_Capturing_move.getStart();
        setupBoard_CapturingCrown(captureMove, correctPiece, intermediate, enemy, baseBoard);

        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, upwardRight_Capturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_CaptureCrownedRedDownLeft() {
        Move captureMove = downwardLeft_Capturing_move;
        Position correctPiece = downwardRight_Capturing_move.getStart();
        Position intermediate = downwardLeft_nonCapturing_move.getEnd();
        Position enemy = upwardRight_Capturing_move.getStart();
        setupBoard_CapturingCrown(captureMove, correctPiece, intermediate, enemy, baseBoard);

        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, downwardLeft_Capturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }

    @Test
    public void test_validateMove_CaptureCrownedRedDownRight() {
        Move captureMove = downwardRight_Capturing_move;
        Position correctPiece = downwardLeft_Capturing_move.getStart();
        Position intermediate =  downwardRight_nonCapturing_move.getEnd();
        Position enemy = upwardRight_Capturing_move.getStart();
        setupBoard_CapturingCrown(captureMove, correctPiece, intermediate, enemy, baseBoard);

        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, downwardRight_Capturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(VALID, result.getText());
        assertEquals(GameState.PENDING, game.getState());
    }


    @Test
    public void test_validateMove_Skip() {
        setupBoard_Skipping(baseBoard);
        game.resetPendingBoard();
        pendingBoard = game.getPendingBoard();

        Message result = CuT.validateMove(game, downwardRight_Capturing_move, pendingBoard, baseBoard);

        assertEquals(MessageType.info, result.getType());
        assertEquals(SKIPAVAILABLE, result.getText());
        assertEquals(GameState.SKIP, game.getState());
    }











    @Test
    public void test_checkSkip() {
    }

    @Test
    public void isCaptureAvailable() {
    }
}