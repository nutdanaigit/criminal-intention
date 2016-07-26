package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nutdanai on 7/18/2016.
 */
public class CrimeListFragment extends Fragment {
    private static final int REQUEST_UPDATED_CRIME = 137;

    private RecyclerView _crimeRecyclerView;

    private CrimeAdapter _adapter;

    protected static final String TAG = "CRIME_LIST";
    private int crimePos;

    /**
     *  start
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container,false); //create view to get in container
        // Inside RecyclerView,I don't know how it's work.
        _crimeRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycler_view); //Get RecyclerView and give v to find ID
        _crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        /**
            Set layoutManager .It's setter. linear is vertical
            getActivity stay in fragment.
         **/

        updateUI();
        return v; //return view to use up to you.
    }


    /**
     * Update UI
     */
    private void updateUI(){
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());//Call Instance and get ob
        List<Crime> crimes = crimeLab.getCrime();

        if(_adapter==null){
            _adapter = new CrimeAdapter(crimes); //Create ob by call CrimeAdapter
            _crimeRecyclerView.setAdapter(_adapter); //set adapter but I don't know when crimeRecyclerView work.
        }else{
//            _adapter.notifyDataSetChanged();
            _adapter.notifyItemChanged(crimePos);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"Resume list");
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_UPDATED_CRIME){
            if(resultCode == Activity.RESULT_OK){
                crimePos = (int) data.getExtras().get("position");
                Log.d(TAG, "get crimePos=" + crimePos);
            }
            //Blah blah
            Log.d(TAG,"Return form CrimeFragment ");
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView _titleTextView;
        public TextView _dateTextView;
        public CheckBox _solvedCheckBox;
        int _position;
        Crime _crime;

        public CrimeHolder(View itemView) {
            super(itemView);
            _titleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_crime_title_text_view);
            _solvedCheckBox = (CheckBox)
                        itemView.findViewById(R.id.list_item_crime_solved_check_box);
            _dateTextView =(TextView)
                        itemView.findViewById(R.id.list_item_crime_date_text_view);

            itemView.setOnClickListener(this); //plus OnClickListener

        }

        public void bind(Crime crime,int position) {
            _crime = crime;
            _position = position;
            _titleTextView.setText(_crime.getTitle());
            _dateTextView.setText(_crime.getCrimeDate().toString());
            _solvedCheckBox.setChecked(_crime.isSolved());
        }

        /**
         * CrimeHolder
         * onclick Method
         */
        @Override
        public void onClick(View view) {
//            Toast.makeText(getActivity(),
//                    "Press!"+_titleTextView.getText(),
//                    Toast.LENGTH_SHORT)
//                    .show();
            Log.d(TAG, "Send position ; "+ _position);
            Intent intent = CrimePagerActivity.newIntent(getActivity(),_crime.getId(),_position);//Call Method newIntent and sent
            startActivityForResult(intent,REQUEST_UPDATED_CRIME);//Call Method of Fragment class
        }
    }
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> _crimes;
        private int viewCreatingCount;
        public  CrimeAdapter(List<Crime> crimes){this._crimes = crimes;} //ArrayList

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            viewCreatingCount++;
            Log.d(TAG,"Create view holder for CrimeList : creating view time = "+ viewCreatingCount);
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity()); // from is statics method LayoutInflater stay in getActivity
            View v = layoutInflater.inflate(R.layout.list_item_crime,parent,false); // Draw , Create TextView 2txt
            return new CrimeHolder(v);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Log.d(TAG,"Bind view holder for CrimeList : position = " + position);
            Crime crime = _crimes.get(position);
            holder.bind(crime,position); // get crime object to use
        }

        @Override
        public int getItemCount() {
            return _crimes.size();
        }
    }

}
