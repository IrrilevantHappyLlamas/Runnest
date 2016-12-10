package ch.epfl.sweng.project;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.Message;

public class MessageTest {

    @Test
    public void defaultConstructorDontThrowsException() {
        Message testMessage = new Message("me", "you", "me", "you", Message.Type.TEXT, "Hello, world!");
        Assert.assertTrue(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalTo() {
        Message testMessage = new Message(null, "you", "me", "you", Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyTo() {
        Message testMessage = new Message("", "you", "me", "you", Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalFrom() {
        Message testMessage = new Message("me", null, "me", "you", Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyFrom() {
        Message testMessage = new Message("me", "", "me", "you", Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalSender() {
        Message testMessage = new Message("me", "you", null, "you", Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptySender() {
        Message testMessage = new Message("me", "you", "", "you", Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalAddressee() {
        Message testMessage = new Message("me", "you", "me", null, Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyAddresseee() {
        Message testMessage = new Message("me", "you", "me", "", Message.Type.TEXT, "Hello, world!");
    }
    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalType() {
        Message testMessage = new Message("me", "you", "me", "you", null, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalMessage() {
        Message testMessage = new Message("me", "you", "me", "you", Message.Type.CHALLENGE_REQUEST, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyMessage() {
        Message testMessage = new Message("me", "you", "me", "you", Message.Type.CHALLENGE_RESPONSE, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithTimeThrowsExceptionWithNullTime() {
        Message testMessage = new Message("me", "you", "me", "you", Message.Type.MEMO, "Hello, world!", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantSendMessageToYourself() {
        Message testMessage = new Message("me", "me", "me", "me", Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorChallengeParamsThrowsExceptionWithNullChallengeType() {
        Date time = new Date();
        Message testMessage = new Message("me", "you", "me", "you", Message.Type.TEXT, "msg", time, 0, 1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorScheduleMemoThrowsExceptionWithNullChallengeType() {
        Date time = new Date();
        Message testMessage = new Message("me", "you", "me", "you", Message.Type.TEXT, "msg", time, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorScheduleMemoThrowsExceptionWithIncoherentMessageType() {
        Date time = new Date();
        Message testMessage = new Message("me", "you", "me", "you", Message.Type.TEXT, "msg", time, Challenge.Type.DISTANCE);
    }

    @Test
    public void getters() {
        String from = "me";
        String to = "you";
        String sender = "me";
        String addressee = "you";
        Message.Type type = Message.Type.TEXT;
        String msgTxt = "Hello, world!";
        Date time = new Date();
        int firstValue = 0;
        int secondValue = 1;
        Challenge.Type challengeType = Challenge.Type.TIME;
        Message message = new Message(from, to, sender, addressee, type, msgTxt, time, firstValue, secondValue, challengeType);

        Assert.assertTrue(from.equals(message.getFrom()));
        Assert.assertTrue(to.equals(message.getTo()));
        Assert.assertTrue(sender.equals(message.getSender()));
        Assert.assertTrue(addressee.equals(message.getAddressee()));
        Assert.assertTrue(type.equals(message.getType()));
        Assert.assertTrue(msgTxt.equals(message.getMessage()));
        Assert.assertTrue(time.equals(message.getTime()));
        Assert.assertEquals(firstValue, message.getFirstValue());
        Assert.assertEquals(secondValue, message.getSecondValue());
        Assert.assertEquals(challengeType, message.getChallengeType());

        String expectedId = from.hashCode() + "_" + time.hashCode();
        Assert.assertTrue(expectedId.equals(message.getUid()));
    }
}
