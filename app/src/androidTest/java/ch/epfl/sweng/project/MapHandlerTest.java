package ch.epfl.sweng.project;

import org.junit.Test;

import ch.epfl.sweng.project.Fragments.RunFragments.MapHandler;


public class MapHandlerTest {

    @Test(expected = NullPointerException.class)
    public void constructorMustThrowExceptionWithNullGoogleMap(){
        new MapHandler(null, 0);
    }

}
