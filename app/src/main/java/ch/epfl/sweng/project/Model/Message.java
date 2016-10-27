package ch.epfl.sweng.project.Model;

import java.util.Date;

/**
 * This class represents a message that can be sent to de server and retrieved from the server
 */
public class Message {
    private String from;
    private String to;
    private MessageType type;
    private String message;
    private Date time;

    /**
     * This enumeration represents all types of messages that can be sent to the server
     */
    public enum MessageType {
        TEXT, CHALLENGE_REQUEST, CHALLENGE_RESPONSE, MY_IP_IS
    }

    /**
     * The constructor of the Message
     *
     * @param from
     * @param to
     * @param type
     * @param message
     * @throws IllegalArgumentException
     */
    public Message(String from, String to, MessageType type, String message)
            throws IllegalArgumentException
    {
        if (from == null || from.equals("")
                || to == null || to.equals("")
                || type == null
                || message == null || message.equals("")) {
            throw new IllegalArgumentException("Invalid argument: parameters can't be null nor empty");
        }
        if (from.equals(to)) {
            throw new IllegalArgumentException("Invalid argument: can't send a message to yourself");
        }

        this.from = from;
        this.to = to;
        this.type = type;
        this.message = message;
        time = new Date();
    }

    /**
     * The constructor of the Message that allows to set the time
     *
     * @param from
     * @param to
     * @param type
     * @param message
     * @param sentAt
     * @throws IllegalArgumentException
     */
    public Message(String from, String to, MessageType type, String message, Date sentAt)
            throws IllegalArgumentException
    {
        this(from, to, type, message);
        if (sentAt == null) {
            throw new IllegalArgumentException("Invalid argument: parameters can't be null nor empty");
        }

        this.time = sentAt;
    }

    /**
     * Getter for the from attribute
     *
     * @return the from attribute of the message
     */
    public String getFrom() {
        return from;
    }

    /**
     * Getter for the to attribute
     *
     * @return the to attribute of the message
     */
    public String getTo() {
        return to;
    }

    /**
     * Getter for the type attribute
     *
     * @return the type of the message
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Getter for the message text
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter for the time
     *
     * @return the time the message was sent
     */
    public Date getTime() {
        return time;
    }

    /**
     * Computes a message unique id
     *
     * @return the message's unique id
     */
    public String getUid() {
        return from.hashCode() + "_" + time.hashCode();
    }
}
