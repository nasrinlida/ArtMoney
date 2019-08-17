package io.github.nasrin.beware.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Room;

@Dao
public interface BewareDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBudget(Budget budget);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExpense(Expense expense);

    @Query("Select category, sum(amount) as amount from Expense where month=:month and year=2019 group by category")
    LiveData<List<CategoryExpense>> getCategoryExpenseList(int month);

    @Query("Select day, sum(amount) as amount from Expense where month=:month and year=2019 group by day")
    LiveData<List<DayExpense>> getDayExpenseList(int month);

    @Query("Select * from budget where month=:month Order By id DESC Limit 1")
    LiveData<Budget> getBudget(int month);

    @Query("Select sum(amount) as total from Expense")
    LiveData<Total> getTotalExpenses();

    @Query("Select sum(earning) as total from Budget")
    LiveData<Total> getTotalEarnings();

    @Query("Select sum(amount) as total from Expense where month=:month and year=2019")
    LiveData<Total> getSubTotalExpenses(int month);

    @Query("Select earning as total from Budget where month=:month and year=2019 Order By id DESC Limit 1")
    LiveData<Total> getSubTotalEarnings(int month);

    @Query("Select budget as total from budget where month=:month and year=2019 Order By id DESC Limit 1")
    LiveData<Total> getSubTotalBudget(int month);

}
