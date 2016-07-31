package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Nutdanai on 7/28/2016.
 */
public class TimePickerFragment extends DialogFragment implements DialogInterface.OnClickListener {
    protected static final String EXTRA_DATE2 ="extra_date" ;
    private Date date;
    protected Calendar tt;

    public static TimePickerFragment newInstance(Date date) {
        TimePickerFragment df = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable("ARG_TIME", date);
        df.setArguments(args);
        return df;
    }

    TimePicker _timePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        date = (Date) getArguments().getSerializable("ARG_TIME");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dailog_time, null);
        _timePicker = (TimePicker) v.findViewById(R.id.time_picker_in_dialog);
        _timePicker.setHour(hour);
        _timePicker.setMinute(minute);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.time_picker_title);
        builder.setPositiveButton(android.R.string.ok, this);
//        builder.setNeutralButton();
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        int minute = _timePicker.getMinute();
        int hour = _timePicker.getHour();

        tt = Calendar.getInstance();
        tt.setTime(date);
        tt.set(Calendar.HOUR,hour);
        tt.set(Calendar.MINUTE,minute);

        sendResult(Activity.RESULT_OK, tt.getTime());
    }
    private void sendResult(int resultCode,Date date)    {
        if(getTargetFragment()==null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE2,date);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);

    }
}
