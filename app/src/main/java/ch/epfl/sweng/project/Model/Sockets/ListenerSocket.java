package ch.epfl.sweng.project.Model.Sockets;

import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class allows to keep listening for messages comming directly from another device
 *
 * @author Pablo Pfister
 */
public class ListenerSocket {
    private ServerSocket serverSocket = null;
    private String line;
    private DataInputStream input;

    /**
     * Prepares the Socket to listen on a specific port.
     *
     * @param port
     */
    public ListenerSocket(int port) {
        if (port <= 1024 && port > 65535) {
            throw new IllegalArgumentException();
        }

        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts listening.
     */
    public void listen() {
        if (serverSocket == null || input == null) {
            throw new NullPointerException();
        }

        try {
            while (true) {
                Socket socket = serverSocket.accept();
                input = new DataInputStream(socket.getInputStream());
                String message = input.readLine();
                Log.e("SERVER_LISTEN", message);
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Stops listening and closes the Socket.
     */
    public void close() {
        if (serverSocket == null || input == null) {
            throw new NullPointerException();
        }

        try {
            input.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}