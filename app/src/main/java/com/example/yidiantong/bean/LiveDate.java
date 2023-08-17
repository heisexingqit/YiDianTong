package com.example.yidiantong.bean;

import java.io.Serializable;

public class LiveDate implements Serializable {
    private String time;
    private String minutes;
    private String seconds;
    private String hours;
    private String month;
    private String timezoneOffset;
    private String year;
    private String day;
    private String date;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getSeconds() {
        return seconds;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(String timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "LiveStartDate{" +
                "time='" + time + '\'' +
                ", minutes='" + minutes + '\'' +
                ", seconds='" + seconds + '\'' +
                ", hours='" + hours + '\'' +
                ", month='" + month + '\'' +
                ", timezoneOffset='" + timezoneOffset + '\'' +
                ", year='" + year + '\'' +
                ", day='" + day + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
