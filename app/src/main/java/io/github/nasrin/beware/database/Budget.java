package io.github.nasrin.beware.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Budget {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int month, year;
    private double earning, budget;

    public Budget(int id, int month, int year, double earning, double budget) {
        this.id = id;
        this.month = month;
        this.year = year;
        this.earning = earning;
        this.budget = budget;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getEarning() {
        return earning;
    }

    public void setEarning(double earning) {
        this.earning = earning;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}
