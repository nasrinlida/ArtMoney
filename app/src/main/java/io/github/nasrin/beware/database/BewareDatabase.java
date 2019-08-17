package io.github.nasrin.beware.database;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Budget.class, Expense.class}, version = 1)
public abstract class BewareDatabase extends RoomDatabase {

    private static BewareDatabase instance;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static BewareDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                                                    BewareDatabase.class,
                                                    "BewareDatabase")
                                                    .build();
        }

        return instance;
    }

    public abstract BewareDao bewareDao();


    public void insertBudget(final Budget budget){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                bewareDao().insertBudget(budget);
            }
        });
    }

    public void insertExpense(final Expense expense){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                bewareDao().insertExpense(expense);
            }
        });
    }

    public void clearTables(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                clearAllTables();
            }
        });
    }
}
