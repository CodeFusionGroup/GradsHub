package com.codefusiongroup.gradshub;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
/*
    @Test
    public void testSpyObject() throws Exception{
        SpyTestObject spyObject = new SpyTestObject();
        SpyTestObject spy = Mockito.spy(spyObject);
        Mockito.doNothing().when(spy).methodB();

        spy.methodA();
        Mockito.verify(spy).methodB();
    }

    public class SpyTestObject{
        public void methodA(){
            methodB();
        }
        public void methodB(){
            throw new RuntimeException();
        }
    }*/
}
