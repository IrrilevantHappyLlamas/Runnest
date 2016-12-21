package ch.epfl.sweng.project.Model;

/**
 * Interface for proxies that intend to put themselves as serves in the eyes of the local challenge system.
 * This interface provides methods to input and retrieve data to and from the opponent. The client does not need to know
 * the underlying communication system to interact with the proxy.
 */
public interface ChallengeProxy {

    /**
     * Interface whose goal is to be used as a lambda function, to provide the proxy the functions it needs to handle
     * opponent data.
     */
    interface Handler{
        /**
         * The proxy will call it when it has new data coming from the opponent. The implementation should handle the
         * checkpoints in the way the client needs.
         *
         * @param checkPoint    <code>CheckPoint</code> received from the opponent.
         */
        void hasNewData(CheckPoint checkPoint);

        /**
         * Will be called when the proxy receives notification that the opponent is ready.
         */
        void isReady();

        /**
         * Will be called when the proxy receives notification that the opponent has finished.
         */
        void isFinished();

        /**
         * Will be called when the proxy receives notification that the opponent has left the challenge.
         */
        void hasLeft();

        /**
         * Will be called when the proxy receives notification that the opponent has joined the challenge room.
         */
        void opponentInRoom();
    }

    /**
     * Used by the client to input a checkpoint from the local user into the challenge communication system. The client
     * can assume that the proxy will take the right measures to ensure that the data arrives to the opponent.
     *
     * @param checkPoint    Data the client intends to sendMessage to his opponent.
     */
    void putData(CheckPoint checkPoint);

    /**
     * Method which has to be invoked when the <code>checkPoint</code> can begin to be sent and received.
     * Should be called only after the "ready handshake" has been successfully completed.
     */
    void startChallenge();

    /**
     * Signal to the opponent that the local user is ready to start the challenge at any moment. It's part of a
     * "ready handshake" that ensures both parts are ready to sendMessage and receive data when the challenge starts.
     */
    void imReady();

    /**
     * Signal to the opponent that the local user has completed his challenge goal and won't sendMessage any additional
     * <code>CheckPoint</code>.
     */
    void imFinished();

    /**
     * Signal to the opponent that the local user has left the challenge and won't sendMessage any additional data.
     */
    void abortChallenge();

    /**
     * Calling this method will cause the proxy to deleteMessage challenge data from the underlying communication system.
     * The method should be called only when no user expects any data from the challenge.
     */
    void deleteChallenge();
}
