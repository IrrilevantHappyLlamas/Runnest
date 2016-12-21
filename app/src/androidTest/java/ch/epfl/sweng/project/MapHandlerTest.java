package ch.epfl.sweng.project;

import org.junit.Test;

import ch.epfl.sweng.project.Fragments.RunFragments.MapHandler;


public class MapHandlerTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorMustThrowExceptionWithNullGoogleMap(){
        new MapHandler(null, 0);
    }

}
