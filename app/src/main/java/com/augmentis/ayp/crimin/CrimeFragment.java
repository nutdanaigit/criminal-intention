package com.augmentis.ayp.crimin;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.CrimeLab;
import com.augmentis.ayp.crimin.model.PictureUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Nutdanai on 7/18/2016.
 */
public class CrimeFragment extends Fragment {
    private static final String CRIME_ID = "CrimeFragment.CRIME_ID";
    private static final String DILOG_DATE = "CrimeFragment.CRIME_DatePicker";
    private static final String DILOG_TIME = "CrimeFragment.CRIME_TimePicker";
    private static final String CRIME_POSITION = "CrimeFragment.CRIME_POS";
    private static final int REQUEST_DATE = 22;
    private static final int REQUEST_DATE2 = 23;
    private static final int REQUEST_CONTACT_SUSPECT = 25;

    private Crime crime;
    private EditText editText;
    private Button crimeDateButton;
    private Button crimeTimeButton;
    private Button crimeSuspectButton;
    private ImageButton crimeCallButton;
    //    private Button crimeDeleteButton;
    private CheckBox crimeSolvedCheckbox;
    private Button crimeReportButton;
    private int position;
    private String phoneNumber;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 12;
    private static final int REQUEST_CAPTURE_PHOTO = 875;
    private ImageView photoView;
    private ImageButton photoButton;
    private File photoFile;
    private static final int REQUEST_CODE_DIALOG = 5543;
    private static final String REQUEST_STRING_DIALOG = "Hello";
    private Callbacks callBacks;



    public CrimeFragment() {
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(CRIME_ID, crimeId);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    public UUID getCrimeId() {
        if (this.crime != null) {
            return  this.crime.getId();
        }
        return null;
    }

    //CallBack
    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
        void onCrimeDeleted();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callBacks = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callBacks = (Callbacks) context;
    }
    private void reloadCrimeFromDB(){

        UUID crimeId = (UUID) getArguments().getSerializable(CRIME_ID);
        crime = CrimeLab.getInstance(getActivity()).getCrimeById(crimeId);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        reloadCrimeFromDB();
        Log.d(CrimeListFragment.TAG, "crime.getTitle()=" + crime.getTitle());
        photoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(crime);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
//        CrimeLab.getInstance(getActivity()).getCrime();
        editText = (EditText) v.findViewById(R.id.crime_title);
        editText.setText(crime.getTitle());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int cout, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int after) {
                crime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                dialogFragment.show(fm, DILOG_TIME);
            }
        });

        crimeDateButton = (Button) v.findViewById(R.id.crime_date);
        crimeDateButton.setText(getFormattedDate(crime.getCrimeDate()));
        crimeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialogFragment = DatePickerFragment.newInstance(crime.getCrimeDate());
                dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);

                dialogFragment.show(fm, DILOG_DATE);
            }
        });

        crimeSolvedCheckbox = (CheckBox) v.findViewById(R.id.crime_solved);
        crimeSolvedCheckbox.setChecked(crime.isSolved());
        crimeSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                crime.isSolved();
                if(buttonView.isPressed()) {
                    crime.setSolved(isChecked);
                    updateCrime();
                    Log.d(CrimeListFragment.TAG, "Crime" + crime.toString());
                }
            }
        });

        crimeReportButton = (Button) v.findViewById(R.id.crime_report);
        crimeReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");//MIME Type
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }


        });

        crimeCallButton = (ImageButton) v.findViewById(R.id.imageBtn_call);
        crimeCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasCallPermission()){
                    callSuspect();
                }
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//        pickContact.addCategory(Intent.CATEGORY_HOME);
        crimeSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        crimeSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT_SUSPECT);
            }
        });


        if (crime.getSuspect() != null) {
            crimeSuspectButton.setText(crime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            crimeSuspectButton.setEnabled(false);
        }

        photoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        photoView = (ImageView) v.findViewById(R.id.crime_photo);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                CrimeDialogImage crimeDialogImage = CrimeDialogImage.newInstance(photoFile);
                crimeDialogImage.setTargetFragment(CrimeFragment.this,REQUEST_CAPTURE_PHOTO);
                crimeDialogImage.show(fm,REQUEST_STRING_DIALOG);
            }
        });

        //Call camera intent
        final Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // check if we can take photo
        boolean canTakePhoto = photoFile != null && captureImageIntent.resolveActivity(packageManager)!=null;
        if(canTakePhoto){
            Uri uri = Uri.fromFile(photoFile);
            captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        }
        //on click -> start activity for camera
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(captureImageIntent,REQUEST_CAPTURE_PHOTO);
            }
        });

        //update photo changing
        updatePhotoView();

        return v;
    }

    private String getFormattedtime(Date date) {
        return new SimpleDateFormat("HH : mm").format(date);
    }

    private String getFormattedDate(Date date) {
        return new SimpleDateFormat("dd MMMM yyyy").format(date);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callSuspect();

                } else {
                    Toast.makeText(getActivity(),"Deny Permissions" ,Toast.LENGTH_LONG);
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int result, Intent data) {
        if (result != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            //set
            crime.setCrimeDate(date);
            crimeDateButton.setText(getFormattedDate(crime.getCrimeDate()));
            updateCrime();
        }
        if (requestCode == REQUEST_DATE2) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_DATE2);

            //set
            crime.setCrimeDate(date);
            crimeTimeButton.setText(getFormattedtime(crime.getCrimeDate()));
            updateCrime();
        }

        if (requestCode == REQUEST_CONTACT_SUSPECT) {
            if (data != null) {
                Uri contactUri = data.getData();
                String[] queryFields = new String[]{
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor c = getActivity()
                        .getContentResolver()
                        .query(contactUri,
                                queryFields,
                                null,
                                null,
                                null);
                try {
                    if (c.getCount() == 0) {
                        return;
                    }
                    c.moveToFirst();
                    String suspect = c.getString(0);
                    suspect = suspect + ": " + c.getString(1);
                    phoneNumber = c.getString(1);
//                    String suspect = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    crime.setSuspect(suspect);
                    crimeSuspectButton.setText(suspect);
                    updateCrime();
                } finally {
                    c.close();
                }
            }
        }

        if(requestCode == REQUEST_CAPTURE_PHOTO){
            updatePhotoView();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.getInstance(getActivity()).deleteCrime(crime.getId());
                callBacks.onCrimeDeleted();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private boolean hasCallPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return true;
        }
        return false;
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (crime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE,MMM dd";
        String dateString = DateFormat.format(dateFormat, crime.getCrimeDate()).toString();
        String suspect = crime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_with_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                crime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    public void updatePhotoView(){

        if(photoFile == null||!photoFile.exists()){
            photoView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(),getActivity());
            photoView.setImageBitmap(bitmap);

        }
    }
    public void updateUI(){
        reloadCrimeFromDB();
        crimeSolvedCheckbox.setChecked(crime.isSolved());
    }

    private void callSuspect() {
        Log.d("pearll", "" + phoneNumber);
        Intent c = new Intent(Intent.ACTION_CALL);
        c.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(c);
    }
    private void updateCrime(){
        CrimeLab.getInstance(getActivity()).updateCrime(crime);
        callBacks.onCrimeUpdated(crime);
    }
}
