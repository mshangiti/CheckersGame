package com.webcheckers.model;

import java.util.ArrayList;
import java.util.Iterator;

public class Row implements Iterable<Space>, Cloneable {

    private int index;
    private ArrayList<Space> spaces;

    /**
     * Constructor
     * @param i index
     */
    public Row(int i) {

        this.spaces = new ArrayList<>();

        this.index = i;

        for (int j = 0; j < 8; j++)
        {
            this.spaces.add(new Space(i, j));
        }
    }

    /**
     * Getters
     */
    public int getIndex() {
        return index;
    }

    public ArrayList<Space> getSpaces() {
        return spaces;
    }

    @Override
    public synchronized Iterator<Space> iterator() {
        return spaces.iterator();
    }

    @Override
    public Row clone() {
        Row newrow = new Row(this.index);
        newrow.spaces = new ArrayList<>();

        for (Space oldSpace : this.spaces)
        {
            newrow.spaces.add(oldSpace.clone());
        }
        return newrow;
    }

    private void setSpaces(ArrayList<Space> spaces)
    {
        this.spaces = spaces;
    }
}
