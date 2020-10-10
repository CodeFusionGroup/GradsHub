package com.codefusiongroup.gradshub.common.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class ScheduleTest {

    String id = "1";
    String title = "Event";
    String link = "www.google.com";
    String deadline = "10/10/2020";
    String timezone = "GMT10";
    String date = "08/10/2020";
    String place = "WITS";

    Schedule schedule = new Schedule(id, title, link, deadline, timezone, date, place);
    Schedule schedule2 = new Schedule();

    @Test
    public void setId() {
        schedule2.setId(id);
        assertEquals(id, schedule2.getId());
    }

    @Test
    public void getId(){
        assertEquals(id, schedule.getId());
    }

    @Test
    public void setTitle() {
        schedule2.setTitle("Scheduled");
        assertEquals("Scheduled", schedule2.getTitle());
    }

    @Test
    public void getTitle() {
        assertEquals(title, schedule.getTitle());
    }

    @Test
    public void setLink() {
        schedule2.setLink("www.wits.ac.za");
        assertEquals("www.wits.ac.za", schedule2.getLink());
    }

    @Test
    public void getLink() {
        assertEquals(link, schedule.getLink());
    }

    @Test
    public void setDeadline() {
        schedule2.setDeadline(deadline);
        assertEquals(deadline, schedule2.getDeadLine());
    }

    @Test
    public void getDeadLine() {
        assertEquals(deadline, schedule.getDeadLine());
    }

    @Test
    public void setTimezone() {
        schedule2.setTimezone(timezone);
        assertEquals(timezone, schedule2.getTimezone());
    }

    @Test
    public void getTimezone() {
        assertEquals(timezone, schedule.getTimezone());
    }

    @Test
    public void setDate() {
        schedule2.setDate(date);
        assertEquals(date, schedule2.getDate());
    }

    @Test
    public void getDate() {
        assertEquals(date, schedule.getDate());
    }

    @Test
    public void setPlace() {
        schedule2.setPlace(place);
        assertEquals(place, schedule2.getPlace());
    }

    @Test
    public void getPlace() {
        assertEquals(place, schedule.getPlace());
    }

    @Test
    public void setStarCount() {
        schedule2.setStarCount(10);
        assertEquals(10,schedule2.getStarCount());
    }

    @Test
    public void setFavouredByUser() {
        schedule2.setFavouredByUser(false);
        assertEquals(false, schedule2.isFavouredByUser());

    }
}