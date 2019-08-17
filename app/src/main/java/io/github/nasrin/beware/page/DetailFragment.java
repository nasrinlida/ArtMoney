package io.github.nasrin.beware.page;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import io.github.nasrin.beware.R;
import io.github.nasrin.beware.database.BewareDao;
import io.github.nasrin.beware.database.BewareDatabase;
import io.github.nasrin.beware.database.Budget;
import io.github.nasrin.beware.database.CategoryExpense;
import io.github.nasrin.beware.database.Expense;
import io.github.nasrin.beware.database.Total;
import io.github.nasrin.beware.databinding.FragmentBudgetBinding;
import io.github.nasrin.beware.databinding.FragmentDetailBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentDetailBinding detailBinding;

    private BewareDao bewareDao;

    private MediatorLiveData<Double> remainingLiveData = new MediatorLiveData<>();
    private LiveData<Total> budgetLiveData, spentLiveData;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        detailBinding = FragmentDetailBinding.inflate(inflater, container, false);
        bewareDao = BewareDatabase.getInstance(getContext()).bewareDao();

        /*ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                bewareDao.insertExpense(new Expense(1,2,3,2019, 12.00, "Food"));
                bewareDao.insertBudget(new Budget(1,3,2019, 20.9, 10.9));
            }
        });*/

        return detailBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setSpinner(detailBinding.spinner, getContext(), this);

    }

    public static int setSpinner(Spinner spinner, Context context, AdapterView.OnItemSelectedListener itemSelectedListener){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.month_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(itemSelectedListener);

        Date date = Calendar.getInstance().getTime();
        int month = date.getMonth();

        switch (month){
            case 0:
                spinner.setSelection(0);
                break;
            case 1:
                spinner.setSelection(1);
                break;
            case 2:
                spinner.setSelection(2);
                break;
            case 3:
                spinner.setSelection(3);
                break;
            case 4:
                spinner.setSelection(4);
                break;
            case 5:
                spinner.setSelection(5);
                break;
            case 6:
                spinner.setSelection(6);
                break;
            case 7:
                spinner.setSelection(7);
                break;
            case 8:
                spinner.setSelection(8);
                break;
            case 9:
                spinner.setSelection(9);
                break;
            case 10:
                spinner.setSelection(10);
                break;
            case 11:
                spinner.setSelection(11);
                break;
        }

        return month;
    }

    private double calculateSubTotalSaving(Total total){
        remainingLiveData.setValue(0.0);
        if(budgetLiveData.getValue() != null && spentLiveData.getValue() != null){

            double a = budgetLiveData.getValue().getTotal() - spentLiveData.getValue().getTotal();

            remainingLiveData.setValue(a);
        }

        return 0;
    }

    private void observeData(int month){


        if(budgetLiveData != null && spentLiveData != null){
            remainingLiveData.removeSource(budgetLiveData);
            remainingLiveData.removeSource(spentLiveData);

            budgetLiveData.removeObservers(this);
            spentLiveData.removeObservers(this);
            remainingLiveData.removeObservers(this);
        }

        budgetLiveData = bewareDao.getSubTotalBudget(month);
        spentLiveData = bewareDao.getSubTotalExpenses(month);
        remainingLiveData.addSource(budgetLiveData, this::calculateSubTotalSaving);
        remainingLiveData.addSource(spentLiveData, this::calculateSubTotalSaving);

        spentLiveData.observe(this, total -> {
            if(total != null){
                detailBinding.spentAmnt.setText(String.format(Locale.getDefault(),"%.2f",total.getTotal()));
                detailBinding.textView7.setText(String.format(Locale.getDefault(),"%.2f",total.getTotal()));
            }
        });

        remainingLiveData.observe(this, total ->{
            if(total != null){
                detailBinding.remainingAmnt.setText(String.format(Locale.getDefault(),"%.2f",total));

            }
        });

        budgetLiveData.observe(this,  budget -> {

            if(budget != null){
                detailBinding.noBudgetTxt.setVisibility(View.GONE);
                detailBinding.budgetAmnt.setVisibility(View.VISIBLE);
                detailBinding.spentAmnt.setVisibility(View.VISIBLE);
                detailBinding.remainingAmnt.setVisibility(View.VISIBLE);

                detailBinding.budgetTxt.setVisibility(View.VISIBLE);
                detailBinding.spentTxt.setVisibility(View.VISIBLE);
                detailBinding.remainingTxtTxt.setVisibility(View.VISIBLE);

                detailBinding.budgetAmnt.setText(String.format(Locale.getDefault(),"%.2f",budget.getTotal()));


            }else {
                detailBinding.budgetAmnt.setVisibility(View.GONE);
                detailBinding.spentAmnt.setVisibility(View.GONE);
                detailBinding.remainingAmnt.setVisibility(View.GONE);

                detailBinding.budgetTxt.setVisibility(View.GONE);
                detailBinding.spentTxt.setVisibility(View.GONE);
                detailBinding.remainingTxtTxt.setVisibility(View.GONE);

                detailBinding.noBudgetTxt.setVisibility(View.VISIBLE);
            }
        });

        bewareDao.getCategoryExpenseList(month).observe(this, categoryExpenses -> {

            setVisibilityGone();

            if(categoryExpenses != null && categoryExpenses.size() > 0){

                detailBinding.noDetailTxt.setVisibility(View.GONE);

                double spent = 0;

                for (int i = 0; i < categoryExpenses.size(); i++) {

                    CategoryExpense categoryExpense = categoryExpenses.get(i);


                    spent += categoryExpense.getAmount();
                    int temp = i+1;

                    switch (temp){
                        case 1:
                            detailBinding.cateTxt1.setVisibility(View.VISIBLE);
                            detailBinding.value1.setVisibility(View.VISIBLE);
                            detailBinding.cateTxt1.setText(categoryExpense.getCategory());
                            detailBinding.value1.setText(String.valueOf(categoryExpense.getAmount()));
                            break;
                        case 2:
                            detailBinding.cateTxt2.setVisibility(View.VISIBLE);
                            detailBinding.value2.setVisibility(View.VISIBLE);
                            detailBinding.cateTxt2.setText(categoryExpense.getCategory());
                            detailBinding.value2.setText(String.valueOf(categoryExpense.getAmount()));
                            break;
                        case 3:
                            detailBinding.cateTxt3.setVisibility(View.VISIBLE);
                            detailBinding.value3.setVisibility(View.VISIBLE);
                            detailBinding.cateTxt3.setText(categoryExpense.getCategory());
                            detailBinding.value3.setText(String.valueOf(categoryExpense.getAmount()));
                            break;
                        case 4:
                            detailBinding.cateTxt4.setVisibility(View.VISIBLE);
                            detailBinding.value4.setVisibility(View.VISIBLE);
                            detailBinding.cateTxt4.setText(categoryExpense.getCategory());
                            detailBinding.value4.setText(String.valueOf(categoryExpense.getAmount()));
                            break;
                        case 5:
                            detailBinding.cateTxt5.setVisibility(View.VISIBLE);
                            detailBinding.value5.setVisibility(View.VISIBLE);
                            detailBinding.cateTxt5.setText(categoryExpense.getCategory());
                            detailBinding.value5.setText(String.valueOf(categoryExpense.getAmount()));
                            break;
                        case 6:
                            detailBinding.cateTxt6.setVisibility(View.VISIBLE);
                            detailBinding.value6.setVisibility(View.VISIBLE);
                            detailBinding.cateTxt6.setText(categoryExpense.getCategory());
                            detailBinding.value6.setText(String.valueOf(categoryExpense.getAmount()));
                            break;
                        case 7:
                            detailBinding.cateTxt7.setVisibility(View.VISIBLE);
                            detailBinding.value7.setVisibility(View.VISIBLE);
                            detailBinding.cateTxt7.setText(categoryExpense.getCategory());
                            detailBinding.value7.setText(String.valueOf(categoryExpense.getAmount()));
                            break;
                        case 8:
                            detailBinding.cateTxt8.setVisibility(View.VISIBLE);
                            detailBinding.value8.setVisibility(View.VISIBLE);
                            detailBinding.cateTxt8.setText(categoryExpense.getCategory());
                            detailBinding.value8.setText(String.valueOf(categoryExpense.getAmount()));
                            break;
                        case 9:
                            detailBinding.cateTxt9.setVisibility(View.VISIBLE);
                            detailBinding.value9.setVisibility(View.VISIBLE);
                            detailBinding.cateTxt9.setText(categoryExpense.getCategory());
                            detailBinding.value9.setText(String.valueOf(categoryExpense.getAmount()));
                            break;
                        case 10:
                            detailBinding.cateTxt10.setVisibility(View.VISIBLE);
                            detailBinding.value10.setVisibility(View.VISIBLE);
                            detailBinding.cateTxt10.setText(categoryExpense.getCategory());
                            detailBinding.value10.setText(String.valueOf(categoryExpense.getAmount()));
                            break;
                        case 11:
                            detailBinding.cateTxt11.setVisibility(View.VISIBLE);
                            detailBinding.value11.setVisibility(View.VISIBLE);
                            detailBinding.cateTxt11.setText(categoryExpense.getCategory());
                            detailBinding.value11.setText(String.valueOf(categoryExpense.getAmount()));
                            break;
                        case 12:
                            detailBinding.cateTxt12.setVisibility(View.VISIBLE);
                            detailBinding.value12.setVisibility(View.VISIBLE);
                            detailBinding.cateTxt12.setText(categoryExpense.getCategory());
                            detailBinding.value12.setText(String.valueOf(categoryExpense.getAmount()));
                            break;
                    }
                }

                detailBinding.divider.setVisibility(View.VISIBLE);
                detailBinding.textView7.setVisibility(View.VISIBLE);
                detailBinding.textView7.setText(String.format(Locale.getDefault(),"%.2f",spent));
            }
            else {
                detailBinding.noDetailTxt.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setVisibilityGone(){
        detailBinding.cateTxt1.setVisibility(View.GONE);
        detailBinding.value1.setVisibility(View.GONE);
        detailBinding.cateTxt2.setVisibility(View.GONE);
        detailBinding.value2.setVisibility(View.GONE);
        detailBinding.cateTxt3.setVisibility(View.GONE);
        detailBinding.value3.setVisibility(View.GONE);
        detailBinding.cateTxt4.setVisibility(View.GONE);
        detailBinding.value4.setVisibility(View.GONE);
        detailBinding.cateTxt5.setVisibility(View.GONE);
        detailBinding.value5.setVisibility(View.GONE);
        detailBinding.cateTxt6.setVisibility(View.GONE);
        detailBinding.value6.setVisibility(View.GONE);
        detailBinding.cateTxt7.setVisibility(View.GONE);
        detailBinding.value7.setVisibility(View.GONE);
        detailBinding.cateTxt8.setVisibility(View.GONE);
        detailBinding.value8.setVisibility(View.GONE);
        detailBinding.cateTxt9.setVisibility(View.GONE);
        detailBinding.value9.setVisibility(View.GONE);
        detailBinding.cateTxt10.setVisibility(View.GONE);
        detailBinding.value10.setVisibility(View.GONE);
        detailBinding.cateTxt11.setVisibility(View.GONE);
        detailBinding.value11.setVisibility(View.GONE);
        detailBinding.cateTxt12.setVisibility(View.GONE);
        detailBinding.value12.setVisibility(View.GONE);

        detailBinding.divider.setVisibility(View.GONE);
        detailBinding.textView7.setVisibility(View.GONE);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        observeData(position+1);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
