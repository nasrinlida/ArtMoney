package io.github.nasrin.beware.page;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import io.github.nasrin.beware.R;
import io.github.nasrin.beware.database.BewareDatabase;
import io.github.nasrin.beware.database.Budget;
import io.github.nasrin.beware.databinding.FragmentBudgetBinding;


public class BudgetFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentBudgetBinding budgetBinding;
    private int month = 0;

    public BudgetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        budgetBinding = FragmentBudgetBinding.inflate(inflater, container, false);

        return budgetBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        month = DetailFragment.setSpinner(budgetBinding.spinner, getContext(), this);

        budgetBinding.materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = budgetBinding.earningEditTxt.getText().toString();
                String budget = budgetBinding.budgetEditTxt.getText().toString();

                if(amount != null && amount.length()>0 && budget !=null && budget.length() > 0){
                    Budget budgetObject = new Budget(0, budgetBinding.spinner.getSelectedItemPosition()+1, 2019,
                            Double.parseDouble(amount),
                            Double.parseDouble(budget));

                    BewareDatabase.getInstance(getContext()).insertBudget(budgetObject);

                    budgetBinding.earningEditTxt.setText("");
                    budgetBinding.budgetEditTxt.setText("");
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        month = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
