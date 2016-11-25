package ch.epfl.sweng.project.Model;

import java.io.Serializable;
import java.util.Date;

import ch.epfl.sweng.project.Activities.ChallengeActivity;
import ch.epfl.sweng.project.Fragments.ChallengeDialogFragment;

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
    private int firstValue;
    private int secondValue;
    private ChallengeActivity.ChallengeType challengeType;

    /**
     * This enumeration represents all types of messages that can be sent to the server
     */
    public enum MessageType {
        TEXT, CHALLENGE_REQUEST, CHALLENGE_DISTANCE, CHALLENGE_TIME, CHALLENGE_RESPONSE, MY_IP_IS
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
     * The constructor of the Message for challenge requests
     *
     * @param from
     * @param to
     * @param sender
     * @param addressee
     * @param type
     * @param message
     * @param challengeType
     * @throws IllegalArgumentException
     */
    public Message(String from,
                   String to,
                   String sender,
                   String addressee,
                   MessageType type,
                   String message,
                   Date sentAt,
                   int firstValue,
                   int secondValue,
                   ChallengeActivity.ChallengeType challengeType)
            throws IllegalArgumentException
    {
        this(from, to, sender, addressee, type, message);
        if (sentAt == null) {
            throw new IllegalArgumentException("Invalid argument: parameters can't be null nor empty");
        }

        this.time = sentAt;
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.challengeType = challengeType;
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
     * Getter for the first value
     *
     * @return the first value of the goal
     */
    public int getFirstValue() {
        return firstValue;
    }

    /**
     * Getter for the second value
     *
     * @return the second value of the goal
     */
    public int getSecondValue() {
        return secondValue;
    }

    /**
     * Getter for the challenge type
     *
     * @return the type of the challenge
     */
    public ChallengeActivity.ChallengeType getChallengeType() {
        return challengeType;
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
