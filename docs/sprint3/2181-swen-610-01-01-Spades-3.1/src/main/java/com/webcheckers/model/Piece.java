package com.webcheckers.model;

public class Piece {

    private Crown type;
    private PieceColor color;

    /**
     * Constructor
     * @param color
     */
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

    public PieceColor getColor() {
        return color;
    }
}
