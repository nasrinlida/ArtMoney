package io.github.nasrin.beware;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import io.github.nasrin.beware.database.BewareDatabase;
import io.github.nasrin.beware.databinding.ActivityMainBinding;
import io.github.nasrin.beware.page.BudgetFragment;
import io.github.nasrin.beware.page.DetailFragment;
import io.github.nasrin.beware.page.HomeFragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.page_placeholder, new HomeFragment()).commit();

        activityMainBinding.bottomNavigation.setOnNavigationItemSelectedListener(this);

        //BewareDatabase.getInstance(this).clearTables();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.page_placeholder, new HomeFragment()).commit();
                break;

            case R.id.detail:
                getSupportFragmentManager().beginTransaction().replace(R.id.page_placeholder, new DetailFragment()).commit();
                break;

            case R.id.budget:
                getSupportFragmentManager().beginTransaction().replace(R.id.page_placeholder, new BudgetFragment()).commit();
                break;
        }

        return false;
    }
}
