package ch.ihl.runnest;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import ch.ihl.runnest.Model.Challenge;
import ch.ihl.runnest.Model.Message;

public class MessageTest {

    @Test
    public void defaultConstructorDoesNotThrowsException() {
        new Message("me", "you", "me", "you", Message.Type.TEXT, "Hello, world!");
        Assert.assertTrue(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalTo() {
        new Message(null, "you", "me", "you", Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyTo() {
        new Message("", "you", "me", "you", Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalFrom() {
        new Message("me", null, "me", "you", Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyFrom() {
        new Message("me", "", "me", "you", Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalSender() {
        new Message("me", "you", null, "you", Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptySender() {
        new Message("me", "you", "", "you", Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalAddressee() {
        new Message("me", "you", "me", null, Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyAddressee() {
        new Message("me", "you", "me", "", Message.Type.TEXT, "Hello, world!");
    }
    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalType() {
        new Message("me", "you", "me", "you", null, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithIllegalMessage() {
        new Message("me", "you", "me", "you", Message.Type.CHALLENGE_REQUEST, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultConstructorThrowsExceptionWithEmptyMessage() {
        new Message("me", "you", "me", "you", Message.Type.CHALLENGE_RESPONSE, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithTimeThrowsExceptionWithNullTime() {
        new Message("me", "you", "me", "you", Message.Type.MEMO, "Hello, world!", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantSendMessageToYourself() {
        new Message("me", "me", "me", "me", Message.Type.TEXT, "Hello, world!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorChallengeParamsThrowsExceptionWithNullChallengeType() {
        Date time = new Date();
        new Message("me", "you", "me", "you", Message.Type.TEXT, "msg", time, 0, 1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorScheduleMemoThrowsExceptionWithNullChallengeType() {
        Date time = new Date();
        new Message("me", "you", "me", "you", Message.Type.MEMO, "msg", time, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorScheduleMemoThrowsExceptionWithIncoherentMessageType() {
        Date time = new Date();
        new Message("me", "you", "me", "you", Message.Type.TEXT, "msg", time, Challenge.Type.DISTANCE);
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

        int expectedId = 200;
        message.setUid(expectedId);
        Assert.assertTrue(expectedId == message.getUid());
    }
}
