package com.example.icooking;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;


public class Inventory implements Serializable {


    private int ingredientDuration_default;
    private String expiryDate;
    private boolean selected;
    @Exclude
    private String ingredientName;
    @Exclude
    private String key;




    public Inventory(String ingredientName, String expiryDate) {
        this.ingredientName = ingredientName;
        this.expiryDate=expiryDate;
    }

    public Inventory() {

    }

    public String getIngredientName() {
        return ingredientName;
    }
    public String getExpiryDate() {
        return expiryDate;
    }

    public String getDayLeft() {
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd[ HH:mm:ss]")
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter();
            LocalDateTime expiryDate = LocalDateTime.parse(this.expiryDate, formatter);
            LocalDateTime todayDate = LocalDateTime.now();
            long daysBetween = ChronoUnit.DAYS.between(todayDate, expiryDate);
            String daysBetween_str = Long.toString(daysBetween);

            return daysBetween_str;
        } catch (Exception e) {
            return e.toString();
        }

    }

//    public void setIngredientName(String ingredientName) {
//        this.ingredientName = ingredientName;
//    }
//
//    public void setDayLeft(String dayLeft) {
//        this.dayLeft = dayLeft;
//    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected){
        this.selected = selected;
    }
}

