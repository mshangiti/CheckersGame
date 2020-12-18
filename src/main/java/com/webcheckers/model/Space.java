package com.webcheckers.model;

public class Space implements Cloneable {

    private int cellIdx;
    private boolean isValid;
    private Piece piece;

    /**
     * Constructor
     */
    public Space() {}

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

    public void setPiece(Piece piece) {
        if (piece != null) {
            this.piece = piece;
            this.isValid = false;
        }
        else {
            this.piece = null;
            this.isValid = true;
        }
    }

    public  void removePiece() {
        this.piece = null;
        this.isValid = true;
    }

    @Override
    public Space clone() {
        Space newspace = new Space();

        if (this.piece == null)
            newspace.setPiece(null);
        else
            newspace.setPiece(this.piece.clone());

        newspace.setIsValid(this.isValid);
        newspace.setCellIdx(this.cellIdx);

        return newspace;
    }

    private void setIsValid(boolean validity)
    {
        this.isValid = validity;
    }

    private void setCellIdx(int id)
    {
        this.cellIdx = id;
    }

}
