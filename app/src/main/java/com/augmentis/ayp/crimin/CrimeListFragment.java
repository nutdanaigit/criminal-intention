package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.CrimeLab;
import com.augmentis.ayp.crimin.model.PictureUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Nutdanai on 7/18/2016.
 */
public class CrimeListFragment extends Fragment {
    private static final int REQUEST_UPDATED_CRIME = 137;
    private static final java.lang.String SUBTITLE_VISIBLE_STATE ="SUBTITLE_VISIBLE" ;
    protected static final String TAG = "CRIME_LIST";
    private RecyclerView _crimeRecyclerView;
    private CrimeAdapter _adapter;
    private Integer[] crimePos;
    private boolean _subtitleVisible;
    private TextView showToAdd;
    private String subtitle;



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
        showToAdd = (TextView) v.findViewById(R.id.show_txt);




        if(savedInstanceState!=null){
            _subtitleVisible=savedInstanceState.getBoolean(SUBTITLE_VISIBLE_STATE);
        }

        /**
            Set layoutManager .It's setter. linear is vertical
            getActivity stay in fragment.
         **/

        updateUI();
        return v; //return view to use up to you.
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_list_menu,menu);

        MenuItem menuItem = menu.findItem(R.id.menu_item_show_subtitle);
        if(_subtitleVisible){
            menuItem.setIcon(R.drawable.closed_eyes);
            menuItem.setTitle(R.string.hide_subtitle);
        }else{
            menuItem.setIcon(R.drawable.opened_eye);
            menuItem.setTitle(R.string.show_subtitle);

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_new_crime :
                Crime crime = new Crime();
                CrimeLab.getInstance(getActivity()).addCrime(crime);//TODO: AddCrime()to Crime
                Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:
                _subtitleVisible=!_subtitleVisible;
                getActivity().invalidateOptionsMenu();

                updateSubtitle();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        int crimeCount = crimeLab.getCrime().size();
        //plurals
//        String subtitle = getString(R.string.subtitle_format , crimeCount);
            subtitle = getResources().getQuantityString(R.plurals.subtitle_format, crimeCount, crimeCount);

        if(crimeCount == 0){
            subtitle = "0 Crime";
        }
        if(!_subtitleVisible){
            subtitle = null;
        }
//        String.format("%s : %d","Hello",222);
        AppCompatActivity appCompatActivity= (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
    }


    @Override
    public void onSaveInstanceState(Bundle whatever) {
        super.onSaveInstanceState(whatever);
        whatever.putBoolean(SUBTITLE_VISIBLE_STATE,_subtitleVisible);
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
            _adapter.setCrimes(crimeLab.getCrime());
            _adapter.notifyDataSetChanged();
        }

        if(crimes.isEmpty()){
            showToAdd.setVisibility(View.VISIBLE);
        }else{
            showToAdd.setVisibility(View.GONE);
        }

        updateSubtitle();
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
                crimePos = (Integer[]) data.getExtras().get("position");
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
        private  Crime _crime;
        private ImageView imageView;
        private File fileImage;


        public CrimeHolder(View itemView) {
            super(itemView);
            _titleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_crime_title_text_view);
            _solvedCheckBox = (CheckBox)
                        itemView.findViewById(R.id.list_item_crime_solved_check_box);
//            _solvedCheckBox.setEnabled(false);
            _solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    _crime.setSolved(b);
                    CrimeLab.getInstance(getActivity()).updateCrime(_crime);
                }
            });
            _dateTextView =(TextView)
                        itemView.findViewById(R.id.list_item_crime_date_text_view);
            imageView = (ImageView) itemView.findViewById(R.id.image_list_view);

            itemView.setOnClickListener(this); //plus OnClickListener

        }

        public void updateImage(){
            if(fileImage == null||!fileImage.exists()){
                imageView.setImageDrawable(null);
            }else{
                Bitmap bitmap = PictureUtils.getScaledBitmap(fileImage.getPath(),getActivity());
                imageView.setImageBitmap(bitmap);
            }
        }

        public void bind(Crime crime,int position) {
            _crime = crime;
            _position = position;
            _titleTextView.setText(_crime.getTitle());
            _dateTextView.setText(_crime.getCrimeDate().toString());
            _solvedCheckBox.setChecked(_crime.isSolved());
            fileImage = CrimeLab.getInstance(getActivity()).getPhotoFile(_crime);
            updateImage();
        }

        /**
         * CrimeHolder
         * onclick Method
         */
        @Override
        public void onClick(View view) {
            Log.d(TAG, "Send position ; "+ _position);
            Intent intent = CrimePagerActivity.newIntent(getActivity(),_crime.getId());//Call Method newIntent and sent
            startActivityForResult(intent,REQUEST_UPDATED_CRIME);//Call Method of Fragment class
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> _crimes;
        private int viewCreatingCount;

        public  CrimeAdapter(List<Crime> crimes){this._crimes = crimes;} //ArrayList


        protected void setCrimes(List<Crime> crimes){
            _crimes = crimes;
        }

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
