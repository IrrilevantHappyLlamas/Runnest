package ch.epfl.sweng.project;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import ch.epfl.sweng.project.Model.Message;

public class MessageTest {

    @Test
    public void defaultConstructorDontThrowsException() {
        Message testMessage = new Message("me", "you", Message.MessageType.TEXT, "Hello, world!");
        Assert.assertTrue(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalTo() {
        Message testMessage = new Message(null, "you", Message.MessageType.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyTo() {
        Message testMessage = new Message("", "you", Message.MessageType.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalFrom() {
        Message testMessage = new Message("me", null, Message.MessageType.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyFrom() {
        Message testMessage = new Message("me", "", Message.MessageType.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalType() {
        Message testMessage = new Message("me", "you", null, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalMessage() {
        Message testMessage = new Message("me", "you", null, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyMessage() {
        Message testMessage = new Message("me", "you", Message.MessageType.TEXT, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithTimeThrowsExceptionWithNullTime() {
        Message testMessage = new Message("me", "you", Message.MessageType.TEXT, "Hello, world!", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantSendMessageToYourself() {
        Message _ = new Message("me", "me", Message.MessageType.TEXT, "Hello, world!");
    }

    @Test
    public void gettersReturnSettedValue() {
        String from = "me";
        String to = "you";
        Message.MessageType type = Message.MessageType.TEXT;
        String message = "Hello, world!";
        Date time = new Date();
        Message testMessage = new Message(from, to, type, message, time);

        Assert.assertTrue(from.equals(testMessage.getFrom()));
        Assert.assertTrue(to.equals(testMessage.getTo()));
        Assert.assertTrue(type.equals(testMessage.getType()));
        Assert.assertTrue(message.equals(testMessage.getMessage()));
        Assert.assertTrue(time.equals(testMessage.getTime()));
    }
}
