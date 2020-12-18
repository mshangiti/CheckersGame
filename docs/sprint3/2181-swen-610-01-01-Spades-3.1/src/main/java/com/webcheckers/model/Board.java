package com.webcheckers.model;

import java.util.Iterator;
import java.util.ArrayList;

public class Board implements Iterable<Row> {

    private ArrayList<Row> rows;
    public static final int BOARD_SIZE = 8;
    public Board()
    {
        rows = new ArrayList<>();

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            rows.add( new Row(i) );
        }
    }

    public int getBoardSize(){
        return rows.size();
    }

    @Override
    public synchronized Iterator<Row> iterator() {
        return rows.iterator();
    }
}
