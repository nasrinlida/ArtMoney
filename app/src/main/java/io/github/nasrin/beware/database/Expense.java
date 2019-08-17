package io.github.nasrin.beware.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Expense {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int day, month, year;
    private double amount;
    private String category;


    public Expense(int id, int day, int month, int year, Double amount, String category) {
        this.id = id;
        this.day = day;
        this.month = month;
        this.year = year;
        this.amount = amount;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
