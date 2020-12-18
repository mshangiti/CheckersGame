package com.webcheckers.model;

import java.util.Iterator;

public class GameRules {

    private static final String VALID = "Valid move";
    private static final String INVALIDMOVE = "Invalid move. Single pieces must move forward to their immediate diagonals.";
    private static final String INVALIDCAPTURENOTEXIST = "Invalid move. You must capture an opposing piece to move two squares.";
    private static final String INVALIDCAPTURECOLOR = "Invalid move. You can only capture pieces of the opposing color.";
    private static final String CAPTUREAVAILABLE = "Invalid move. You must capture a piece if possible.";
    private static final String SKIPAVAILABLE = "Valid move, but there are more pieces to be captured. Keep skipping.";

    public GameRules() { }

    public Message validateMove(Game game, Move move, Board pendingBoard, Board baseBoard) {

        Piece piece = pendingBoard.getPiece(move.getStart().getRow(), move.getStart().getCell());
        PieceColor color = piece.getColor();

        //Assume the move is invalid. Will change if move is valid
        boolean valid = false;
        Message msg = new Message(INVALIDMOVE, MessageType.error);

        boolean capturingMove = false;

        // Check capturing moves
        if (!valid) { valid = checkSimpleCapturingMovement(pendingBoard, move, piece, msg); }
        if (!valid) { valid = checkCrownCapturingMovement(pendingBoard, move, piece, msg);}

        if (valid) {
            capturingMove = true;
        }

        // Check non-capturing move (Verify that there are not capturing move available)
        if (!valid)
        {
            //If a capture is available, skip checking and create message
            if (isCaptureAvailable(baseBoard, color)) {
                msg.setText(CAPTUREAVAILABLE);
                msg.setType(MessageType.error);
            }
            else {
                if (!valid) {
                    valid = checkSimpleMovement(move, piece, msg);
                }
                if (!valid) {
                    valid = checkCrownMovement(move, piece, msg);
                }
            }
        }

        if (valid) {
            if ( checkCrowningPiece(move, piece) ) {
                pendingBoard.updateBoard(move);
                game.setState(GameState.PENDING);
            }
            else if (capturingMove)
            {
                pendingBoard.updateBoard(move);
                checkSkip(game, baseBoard, pendingBoard, move, piece, msg);
            }
            else
            {
                pendingBoard.updateBoard(move);
                game.setState(GameState.PENDING);
            }

        }

        return msg;
    }

    public void checkSkip(Game game, Board baseBoard, Board pendingBoard, Move move, Piece piece, Message msg)
    {
        baseBoard.setMultiCap(false);

        if ( pendingBoard.checkCaptureAvailable(move.getEnd().getRow(), move.getEnd().getCell(), piece.getColor()) )
        {
            msg.setType(MessageType.info);
            msg.setText(SKIPAVAILABLE);
            baseBoard.setMultiCap(true);
            baseBoard.setCurrentStep(move.getEnd().clone());
            game.getBoard().setCurrentStep(move.getEnd().clone());

            game.setState(GameState.SKIP);
        }
        else
        {
            game.setState(GameState.PENDING);
        }

    }

    private boolean checkSimpleMovement(Move move, Piece piece, Message msg)
    {
        if (piece.getType() == Crown.SINGLE)
        {
            if (piece.getColor() == PieceColor.WHITE)
            {
                if (move.getStart().getRow() - 1 == move.getEnd().getRow() &&
                        Math.abs(move.getStart().getCell() - move.getEnd().getCell()) == 1) {
                    msg.setText(VALID);
                    msg.setType(MessageType.info);
                    return true;
                }
            }
            else if (piece.getColor() == PieceColor.RED)
            {
                if (move.getStart().getRow() + 1 == move.getEnd().getRow() &&
                        Math.abs(move.getStart().getCell() - move.getEnd().getCell()) == 1) {
                    msg.setText(VALID);
                    msg.setType(MessageType.info);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkSimpleCapturingMovement(Board board, Move move, Piece piece, Message msg)
    {
        int initRow = move.getStart().getRow();
        int initCell = move.getStart().getCell();

        int endRow = move.getEnd().getRow();
        int endCell = move.getEnd().getCell();

        int midCell;
        if (initCell > endCell) { midCell = initCell - 1; }
        else { midCell = initCell + 1; }

        int midRow;
        if (initRow > endRow) { midRow = initRow - 1; }
        else { midRow = initRow + 1; }

        //Fetch captured piece, if it exists.
        Piece midPiece = null;
        if (midCell > 0 && midRow > 0) {
            midPiece = board.getPiece(midRow, midCell);
        }

        if (piece.getType() == Crown.SINGLE)                                        //Verify we're moving a single piece
        {
            if (piece.getColor() == PieceColor.WHITE)                               //If we're moving white
            {
                if (initRow - 2 == endRow && Math.abs(initCell - endCell) == 2) {   //See if the actual movement is a capturing move

                    if (midPiece == null )                                          //If no piece was present, set error message
                    {
                        msg.setText(INVALIDCAPTURENOTEXIST);
                        msg.setType(MessageType.error);
                        return false;
                    }
                    else if (midPiece.getColor() == PieceColor.WHITE)               //If piece is the same color, set error message
                    {
                        msg.setText(INVALIDCAPTURECOLOR);
                        msg.setType(MessageType.error);
                        return false;
                    }
                    else                                                            //If capture was valid, set message
                    {
                        msg.setText(VALID);
                        msg.setType(MessageType.info);
                        board.capturePiece(midRow, midCell);
                        return true;
                    }
                }
            }
            else if (piece.getColor() == PieceColor.RED)                            //Same as above, but in the case that player is Red.
            {

                if (initRow + 2 == endRow && Math.abs(initCell - endCell) == 2) {
                    if (midPiece == null )
                    {
                        msg.setText(INVALIDCAPTURENOTEXIST);
                        msg.setType(MessageType.error);
                        return false;
                    }
                    else if (midPiece.getColor() == PieceColor.RED)
                    {
                        msg.setText(INVALIDCAPTURECOLOR);
                        msg.setType(MessageType.error);
                        return false;
                    }
                    else
                    {
                        msg.setText(VALID);
                        msg.setType(MessageType.info);
                        board.capturePiece(midRow, midCell);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkCrowningPiece(Move move, Piece piece)
    {
        if (piece.getColor() == PieceColor.WHITE && piece.getType() == Crown.SINGLE && move.getEnd().getRow() == 0)
        {
            piece.setType(Crown.KING);
            return true;
        }
        else if (piece.getColor() == PieceColor.RED && piece.getType() == Crown.SINGLE && move.getEnd().getRow() == 7)
        {
            piece.setType(Crown.KING);
            return true;
        }

        return false;
    }

    private boolean checkCrownMovement(Move move, Piece piece, Message msg)
    {
        if (piece.getType() == Crown.KING)
        {
            if (Math.abs(move.getStart().getRow() - move.getEnd().getRow()) == 1 &&
                    Math.abs(move.getStart().getCell() - move.getEnd().getCell()) == 1) {
                msg.setText(VALID);
                msg.setType(MessageType.info);
                return true;
            }
        }

        return false;
    }

    private boolean checkCrownCapturingMovement(Board board, Move move, Piece piece, Message msg)
    {
        int initRow = move.getStart().getRow();
        int initCell = move.getStart().getCell();
        int endRow = move.getEnd().getRow();
        int endCell = move.getEnd().getCell();

        int midCell;
        if (initCell > endCell) { midCell = initCell - 1; }
        else { midCell = initCell + 1; }

        int midRow;
        if (initRow > endRow) { midRow = initRow - 1; }
        else { midRow = initRow + 1; }

        Piece midPiece = null;
        if (midCell > 0 && midRow > 0) {
            midPiece = board.getPiece(midRow, midCell);
        }

        if (piece.getType() == Crown.KING)                                              //Verify we're moving a single piece
        {
            if (Math.abs(initRow - endRow) == 2 && Math.abs(initCell - endCell) == 2) { //See if the actual movement is a capturing move

                if (midPiece == null )                                                  //If no piece was present, set error message
                {
                    msg.setText(INVALIDCAPTURENOTEXIST);
                    msg.setType(MessageType.error);
                    return false;
                }
                else if (midPiece.getColor() == piece.getColor())                       //If piece is the same color, set error message
                {
                    msg.setText(INVALIDCAPTURECOLOR);
                    msg.setType(MessageType.error);
                    return false;
                }
                else                                                                    //If capture was valid, set message
                {
                    msg.setText(VALID);
                    msg.setType(MessageType.info);
                    board.capturePiece(midRow, midCell);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isCaptureAvailable(Board board, PieceColor playerColor) {
        boolean result = false;

        Iterator iteratorRow = board.iterator();

        while (iteratorRow.hasNext()) {

            // Get next row and its index
            Row row = (Row)iteratorRow.next();
            int rowIndex = row.getIndex();

            Iterator iteratorSpaceRow = row.iterator();

            while (iteratorSpaceRow.hasNext()) {

                // Get next cell and its index
                Space space = (Space)iteratorSpaceRow.next();
                int spaceIndex = space.getCellIdx();

                //Check availability
                result = board.checkCaptureAvailable(rowIndex, spaceIndex, playerColor);

                //Return results if a capture is found.
                if (result) { return  result; }
            }
        }

        return result;
    }




}
