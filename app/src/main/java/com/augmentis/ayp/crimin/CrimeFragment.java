package com.augmentis.ayp.crimin;

import android.annotation.TargetApi;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * Created by Nutdanai on 7/18/2016.
 */
public class CrimeFragment extends Fragment{
    private Crime crime;
    private Button crimeDateButton;
    private CheckBox crimeSolvedCheckbox ;
    private EditText editText;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crime =  new Crime();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime,container,false);
        editText = (EditText) v. findViewById(R.id.crime_title);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int cout, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int after) {
                    crime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        crimeDateButton = (Button) v.findViewById(R.id.crime_date);
        crimeDateButton.setText( getFormattedDate(crime.getCrimeDate()));
        crimeDateButton.setEnabled(false);

        crimeSolvedCheckbox = (CheckBox) v.findViewById(R.id.crime_solved);
        crimeSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                crime.setSolved(isChecked);
                Log.d(CrimeActivity.TAG,"Crime" + crime.toString());
            }
        });


        return v;
    }

    private String  getFormattedDate(Date date){
        return new SimpleDateFormat("dd MMMM yyyy").format(date);
    }
}
