package ch.epfl.sweng.project.Model;

public interface ChallengeProxy {

    interface Handler{
        void OnNewDataHandler(CheckPoint checkPoint);
        void isReadyHandler();
    }

    void putData(CheckPoint checkPoint);

    //TODO remove this
    void setHandler(Handler setHandler);

    void startChallenge();

    void imReady();
}
