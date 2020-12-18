package com.webcheckers.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {

    static String TEST_TEXT = "Lorem Ipsum";
    static String TEST_TEXT_2 = "Ipsum Lorem";
    static MessageType MSG_TYPE_ERROR = MessageType.error;
    static MessageType MSG_TYPE_INFO = MessageType.info;

    @Test
    public void test_Constructor()
    {
        Message CuT = new Message(TEST_TEXT, MSG_TYPE_INFO);

        assertEquals(TEST_TEXT, CuT.getText());
        assertEquals(MSG_TYPE_INFO, CuT.getType());
    }

    @Test
    public void test_getText() {
        Message CuT = new Message(TEST_TEXT, MSG_TYPE_INFO);
        assertEquals(TEST_TEXT, CuT.getText());
    }

    @Test
    public void test_setText() {
        Message CuT = new Message(TEST_TEXT, MSG_TYPE_INFO);

        //Verify text is set
        assertEquals(TEST_TEXT, CuT.getText());

        //Change text
        CuT.setText(TEST_TEXT_2);

        assertEquals(TEST_TEXT_2, CuT.getText());
    }

    @Test
    public void test_getType() {
        Message CuT = new Message(TEST_TEXT, MSG_TYPE_INFO);

        assertEquals(MSG_TYPE_INFO, CuT.getType());
    }

    @Test
    public void test_setType() {
        Message CuT = new Message(TEST_TEXT, MSG_TYPE_INFO);

        assertEquals(MSG_TYPE_INFO, CuT.getType());

        //Change type
        CuT.setType(MSG_TYPE_ERROR);

        assertEquals(MSG_TYPE_ERROR, CuT.getType());
    }
}