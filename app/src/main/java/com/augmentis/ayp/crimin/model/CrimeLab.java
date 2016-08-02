package com.augmentis.ayp.crimin.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.augmentis.ayp.crimin.model.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Nutdanai on 7/18/2016.
 */
public class CrimeLab {
//    List<Crime> crimeList;
    private static CrimeLab instance;
    private static final String TAG = "CrimeLab";


    //////////////////////////////////////////// STATIC ZONE ////////////////////////////////////////////////
    public static CrimeLab getInstance(Context context) {
        if (instance == null) {
            instance = new CrimeLab(context);
        }
        return instance;
    }
    private static ContentValues getContentValues(Crime crime){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeTable.Cols.UUID,crime.getId().toString());
        contentValues.put(CrimeTable.Cols.TITLE,crime.getTitle());
        contentValues.put(CrimeTable.Cols.DATE,crime.getCrimeDate().getTime());
        contentValues.put(CrimeTable.Cols.SOLVED,(crime.isSolved())?1:0);
        contentValues.put(CrimeTable.Cols.SUSPECT,crime.getSuspect());
        return  contentValues;
    }

    //////////////////////////////////////////// ??? METHOD ??? ////////////////////////////////////////////////
    private Context context;
    private SQLiteDatabase database;

    public CrimeLab(Context context) {
        this.context = context.getApplicationContext(); //Do to same level
        CrimeBaseHelper crimeBaseHelper = new CrimeBaseHelper(this.context);
        database = crimeBaseHelper.getWritableDatabase();

//        if(crimeList==null){
//            crimeList = new ArrayList<>();
//        }
////        for (int i = 1; i <= 100; i++) {
////            Crime crime = new Crime();
////            crime.setTitle("Crime #" + i);
////            crime.setSolved(i % 2 == 0);
////            crimeList.add(crime);
////        }

    }


    public Crime getCrimeById(UUID uuid) {
//        for (Crime crime : crimeList) {
//            if (crime.getId().equals(uuid))
//                return crime;
//        }
        CrimeCursorWrapper cursor  = queryCrime(CrimeTable.Cols.UUID+ " = ? ",
                new String [] {uuid.toString()});
        try{
            if(cursor.getCount()== 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }
    }

//    public int getCrimePositionById(UUID uuid) {
//        int size = crimeList.size();
//        for (int i = 0; i < size; i++) {
//            if (crimeList.get(i).getId().equals(uuid)) {
//            return i;
//        }
//    }
//    return -1;
//}
    public CrimeCursorWrapper queryCrime(String whereCause, String[] whereArgs){

        Cursor cursor = database.query(CrimeTable.NAME,
                null,
                whereCause,
                whereArgs,
                null,
                null,
                null);
        return new CrimeCursorWrapper(cursor);
    }
    public List<Crime> getCrime(){
//        return this.crimeList;
        List<Crime> crimes= new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrime(null , null);
        try{
            cursor.moveToFirst();
                    while(!cursor.isAfterLast()){
                        crimes.add(cursor.getCrime());
                        cursor.moveToNext();
                    }
        }finally {
            cursor.close();
        }
        return crimes;
    }

//    public static void main(String [] args){
//        CrimeLab crimeLab = CrimeLab.getInstance(null);
//        List<Crime> crimeList = crimeLab.getCrime();
//        int size = crimeList.size();
//                for(int i=0;i<size;i++){
//                    System.out.println(crimeList.get(i));
//                }
//        System.out.println(crimeLab.toString());
//        System.out.println(crimeLab.getInstance());
//    }

    public void addCrime(Crime crime) {
//        crimeList.add(crime);
        Log.d(TAG,"Add Crimee " + crime.toString());
        ContentValues contentValues = getContentValues(crime);
        database.insert(CrimeTable.NAME, null , contentValues);
    }

    public void deleteCrime(UUID uuid){
        database.delete(CrimeTable.NAME,
                CrimeTable.Cols.UUID + " = ?",
                new String[] {uuid.toString()});
    }

    public void updateCrime(Crime crime){
        Log.d(TAG,"Update crime" +crime.toString());
        String uuidStr = crime.getId().toString();
        ContentValues contentValues = getContentValues(crime);
        database.update(CrimeTable.NAME , contentValues,
                CrimeTable.Cols.UUID + " = ? ",new String[]{uuidStr});

    }
}

