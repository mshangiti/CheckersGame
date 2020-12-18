package com.webcheckers.model;

import java.util.ArrayList;
import java.util.Iterator;

public class Row implements Iterable<Space> {

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

    @Override
    public synchronized Iterator<Space> iterator() {
        return spaces.iterator();
    }
}
