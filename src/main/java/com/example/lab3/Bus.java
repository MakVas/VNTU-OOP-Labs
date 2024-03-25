package com.example.lab3;

public class Bus {
    private int number;
    private String from;
    private String where;
    private String firsttime;
    private String secondtime;
    private String date;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getFirsttime() {
        return firsttime;
    }

    public void setFirsttime(String firsttime) {
        this.firsttime = firsttime;
    }

    public String getSecondtime() {
        return secondtime;
    }

    public void setSecondtime(String secondtime) {
        this.secondtime = secondtime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Bus(int number, String from, String where, String firsttime, String secondtime, String date) {
        this.number = number;
        this.from = from;
        this.where = where;
        this.firsttime = firsttime;
        this.secondtime = secondtime;
        this.date = date;
    }
}
