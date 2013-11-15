package com.diegofdez.shoplist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by diegofdez on 04/11/13.
 */
public class DataBaseHelper {
    private Context mCtx = null;
    private DataBaseHelperInternal mDbHelper = null;
    private SQLiteDatabase mDb = null;

    private static final String DATABASE_NAME = "SHOPLIST";
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_TABLE_SHOPLIST = "shoplist";
    public static final String SL_ID = "_id";
    public static final String SL_ITEM = "item";
    public static final String SL_PLACE = "place";
    public static final String SL_IMPORTANCE = "importance";
    public static final String SL_PRICE = "price";

    // Crear tabla
    private static final String DATABASE_CREATE_SHOPLIST = "create table " + DATABASE_TABLE_SHOPLIST
            + " (" + SL_ID + " integer primary key, " + SL_ITEM + " text not null, " + SL_PLACE
            + " text not null, " + SL_IMPORTANCE + " integer not null, " + SL_PRICE
            + " float not null)";

    private static class DataBaseHelperInternal extends SQLiteOpenHelper {

        public DataBaseHelperInternal(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            deleteTables(db);
            createTables(db);
        }

        private void createTables(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_SHOPLIST);
        }

        private void deleteTables(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_SHOPLIST);
        }
    }

    public DataBaseHelper(Context ctx) {
        mCtx = ctx;
    }

    public DataBaseHelper open() throws SQLException {
        mDbHelper = new DataBaseHelperInternal(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public Cursor getItem(long itemId) {
        return mDb.rawQuery(" select " + SL_ITEM + ", " + SL_PLACE + ", " + SL_PRICE
                + ", " + SL_IMPORTANCE + ", " + SL_ID
                + " from " + DATABASE_TABLE_SHOPLIST
                + " where " + SL_ID + "= ?",
                new String[] {Long.toString(itemId)});
    }

    public Cursor getItems() {
        return mDb.query(DATABASE_TABLE_SHOPLIST,
                new String[] {SL_ID, SL_ITEM, SL_PLACE, SL_IMPORTANCE}, null, null, null, null,
                SL_IMPORTANCE);
    }

    public long insertItem(String item, String place, float price, int importance) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(SL_IMPORTANCE, importance);
        initialValues.put(SL_ITEM, item);
        initialValues.put(SL_PLACE, place);
        initialValues.put(SL_PRICE, price);
        return mDb.insert(DATABASE_TABLE_SHOPLIST, null, initialValues);
    }

    public int delete(long lastRowSelected) {
        return mDb.delete(DATABASE_TABLE_SHOPLIST, SL_ID + "=?",
                new String[] {Long.toString(lastRowSelected)});
    }

    public int updateItem(long ident, String item, String place, float price, int importance) {
        ContentValues cv = new ContentValues();
        cv.put(SL_ITEM, item);
        cv.put(SL_PLACE, place);
        cv.put(SL_PRICE, price);
        cv.put(SL_IMPORTANCE, importance);
        return mDb.update(DATABASE_TABLE_SHOPLIST, cv, SL_ID + "=?",
                new String[] {Long.toString(ident)});
    }
}
