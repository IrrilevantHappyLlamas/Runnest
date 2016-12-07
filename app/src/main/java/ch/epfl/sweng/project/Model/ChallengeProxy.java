package ch.epfl.sweng.project.Model;

public interface ChallengeProxy {

    interface Handler{
        void hasNewData(CheckPoint checkPoint);
        void isReady();
        void isFinished();
        void hasAborted();
        void opponentInRoom();
    }

    void putData(CheckPoint checkPoint);

    void startChallenge();

    void imReady();

    void imFinished();

    void abortChallenge();

    void deleteChallenge();
}
