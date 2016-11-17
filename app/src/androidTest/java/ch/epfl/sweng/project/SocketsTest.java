package ch.epfl.sweng.project;

import junit.framework.Assert;

import org.junit.Test;

import ch.epfl.sweng.project.Model.Sockets.ListenerSocket;
import ch.epfl.sweng.project.Model.Sockets.SenderSocket;

public class SocketsTest {
    @Test
    public void canInstantiateSender() {
        SenderSocket sender = new SenderSocket("123.127.19.12", 9999);
        Assert.assertTrue(true);
    }

    @Test
    public void canInstantiateListener() {
        ListenerSocket listener = new ListenerSocket(9999);
        Assert.assertTrue(true);
    }

    @Test
    public void canSendMessage() {
        SenderSocket sender = new SenderSocket("123.127.19.12", 9999);
        sender.send("test");
        sender.close();
        Assert.assertTrue(true);
    }

    @Test
    public void canListen() {
        ListenerSocket listener = new ListenerSocket(9999);
        listener.listen();
        listener.close();
        Assert.assertTrue(true);
    }
}
