package com.example.spendingtrackerlite.models;

public class SettingItem {
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_HEADER = 1;

    public int type;
    public String title;
    public String description;

    public SettingItem(int type, String title, String description) {
        this.type = type;
        this.title = title;
        this.description = description;
    }
}
