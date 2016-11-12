package ch.epfl.sweng.project.Model;

public interface ChallangeProxy {

    interface Handler{
        void OnNewDataHandler(CheckPoint checkPoint);
        void isReadyHandler();
    }

    void putData(CheckPoint checkPoint);

    void setHandler(Handler setHandler);

    void imReady();
}
