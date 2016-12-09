package ch.epfl.sweng.project;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import ch.epfl.sweng.project.Model.Message;

public class MessageTest {

    @Test
    public void defaultConstructorDontThrowsException() {
        Message testMessage = new Message("me", "you", "me", "you", Message.MessageType.TEXT, "Hello, world!");
        Assert.assertTrue(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalTo() {
        Message testMessage = new Message(null, "you", "me", "you", Message.MessageType.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyTo() {
        Message testMessage = new Message("", "you", "me", "you", Message.MessageType.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalFrom() {
        Message testMessage = new Message("me", null, "me", "you", Message.MessageType.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyFrom() {
        Message testMessage = new Message("me", "", "me", "you", Message.MessageType.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalSender() {
        Message testMessage = new Message("me", "you", null, "you", Message.MessageType.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptySender() {
        Message testMessage = new Message("me", "you", "", "you", Message.MessageType.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalAddressee() {
        Message testMessage = new Message("me", "you", "me", null, Message.MessageType.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyAddresseee() {
        Message testMessage = new Message("me", "you", "me", "", Message.MessageType.TEXT, "Hello, world!");
    }
    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalType() {
        Message testMessage = new Message("me", "you", "me", "you", null, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalMessage() {
        Message testMessage = new Message("me", "you", "me", "you", Message.MessageType.CHALLENGE_REQUEST, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyMessage() {
        Message testMessage = new Message("me", "you", "me", "you", Message.MessageType.CHALLENGE_RESPONSE, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithTimeThrowsExceptionWithNullTime() {
        Message testMessage = new Message("me", "you", "me", "you", Message.MessageType.MEMO, "Hello, world!", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantSendMessageToYourself() {
        Message testMessage = new Message("me", "me", "me", "me", Message.MessageType.TEXT, "Hello, world!");
    }

    @Test
    public void gettersReturnSettedValue() {
        String from = "me";
        String to = "you";
        String sender = "me";
        String addressee = "you";
        Message.MessageType type = Message.MessageType.TEXT;
        String message = "Hello, world!";
        Date time = new Date();
        Message testMessage = new Message(from, to, sender, addressee, type, message, time);

        Assert.assertTrue(from.equals(testMessage.getFrom()));
        Assert.assertTrue(to.equals(testMessage.getTo()));
        Assert.assertTrue(type.equals(testMessage.getType()));
        Assert.assertTrue(message.equals(testMessage.getMessage()));
        Assert.assertTrue(time.equals(testMessage.getTime()));

        String expectedId = from.hashCode() + "_" + time.hashCode();
        Assert.assertTrue(expectedId.equals(testMessage.getUid()));
    }
}
