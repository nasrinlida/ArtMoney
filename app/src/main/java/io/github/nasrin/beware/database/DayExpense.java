package io.github.nasrin.beware.database;

public class DayExpense {
    private int day;
    private double amount;

    public DayExpense(int day, double amount) {
        this.day = day;
        this.amount = amount;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
