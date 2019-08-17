package io.github.nasrin.beware.page;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import io.github.nasrin.beware.R;
import io.github.nasrin.beware.database.BewareDao;
import io.github.nasrin.beware.database.BewareDatabase;
import io.github.nasrin.beware.database.Budget;
import io.github.nasrin.beware.database.CategoryExpense;
import io.github.nasrin.beware.database.DayExpense;
import io.github.nasrin.beware.database.Total;
import io.github.nasrin.beware.databinding.FragmentHomeBinding;

import static android.widget.AdapterView.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnItemSelectedListener {

    private FragmentHomeBinding homeBinding;
    private static final String TAG = "HomeFragment";

    private BewareDao bewareDao;

    private LiveData<List<CategoryExpense>> categoryExpenseList;
    private LiveData<List<DayExpense>> dayExpenseList;

    private MediatorLiveData<Double> totalSavingsLiveData = new MediatorLiveData<Double>();
    private LiveData<Total> totalEarningsLiveData, totalExpenseLiveData;

    private MediatorLiveData<Double> currentSavingLiveData = new MediatorLiveData<>();
    private LiveData<Total> subTotalExpenseLiveData;
    private LiveData<Total> subTotalEarnings;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        bewareDao = BewareDatabase.getInstance(getContext()).bewareDao();
        return homeBinding.getRoot();
    }

    private double calculateTotalSaving(Total total){
        totalSavingsLiveData.setValue(0.0);
        if(totalExpenseLiveData.getValue() != null && totalEarningsLiveData.getValue() != null){

            double a = totalEarningsLiveData.getValue().getTotal() - totalExpenseLiveData.getValue().getTotal();

            totalSavingsLiveData.setValue(a);
            Log.d(TAG, "calculateTotalSaving: "+a);
        }

        return 0;
    }

    private double calculateSubTotalSaving(Total total){
        currentSavingLiveData.setValue(0.0);
        if(subTotalExpenseLiveData.getValue() != null && subTotalEarnings.getValue() != null){

            double a = subTotalEarnings.getValue().getTotal() - subTotalExpenseLiveData.getValue().getTotal();

            currentSavingLiveData.setValue(a);
            Log.d(TAG, "calculateSubTotalSaving: "+a);
        }

        return 0;
    }

    private void addCurrentSaving(int month){

        if(subTotalExpenseLiveData != null && subTotalEarnings != null){
            currentSavingLiveData.removeSource(subTotalEarnings);
            currentSavingLiveData.removeSource(subTotalExpenseLiveData);
        }

        subTotalEarnings = bewareDao.getSubTotalEarnings(month);
        subTotalExpenseLiveData = bewareDao.getSubTotalExpenses(month);
        currentSavingLiveData.addSource(subTotalEarnings, this::calculateSubTotalSaving);
        currentSavingLiveData.addSource(subTotalExpenseLiveData, this::calculateSubTotalSaving);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.month_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        homeBinding.spinner.setAdapter(adapter);
        homeBinding.spinner.setOnItemSelectedListener(this);
        homeBinding.spinner.setSelection(3);*/

        DetailFragment.setSpinner(homeBinding.spinner, getContext(), this);

        homeBinding.floatingActionButton.setOnClickListener(v -> getFragmentManager().beginTransaction().replace(R.id.page_placeholder, new InputFragment()).commit());


        totalEarningsLiveData = bewareDao.getTotalEarnings();
        totalExpenseLiveData = bewareDao.getTotalExpenses();
        totalSavingsLiveData.addSource(totalEarningsLiveData, this::calculateTotalSaving);
        totalSavingsLiveData.addSource(totalExpenseLiveData, this::calculateTotalSaving);
        totalSavingsLiveData.observe(this, aDouble -> {
            if(aDouble != null){
                homeBinding.savingsTxt.setText(String.format(Locale.getDefault(),"Total Savings: %.2f/=", aDouble));
            }
        });

        currentSavingLiveData.observe(this, aDouble -> {
            if(aDouble != null){
                homeBinding.remainingTxt.setText(String.format(Locale.getDefault(),"Current Month Savings: %.2f/=", aDouble));
            }
        });

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        //todo for debugging
        //homeBinding.daygraph.addSeries(series);

        /*BarGraphSeries<DataPoint> barSeries = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6),
                new DataPoint(5, 1),
                new DataPoint(6, 5),
                new DataPoint(7, 3),
                new DataPoint(8, 200),
                new DataPoint(9, 300),
                new DataPoint(10, 600),
                new DataPoint(11, 6)

        });*/


        //homeBinding.catgraph.addSeries(barSeries);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.top_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.clearTable){
            BewareDatabase.getInstance(getContext()).clearTables();
        }

        return super.onOptionsItemSelected(item);
    }

    private void observeData(int month){

        if(dayExpenseList != null) dayExpenseList.removeObservers(this);
        if(categoryExpenseList != null) categoryExpenseList.removeObservers(this);

        categoryExpenseList = bewareDao.getCategoryExpenseList(month);
        categoryExpenseList.observe(this, categoryExpenses -> {


            homeBinding.catgraph.removeAllSeries();
            if(categoryExpenses != null && categoryExpenses.size() > 0){
                DataPoint dataPointList[] = new DataPoint[categoryExpenses.size()];
                String categoryArray[] = new String[categoryExpenses.size()];

                for (int i = 0; i < categoryExpenses.size() ; i++) {
                    CategoryExpense categoryExpense = categoryExpenses.get(i);

                    dataPointList[i]= new DataPoint(i+1, categoryExpense.getAmount());
                    categoryArray[i] = categoryExpense.getCategory();
                }


                BarGraphSeries<DataPoint> barGraphSeries = new BarGraphSeries<>(dataPointList);
                barGraphSeries.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                    @Override
                    public int get(DataPoint data) {
                        return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
                    }
                });

                barGraphSeries.setSpacing(50);

                barGraphSeries.setDrawValuesOnTop(true);
                barGraphSeries.setValuesOnTopColor(Color.RED);


                homeBinding.catgraph.removeAllSeries();
                homeBinding.catgraph.addSeries(barGraphSeries);

               /* StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(homeBinding.catgraph);
                staticLabelsFormatter.setHorizontalLabels(categoryArray);
                homeBinding.catgraph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
                homeBinding.catgraph.getGridLabelRenderer().setHorizontalAxisTitle("Category");
                homeBinding.catgraph.getGridLabelRenderer().setHorizontalLabelsAngle(45);
                homeBinding.catgraph.getGridLabelRenderer().setNumHorizontalLabels(8);*/

                //Toast.makeText(getContext(), "Value added "+categoryExpenses.size(), Toast.LENGTH_SHORT).show();


            }
        });

        dayExpenseList = bewareDao.getDayExpenseList(month);
        dayExpenseList.observe(this, dayExpenseList -> {

            homeBinding.daygraph.removeAllSeries();
            if(dayExpenseList != null && dayExpenseList.size() > 0){
                DataPoint dataPointList[] = new DataPoint[dayExpenseList.size()];

                for (int i = 0; i < dayExpenseList.size() ; i++) {
                    DayExpense dayExpense = dayExpenseList.get(i);

                    dataPointList[i]= new DataPoint(dayExpense.getDay(), dayExpense.getAmount());
                }


                LineGraphSeries<DataPoint> barGraphSeries = new LineGraphSeries<>(dataPointList);
                Log.d(TAG, "onChanged: dayExpenseList"+dayExpenseList.size());

                homeBinding.daygraph.removeAllSeries();
                homeBinding.daygraph.addSeries(barGraphSeries);
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        observeData(position+1);
        addCurrentSaving(position+1);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
