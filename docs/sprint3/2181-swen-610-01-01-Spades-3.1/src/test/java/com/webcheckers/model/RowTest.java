package com.webcheckers.model;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Iterator;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The unit test suite for the {@link Row} component.
 *
 */
public class RowTest {

    private int num = 2;

    //Check if object is null
    @Test
    public void test_rowCreation() throws Exception{
        Row rw = new Row(num);
        assertNotNull(rw);
    }

    //check and validate index
    @Test
    public void test_indexAssignment() throws Exception{

        Row rw = new Row(num);
        assertEquals(2,rw.getIndex());

    }

    //check and validate iterator
    @Test
    public void test_iterator() throws Exception{
        Row rw = new Row(num);
        Iterator IteratorRows = rw.iterator();
        assertNotNull(IteratorRows);
        assertTrue(IteratorRows instanceof Iterator);
    }


}
