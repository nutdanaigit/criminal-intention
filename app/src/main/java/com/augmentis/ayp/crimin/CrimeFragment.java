package com.augmentis.ayp.crimin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
    private static final String CRIME_ID = "CrimeFragment.CRIME_ID";
    private static final String DILOG_DATE = "CrimeFragment.CRIME_DatePicker";
    private static final String DILOG_TIME = "CrimeFragment.CRIME_TimePicker";
    private static final String CRIME_POSITION = "CrimeFragment.CRIME_POS";
    private static final int REQUEST_DATE = 22 ;
    private static final int REQUEST_DATE2 = 23 ;
    private Crime crime;
    private EditText editText;
    private Button crimeDateButton;
    private Button crimeTimeButton;
    private Button crimeDeleteButton;
    private CheckBox crimeSolvedCheckbox ;

    public CrimeFragment(){}

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle  args = new Bundle();
        args.putSerializable(CRIME_ID,crimeId);

        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return  crimeFragment;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId =(UUID) getArguments().getSerializable(CRIME_ID);
        crime = CrimeLab.getInstance(getActivity()).getCrimeById(crimeId) ;
//        Log.d(CrimeListFragment.TAG,"crime.getId()=" + crime.getId());
        Log.d(CrimeListFragment.TAG,"crime.getTitle()=" + crime.getTitle());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime,container,false);
        editText = (EditText) v. findViewById(R.id.crime_title);
        editText.setText(crime.getTitle());
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

        crimeDeleteButton = (Button) v.findViewById(R.id.delete_btn);
        crimeDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
                crimeLab.getCrime().remove(crimeLab.getCrimeById(crime.getId()));
                getActivity().finish();
//                Intent intent = new Intent(getActivity(),CrimeListActivity.class);
//                startActivity(intent);
            }
        });


        crimeTimeButton = (Button) v.findViewById(R.id.crime_time);
        crimeTimeButton.setText(getFormattedtime(crime.getCrimeDate()));
        crimeTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                TimePickerFragment dialogFragment = TimePickerFragment.newInstance(crime.getCrimeDate());
                dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE2);
                dialogFragment.show(fm,DILOG_DATE);
            }
        });

        crimeDateButton = (Button) v.findViewById(R.id.crime_date);
        crimeDateButton.setText( getFormattedDate(crime.getCrimeDate()));
        crimeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialogFragment = DatePickerFragment.newInstance(crime.getCrimeDate());
//                DatePickerFragment dialogFragment = new DatePickerFragment();
//                Bundle args = new Bundle();
//                args.putSerializable("ARG_DATE",crime.getCrimeDate());
//                dialogFragment.setArguments(args);
                dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialogFragment.show(fm,DILOG_DATE);
            }
        });

        crimeSolvedCheckbox = (CheckBox) v.findViewById(R.id.crime_solved);
        crimeSolvedCheckbox.setChecked(crime.isSolved());
        crimeSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                crime.setSolved(isChecked);
                Log.d(CrimeListFragment.TAG,"Crime" + crime.toString());
            }
        });

        return v;
    }

    private String  getFormattedtime(Date date){
        return new SimpleDateFormat("HH : mm").format(date);
    }

    private String  getFormattedDate(Date date){
        return new SimpleDateFormat("dd MMMM yyyy").format(date);
    }




    @Override
    public void onActivityResult(int requestCode, int result, Intent data) {
        if(result!=Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_DATE){
         Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            //set
            crime.setCrimeDate(date);
            crimeDateButton.setText(getFormattedDate(crime.getCrimeDate()));
        }
        if(requestCode == REQUEST_DATE2){
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_DATE2);

            //set
            crime.setCrimeDate(date);
            crimeTimeButton.setText(getFormattedtime(crime.getCrimeDate()));
        }
    }
}
