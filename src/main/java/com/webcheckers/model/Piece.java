package com.webcheckers.model;

public class Piece implements Cloneable{

    private Crown type;
    private PieceColor color;

    /**
     * Constructors
     */
    public Piece () {}

    public Piece(PieceColor color)
    {
        this.color = color;
        this.type = Crown.SINGLE;
    }

    /**
     * Getters
     */
    public Crown getType() {
        return type;
    }

    public void setType(Crown type) {
        this.type = type;
    }

    public PieceColor getColor() {
        return color;
    }

    @Override
    public Piece clone()
    {
        Piece newPiece = new Piece();
        newPiece.color = this.color;
        newPiece.type = this.type;

        return newPiece;
    }
}
