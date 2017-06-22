package ch.ihl.runnest;

import org.junit.Test;

import ch.ihl.runnest.Fragments.RunFragments.MapHandler;


public class MapHandlerTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorMustThrowExceptionWithNullGoogleMap(){
        new MapHandler(null, 0);
    }

}
