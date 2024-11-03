package com.example.psico;

public class DiaryEntry {
    private String id;
    private String date;
    private String text;

    public DiaryEntry() { }

    public DiaryEntry(String id, String date, String text) {
        this.id = id;
        this.date = date;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }
}
