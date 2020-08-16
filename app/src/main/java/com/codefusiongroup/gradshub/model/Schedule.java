package com.codefusiongroup.gradshub.model;

public class Schedule {

    private String id;
    private String title;
    private String link;
    private String deadline;
    private String timezone;
    private String date;
    private String place;
    private int votesCount = 0;


    public Schedule(String id, String title, String link, String deadline, String timezone, String date, String place) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.deadline = deadline;
        this.timezone = timezone;
        this.date = date;
        this.place = place;

    }

    public Schedule() {}


    public void setId(String id) { this.id = id; }

    public String getId() { return id; }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDeadLine() {
        return deadline;
    }

    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getTimezone() { return timezone; }

    public void setDate(){ this.date = date; }

    public String getDate() { return date; }

    public void setPlace(String place) { this.place = place; }

    public String getPlace() { return place; }

    public void setVotesCount(int votesCount) {
        this.votesCount = votesCount;
    }

    public int getVotesCount() {
        return votesCount;
    }


}
