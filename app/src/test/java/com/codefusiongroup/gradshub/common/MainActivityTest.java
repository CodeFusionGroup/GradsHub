package com.codefusiongroup.gradshub.common;

import android.content.Context;
import android.net.Uri;

import com.codefusiongroup.gradshub.common.models.Schedule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

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
/*
    Tests depends on alarm context. Impossible to unit test without device
    @Test
    public void  testScheduleNotification(){
        MainActivity mockedMainActivity = new MainActivity();
//        Context context, int notificationId, Schedule event, Date date, Uri uri
        Context context = mockedMainActivity.getApplicationContext();
        int notificationId = 10011101;          //Dummy notification
        Schedule event = new Schedule();
        event.setDate("10:10:2020");
        event.setId("IdTest");
        event.setFavouredByUser(true);
        event.setPlace("University of the Witwatersrand");
        event.setTitle("Test Event");

           when(cont.getSystemService(Context.ALARM_SERVICE))
                 .thenReturn("Something");
        Date date = new Date();
        //Uri uri = Uri.parse("http://stackoverflow.com");
        Uri uri = null;
        mockedMainActivity.scheduleNotification(context, notificationId, event, date, uri);
    }
*/

}