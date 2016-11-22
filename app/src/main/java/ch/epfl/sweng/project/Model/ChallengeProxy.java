package ch.epfl.sweng.project.Model;

public interface ChallengeProxy {

    interface Handler{
        void OnNewDataHandler(CheckPoint checkPoint);
        void isReadyHandler();
        void isFinished();
    }

    void putData(CheckPoint checkPoint);

    void startChallenge();

    void imReady();

    void imFinished();
}
