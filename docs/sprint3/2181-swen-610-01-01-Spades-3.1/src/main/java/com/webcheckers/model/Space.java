package com.webcheckers.model;

public class Space {

    private int cellIdx;
    private boolean isValid;
    private Piece piece;

    /**
     * Constructor
     */
    public Space(int row, int col) {
        //Set column id
        this.cellIdx = col;

        //Determine if space holds a piece and create if it does
        if ((row + col) % 2 == 1)
        {
            if (row < 3)
            {
                this.piece = new Piece(PieceColor.RED);
                this.isValid = false;
            }
            else if (row > 4)
            {
                this.piece = new Piece(PieceColor.WHITE);
                this.isValid = false;
            }
            else
            {
                this.isValid = true;
            }
        }
        else
        {
            this.isValid = false;
        }
    }

    /**
     * Getters
     */
    public int getCellIdx() {
        return cellIdx;
    }

    public boolean isValid() {
        return isValid;
    }

    public Piece getPiece() {
        return piece;
    }


}
