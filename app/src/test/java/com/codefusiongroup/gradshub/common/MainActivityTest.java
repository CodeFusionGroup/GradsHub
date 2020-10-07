package com.codefusiongroup.gradshub.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest{
    @Mock
    MainActivity mockMainActivity;

    @Test
    public void testUrlValidator(){
     //   when(mockMainActivity.validateEventLinkFormat(link))
       //         .thenReturn(link);
        MainActivity mockedMainActivity = new MainActivity();

        // ...when the string is returned from the object under test...
        String link = "https://google.com";
        String result = mockedMainActivity.validateEventLinkFormat(link);

        // ...then the result should be the expected one.
        assertThat(result, is(link));
    }

    @Test
    public void testUrlValidator2(){
        MainActivity mockedMainActivity = new MainActivity();
        String link2 = "google.com";
        String result = mockedMainActivity.validateEventLinkFormat(link2);
        assertThat(result, is("http://google.com"));
    }


}