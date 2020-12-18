package com.webcheckers.model;

public class Position implements Cloneable {
    private int row;
    private int cell;

    public Position(){
    }

    public Position(int row, int cell) {
        this.row = row;
        this.cell = cell;
    }

    public int getRow() {
        return row;
    }


    public int getCell() {
        return cell;
    }

    @Override
    public Position clone() {
        Position newPos = new Position(this.row, this.cell);

        return newPos;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != Position.class)
        {
            return false;
        }

        if (this.getRow() == ((Position)obj).getRow() && this.getCell() == ((Position)obj).getCell())
        {
            return true;
        }

        return false;
    }
}
