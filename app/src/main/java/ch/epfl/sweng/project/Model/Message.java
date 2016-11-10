package ch.epfl.sweng.project.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * This class represents a message that can be sent to de server and retrieved from the server
 */
public class Message implements Serializable {
    private String from;
    private String to;
    private String sender;
    private String addressee;
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
     * @param sender
     * @param addressee
     * @param type
     * @param message
     * @throws IllegalArgumentException
     */

    public Message(String from, String to, String sender, String addressee, MessageType type, String message)
            throws IllegalArgumentException
    {
        if (from == null || from.equals("")
                || to == null || to.equals("")
                || sender == null || sender.equals("")
                || addressee == null || addressee.equals("")
                || type == null
                || message == null || message.equals("")) {
            throw new IllegalArgumentException("Invalid argument: parameters can't be null nor empty");
        }
        if (from.equals(to)) {
            throw new IllegalArgumentException("Invalid argument: can't send a message to yourself");
        }

        this.from = from;
        this.to = to;
        this.sender = sender;
        this.addressee = addressee;
        this.type = type;
        this.message = message;
        time = new Date();
    }

    /**
     * The constructor of the Message that allows to set the time
     *
     * @param from
     * @param to
     * @param sender
     * @param addressee
     * @param type
     * @param message
     * @param sentAt
     * @throws IllegalArgumentException
     */
    public Message(String from, String to, String sender, String addressee, MessageType type, String message, Date sentAt)
            throws IllegalArgumentException
    {
        this(from, to, sender, addressee, type, message);
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
     * Getter for the sender attribute
     *
     * @return the sender attribute of the message
     */
    public String getSender() {
        return sender;
    }

    /**
     * Getter for the addressee attribute
     *
     * @return the addressee attribute of the message
     */
    public String getAddressee() {
        return addressee;
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
