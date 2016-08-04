package com.augmentis.ayp.crimin;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.augmentis.ayp.crimin.model.PictureUtils;

import java.io.File;

/**
 * Created by Nutdanai on 8/4/2016.
 */
public class CrimeDialogImage extends DialogFragment {
        private File file;
        private ImageView imageview;
        public static CrimeDialogImage newInstance(File file){
            CrimeDialogImage cd = new CrimeDialogImage();
            Bundle args = new Bundle();
            args.putSerializable("dd",file);
            cd.setArguments(args);
            return cd;
        }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        file = (File) getArguments().getSerializable("dd");

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.crime_dialog_image , null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        imageview =(ImageView) v.findViewById(R.id.im_show);
        if(file == null||!file.exists()){
            imageview.setImageDrawable(null);
        }else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(file.getPath(), getActivity());
            imageview.setImageBitmap(bitmap);
        }
        builder.setView(v);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.cancel();

            }
        });


        return builder.create();

    }
}
