package com.augmentis.ayp.crimin;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.CrimeLab;

import java.util.List;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks,CrimeFragment.Callbacks {
    @Override
    protected Fragment onCreateFragment() {

        return new CrimeListFragment();
    }

    @Override
    public void onOpenSelectFirst() {
        if (findViewById(R.id.detail_fragment_container) != null) {
            List<Crime> crimeList = CrimeLab.getInstance(this).getCrime();
            if (crimeList != null && crimeList.size() > 0) {
                //get first item
                Crime crime = crimeList.get(0);

                //two pane
                Fragment newDetailFragment = CrimeFragment.newInstance(crime.getId());

                //replace old fragment with new one
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_fragment_container, newDetailFragment)
                        .commit();
                Log.d("pearl", "222");
            }
        }
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            // single pane
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            CrimeFragment currentDetailFragment = (CrimeFragment) getSupportFragmentManager().findFragmentById(R.id.detail_fragment_container);
            if (currentDetailFragment == null || !currentDetailFragment.getCrimeId().equals(crime.getId())) {

                Fragment newDetailFragment = CrimeFragment.newInstance(crime.getId());

                //replace old fragment with new one
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_fragment_container, newDetailFragment)
                        .commit();
                Log.d("pearl","222");
            } else {
                Log.d("pearl","111");
                currentDetailFragment.updateUI();
            }
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        //Update List
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    @Override
    public void onCrimeDeleted() {
        List<Crime> crimeList = CrimeLab.getInstance(this).getCrime();
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        CrimeFragment detailFragment = (CrimeFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_fragment_container);
        //show
//        Log.d("p2","checkUpdate");
//        int pp =crimeList.indexOf(crimeList);
//
//        listFragment.checkUpdate(pp);
//        Log.d("p2","checkUpdate Finish");
        //clear
        Log.d("p1"," Clear");
        getSupportFragmentManager().beginTransaction().detach(detailFragment).commit();
        Log.d("p1"," Clear Finish");



        Log.d("p3"," Update ");
        listFragment.updateUI();
        Log.d("p3"," Update  Finish");





    }

}
