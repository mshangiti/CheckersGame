package com.webcheckers.model;

import java.util.Iterator;
import java.util.ArrayList;

public class Board implements Iterable<Row>, Cloneable {
    public static final int BOARD_SIZE = 8;

    private ArrayList<Row> rows;

    private boolean multiCap;
    private Position currentStep;


    public Board() {
        rows = new ArrayList<>();
        this.multiCap = false;

        for (int i = 0; i < BOARD_SIZE; i++) {
            rows.add(new Row(i));
        }
    }

    public int getBoardSize() {
        return rows.size();
    }

    public void setRows (ArrayList<Row> rows) {
        this.rows = (ArrayList<Row>)rows.clone();
    }

    public Piece getPiece(int row, int space) {
        return rows.get(row).getSpaces().get(space).getPiece();
    }

    public boolean isMultiCap() {
        return multiCap;
    }

    public void setMultiCap(boolean multiCap) {
        this.multiCap = multiCap;
    }

//    public Board getSkipBoard() {
//        return skipBoard;
//    }
//
//    public void setSkipBoard(Board skipBoard) {
//        if (skipBoard != null)
//        {
//            this.skipBoard = skipBoard.clone();
//        }
//        else
//        {
//            this.skipBoard = null;
//        }
//
//    }

    public Position getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(Position currentStep) {
        this.currentStep = currentStep;
    }

    public void updateBoard(Move move) {
        int startRow = move.getStart().getRow();
        int startCell = move.getStart().getCell();
        int endRow = move.getEnd().getRow();
        int endCell = move.getEnd().getCell();

        Piece movingPiece = getPiece(startRow, startCell);

        //Move piece to new space
        rows.get(endRow).getSpaces().get(endCell).setPiece(movingPiece);

        //Delete pieced from previous space
        rows.get(startRow).getSpaces().get(startCell).removePiece();

    }

    public boolean checkMoveAvailable(PieceColor color)
    {
        //find all player pieces and check their ability to move
        for (int i=0;i<rows.size();i++){
            Row currentRow=rows.get(i);
            for(int j=0;j<currentRow.getSpaces().size();j++){
                Space currentSpace = currentRow.getSpaces().get(j);
                //get current space
                Piece currentPiece = currentSpace.getPiece();
                //check current space has piece and that piece belong to the player with given color
                if(currentPiece != null && currentPiece.getColor()==color){
                    //check if capture is possible
                    if(checkCaptureAvailable(currentRow.getIndex(),currentSpace.getCellIdx(),color)){
                        //System.out.println("Single " +color+" Piece in ("+i+","+j+") can capture");
                        return true;
                    }
                    //check piece movements
                    //check upward movement, if cells are valid, then move is possible
                    if (currentPiece.getType() == Crown.SINGLE && currentPiece.getColor() == PieceColor.WHITE
                    && isUpMovementPossible(i,j))
                    {
                        //System.out.println("Single " +color+" Piece in ("+i+","+j+") can move up");
                        return true;
                    }
                    if (currentPiece.getType() == Crown.SINGLE && currentPiece.getColor() == PieceColor.RED
                    && isDownMovementPossible(i,j))
                    {
                        //check downward movement, if cells are valid, then move is possible
                        //System.out.println("Single " +color+" Piece in ("+i+","+j+") can move down");
                        return true;
                    }
                    if (currentPiece.getType() == Crown.KING && (isUpMovementPossible(i,j) || isDownMovementPossible(i,j)))
                    {
                        //System.out.println("King " +color+" Piece in ("+i+","+j+") can move up or down");
                        //check all directions
                        return true;
                    }
                }//if statement check for current piece end
            }//second for end
        }//first for end

        return false;
    }//method end

    private boolean isUpMovementPossible(int i, int j){
        //if we reached the top of the board, then there's no up movement
        if(i==0){return false;}
        //check movement
        if(j==0){
            //if j == 0, test only up right
            if(rows.get(i-1).getSpaces().get(j+1).isValid()){ return true;}
        }else if(j==7){
            //if j==7, test only up left
            if(rows.get(i-1).getSpaces().get(j-1).isValid()){ return true;}
        }else{
            //else test both sides
            if(rows.get(i-1).getSpaces().get(j-1).isValid()//upleft cell
                    || rows.get(i-1).getSpaces().get(j+1).isValid()//upright cell
            ){ return true;}
        }//end of else
        return false;
    }

    private boolean isDownMovementPossible(int i, int j){
        //if we reached the bottom of the board, then there's no down movement
        if(i==7){return false;}

        //check movement
        if(j==0){
            //if j == 0, test only down right
            if(rows.get(i+1).getSpaces().get(j+1).isValid()){ return true;}
        }else if(j==7){
            //if j==7, test only down left
            if(rows.get(i+1).getSpaces().get(j-1).isValid()){ return true;}
        }else{
            //else test both sides
            if(rows.get(i+1).getSpaces().get(j-1).isValid()//downleft cell
                    || rows.get(i+1).getSpaces().get(j+1).isValid()//downright cell
            ){ return true;}
        }//end of else
        return false;
    }


    public void capturePiece(int row, int space)
    {
        rows.get(row).getSpaces().get(space).removePiece();
    }

    public boolean checkCaptureAvailable(int row, int space, PieceColor color)
    {
        Piece piece = getPiece(row, space);

        //Return false if space is either empty or occupied by an opponent piece
        if  (piece == null) { return false; }
        else if (piece.getColor() != color) { return false; }

        boolean result = false;

        if (piece.getType() == Crown.SINGLE && piece.getColor() == PieceColor.WHITE)
        {
            result = checkSingleUpwardCapture(piece, row, space);
        }
        else if (piece.getType() == Crown.SINGLE && piece.getColor() == PieceColor.RED)
        {
            result = checkSingleDownwardCapture(piece, row, space);
        }
        else if (piece.getType() == Crown.KING)
        {
            result = checkCrownCapture(piece, row, space);
        }

        return result;
    }

    public boolean checkBoardStillHasPiecesWithColor(PieceColor color){
        for(Row row:rows){
            for(Space space:row.getSpaces()){
                if(space.getPiece() != null && space.getPiece().getColor()==color){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkSingleUpwardCapture(Piece piece, int row, int space)
    {
        Position diagonalRight = new Position(row - 2, space + 2);
        Position diagonalOppRight = new Position(row - 1, space + 1);

        Position diagonalLeft = new Position(row - 2, space - 2);
        Position diagonalOppLeft = new Position(row - 1, space - 1);

        if (diagonalOppLeft.getCell() > 0 && diagonalOppLeft.getRow() > 0)
        {
            int oppRow = diagonalOppLeft.getRow();  int oppCell = diagonalOppLeft.getCell();
            int endRow = diagonalLeft.getRow();     int endCell = diagonalLeft.getCell();

            if (getPiece(oppRow, oppCell) != null && getPiece(endRow, endCell) == null)
            {
                if (getPiece(oppRow, oppCell).getColor() != piece.getColor())
                {
                    return true;
                }
            }
        }

        if (diagonalOppRight.getCell() < 7 && diagonalOppRight.getRow() > 0)
        {
            int oppRow = diagonalOppRight.getRow();  int oppCell = diagonalOppRight.getCell();
            int endRow = diagonalRight.getRow();     int endCell = diagonalRight.getCell();

            if (getPiece(oppRow, oppCell) != null && getPiece(endRow, endCell) == null)
            {
                if (getPiece(oppRow, oppCell).getColor() != piece.getColor())
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkSingleDownwardCapture(Piece piece, int row, int space)
    {
        Position diagonalRight = new Position(row + 2, space + 2);
        Position diagonalOppRight = new Position(row + 1, space + 1);

        Position diagonalLeft = new Position(row + 2, space - 2);
        Position diagonalOppLeft = new Position(row + 1, space - 1);

        if (diagonalOppLeft.getCell() > 0 && diagonalOppLeft.getRow() < 7)
        {
            int oppRow = diagonalOppLeft.getRow();  int oppCell = diagonalOppLeft.getCell();
            int endRow = diagonalLeft.getRow();     int endCell = diagonalLeft.getCell();

            if (getPiece(oppRow, oppCell) != null && getPiece(endRow, endCell) == null)
            {
                if (getPiece(oppRow, oppCell).getColor() != piece.getColor())
                {
                    return true;
                }
            }
        }

        if (diagonalOppRight.getCell() < 7 && diagonalOppRight.getRow() < 7)
        {
            int oppRow = diagonalOppRight.getRow();  int oppCell = diagonalOppRight.getCell();
            int endRow = diagonalRight.getRow();     int endCell = diagonalRight.getCell();

            if (getPiece(oppRow, oppCell) != null && getPiece(endRow, endCell) == null)
            {
                if (getPiece(oppRow, oppCell).getColor() != piece.getColor())
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkCrownCapture(Piece piece, int row, int space)
    {
        boolean result = false;

        result = checkSingleUpwardCapture(piece, row, space);

        if (!result)
        {
            result = checkSingleDownwardCapture(piece, row, space);
        }

        return result;
    }

    @Override
    public synchronized Iterator<Row> iterator() {
        return rows.iterator();
    }

    @Override
    public Board clone() {

        Board newBoard = new Board();
        newBoard.rows = new ArrayList<>();
        newBoard.multiCap = this.multiCap;

        for (Row oldRow : this.rows)
        {
            newBoard.rows.add(oldRow.clone());
        }

        return newBoard;
    }
}