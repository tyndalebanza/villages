package com.tyndaleb.villages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

public class VillageSearchActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village_search);

        ImageView btnBack = (ImageView) findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Check for empty data in the form
                //mTimer = null ;
                finish() ;

            }

        });
        String search_term = getIntent().getStringExtra("extra_village_search");
        TextView searchTerm = (TextView) findViewById(R.id.searchTerm);
        searchTerm.setText(search_term);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tabLayout.addTab(tabLayout.newTab().setText("Villages"));
        tabLayout.addTab(tabLayout.newTab().setText("Country"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final MyTabAdapter adapter = new MyTabAdapter(this,getSupportFragmentManager(),
                tabLayout.getTabCount());
        Fragment Village = new Village();
        Fragment Country = new Country();

        adapter.addFragment(Village, search_term);
        adapter.addFragment(Country, search_term);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
    }
}