package ch.epfl.sweng.project.Model;

public interface ChallengeProxy {

    interface Handler{
        void OnNewDataHandler(CheckPoint checkPoint);
        void isReadyHandler();
        void isFinished();
        void hasAborted();
    }

    void putData(CheckPoint checkPoint);

    void startChallenge();

    void deleteChallenge();

    void imReady();

    void imFinished();

    void abortChallenge();
}
