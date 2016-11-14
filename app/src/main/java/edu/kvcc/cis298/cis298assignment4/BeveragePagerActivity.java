package edu.kvcc.cis298.cis298assignment4;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.List;

/**
 * Created by David Barnes on 11/5/2015.
 */
public class BeveragePagerActivity extends FragmentActivity {

    //String that can be used as a key for send in data between activities
    private static final String EXTRA_BEVERAGE_ID = "edu.kvcc.cis298.cis298assignment4.beverage_id";

    //Private vars for the viewpager and the list of beverages
    private ViewPager mViewPager;
    private List<Beverage> mBeverages;

    //Public static method that will return a properly formatted intent that can get this activity started
    public static Intent newIntent(Context context, String id) {
        Intent intent = new Intent(context, BeveragePagerActivity.class);
        intent.putExtra(EXTRA_BEVERAGE_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the content to the activity_beverage_pager layout
        setContentView(R.layout.activity_beverage_pager);

        //Get the Beverage Id that was sent from the list view from the intent
        String id = getIntent().getStringExtra(EXTRA_BEVERAGE_ID);

        //Get a handle to the view pager widget
        mViewPager = (ViewPager) findViewById(R.id.activity_beverage_pager_view_pager);

        //Get the beverages from the beverage collection
        mBeverages = BeverageCollection.get(this).getBeverages();

        //Create a fragment manager that the view pager adapter needs to operate
        FragmentManager fragmentManager = getSupportFragmentManager();

        //Set the adapter for the view pager to use the built in one, passing in the fragment manger just created
        mViewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            //Override method to get an item.
            @Override
            public Fragment getItem(int position) {
                //based on the position, get the beverage from the collection
                Beverage beverage = mBeverages.get(position);
                //return a new fragment for that beverage.
                return BeverageFragment.newInstance(beverage.getId());
            }

            //override method to get the size of the beverage list
            @Override
            public int getCount() {
                return mBeverages.size();
            }
        });

        //Loop through the beverages looking for the one that needs to be displayed, and once found
        //set it as the current item on the view pager.
        for (int i = 0; i < mBeverages.size(); i++) {
            if (mBeverages.get(i).getId().equals(id)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
