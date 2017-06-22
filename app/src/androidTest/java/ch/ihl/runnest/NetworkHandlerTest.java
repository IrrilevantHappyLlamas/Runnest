package ch.ihl.runnest;

import org.junit.Test;

public class NetworkHandlerTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalArgumentException() {
        new NetworkHandler(null);
    }
}
