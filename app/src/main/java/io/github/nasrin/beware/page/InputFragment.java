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
import io.github.nasrin.beware.database.Expense;
import io.github.nasrin.beware.databinding.FragmentInputBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class InputFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    FragmentInputBinding inputBinding;

    public InputFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inputBinding = FragmentInputBinding.inflate(inflater, container, false);

        return inputBinding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        inputBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = inputBinding.cateEditTxt.getText().toString();
                String amount = inputBinding.spentEditTxt.getText().toString();
                String date = inputBinding.dateEditTxt.getText().toString();

                String date_arry[] = date.split("/");

                if(category.length()>0 && amount.length()>0 && date_arry.length==3){
                    Expense expense = new Expense(0, Integer.parseInt(date_arry[0]),
                            Integer.parseInt(date_arry[1]),
                            2019,
                            Double.parseDouble(amount),
                            category);

                    BewareDatabase.getInstance(getContext()).insertExpense(expense);


                    inputBinding.cateEditTxt.setText("");
                    inputBinding.spentEditTxt.setText("");
                    inputBinding.dateEditTxt.setText("");
                }
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
