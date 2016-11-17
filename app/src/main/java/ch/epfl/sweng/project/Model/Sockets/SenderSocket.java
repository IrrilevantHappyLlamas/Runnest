package ch.epfl.sweng.project.Model.Sockets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This class allows to send messages directly to an other device
 *
 * @author Pablo Pfister
 */
public class SenderSocket {

    private Socket socket = null;
    private DataOutputStream output = null;

    /**
     * The constructor that takes ip and port number of the destination host and prepares the Socket
     *
     * @param hostName
     * @param port
     */
    public SenderSocket(String hostName, int port) {
        if (port <= 1024 && port > 65535) {
            throw new IllegalArgumentException();
        }

        try {
            socket = new Socket(hostName, port);
            output = new DataOutputStream(socket.getOutputStream());
        } catch  (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the other device
     *
     * @param message
     */
    public void send(String message) {
        if (output == null) {
            throw new NullPointerException();
        }

        try {
            output.writeBytes(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the connection. Needs to be called when the communication is over.
     */
    public void close() {
        if (socket == null || output == null) {
            throw new NullPointerException();
        }

        try {
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}