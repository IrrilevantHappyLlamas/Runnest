package ch.epfl.sweng.project;

import org.junit.Test;

public class NetworkHandlerTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalArgumentException() {
        new NetworkHandler(null);
    }
}
