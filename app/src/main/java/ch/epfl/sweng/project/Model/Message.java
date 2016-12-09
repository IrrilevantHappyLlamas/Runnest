package ch.epfl.sweng.project.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * This class represents a message that can be sent to the server and retrieved by a user
 */
public class Message implements Serializable {
    private String from;
    private String to;
    private String sender;
    private String addressee;
    private Type type;
    private String message;
    private Date time;
    private int firstValue;
    private int secondValue;
    private Challenge.Type challengeType;

    /**
     * This enumeration represents all types of messages that can be sent to the server
     */
    public enum Type {
        TEXT, CHALLENGE_REQUEST, CHALLENGE_RESPONSE, SCHEDULE_REQUEST, MEMO
    }

    /**
     * The constructor of the Message
     *
     * @param from the email (in firebase format) of the sender
     * @param to the email (in firebase format) of the recipient
     * @param sender the name of the sender
     * @param addressee the name of the recipient
     * @param type the type of the message
     * @param message the message to be transmitted
     * @throws IllegalArgumentException
     */
    public Message(String from, String to, String sender, String addressee, Type type, String message)
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

        firstValue = 0;
        secondValue = 0;
        challengeType = null;
    }

    /**
     * The constructor of the Message that allows to set the time
     *
     * @param from the email (in firebase format) of the sender
     * @param to the email (in firebase format) of the recipient
     * @param sender the name of the sender
     * @param addressee the name of the recipient
     * @param type the type of the message
     * @param message the message to be transmitted
     * @param sentAt the date the message was sent
     * @throws IllegalArgumentException
     */
    public Message(String from, String to, String sender, String addressee, Type type, String message, Date sentAt)
            throws IllegalArgumentException
    {
        this(from, to, sender, addressee, type, message);
        if (sentAt == null) {
            throw new IllegalArgumentException("Invalid argument: parameters can't be null nor empty");
        }

        this.time = sentAt;
    }

    /**
     * The constructor of the Message that allows to set challenge request parameters
     *
     * @param from the email (in firebase format) of the sender
     * @param to the email (in firebase format) of the recipient
     * @param sender the name of the sender
     * @param addressee the name of the recipient
     * @param type the type of the message
     * @param message the message to be transmitted
     * @param sentAt the date the message was sent
     * @param firstValue first value of the picker for the challenge duration (hours or km)
     * @param secondValue second value of the picker for the challenge duration (minutes or m)
     * @param challengeType the type of the challenge: based on time or distance
     * @throws IllegalArgumentException
     */
    public Message(String from,
                   String to,
                   String sender,
                   String addressee,
                   Type type,
                   String message,
                   Date sentAt,
                   int firstValue,
                   int secondValue,
                   Challenge.Type challengeType)
            throws IllegalArgumentException
    {
        this(from, to, sender, addressee, type, message, sentAt);
        if (type != Type.CHALLENGE_REQUEST) {
            throw new IllegalArgumentException("This constructor can be used only for challenge requests");
        }
        if (challengeType == null) {
            throw new IllegalArgumentException("Invalid argument: parameters can't be null nor empty");
        }

        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.challengeType = challengeType;
    }

    /**
     * The constructor of the Message to schedule and memo
     *
     * @param from the email (in firebase format) of the sender
     * @param to the email (in firebase format) of the recipient
     * @param sender the name of the sender
     * @param addressee the name of the recipient
     * @param type the type of the message
     * @param message the message to be transmitted
     * @param scheduledDate the date the challenge has been scheduled
     * @param challengeType the type of the challenge: based on time or distance
     * @throws IllegalArgumentException
     */
    public Message(String from,
                   String to,
                   String sender,
                   String addressee,
                   Type type,
                   String message,
                   Date scheduledDate,
                   Challenge.Type challengeType)
            throws IllegalArgumentException
    {
        this(from, to, sender, addressee, type, message, scheduledDate);
        if (type != Type.SCHEDULE_REQUEST && type != Type.MEMO) {
            throw new IllegalArgumentException("This constructor can be used only for schedules and memos");
        }
        if (challengeType == null) {
            throw new IllegalArgumentException("Invalid argument: parameters can't be null nor empty");
        }

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
    public Type getType() {
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
    public Challenge.Type getChallengeType() {
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
