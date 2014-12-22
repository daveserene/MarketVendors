package com.shop.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_VENDORNAME = "vendorname";
	public static final String KEY_MARKET = "market";
	public static final String KEY_COMMODITY = "commodity";
	public static final String KEY_PRICE = "price";
	public static final String KEY_UNIT = "unit";
	public static final String KEY_SUBMIT = "submit";
	
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "MarketItems";
	private static final String DATABASE_TABLE = "Commodity";
	private static final String DATABASE_TABLE_2 = "server";
	private static final int DATABASE_VERSION = 13;
	
	private static final String CREATE_TABLE =
			"create table Commodity (_id integer primary key autoincrement, "
					+ "vendorname text not null, market text not null, " +
					"commodity text not null unique, " +
					"price text not null, " +
					"unit text not null);";
	
	private static final String CREATE_TABLE_2 =
			"create table server (_id integer primary key autoincrement, "
					+ "vendorname text not null, market text not null, " +
					"commodity text not null, " +
					"price text not null, " +
					"unit text not null );";
	
	private final Context context;
	private static DatabaseHelper DBHelper;
	private static SQLiteDatabase db;
	public DBAdapter(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			try {
				db.execSQL(CREATE_TABLE);
				db.execSQL(CREATE_TABLE_2);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			
			db.execSQL("DROP TABLE IF EXISTS Commodity");
			db.execSQL("DROP TABLE IF EXISTS server");
			onCreate(db);
		}
	}
		//---opens the database---
		public DBAdapter open() throws SQLException
		{
			db = DBHelper.getWritableDatabase();
			return this;
		}
		//---closes the database---
		public void close()
		{
			DBHelper.close();
		}
		
		//---insert a commodity into the database---
		public long insertItem(String table, String vendor, String market, String commodity, String price, String unit)
		{
			
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_VENDORNAME, vendor);
			initialValues.put(KEY_MARKET, market);
			initialValues.put(KEY_COMMODITY, commodity);
			initialValues.put(KEY_PRICE , price);
			initialValues.put(KEY_UNIT , unit);
			
			return db.insertOrThrow(table, null, initialValues);
		}
		
		//---deletes a particular commodity---
		public boolean deleteItem(String table, long rowId)
		{
			return db.delete(table, KEY_ROWID + "=" + rowId, null) > 0;
		}
		//---retrieves all the commodities---
		public Cursor getAllItems(String table)
		{
			
			return db.query(table, new String[] {KEY_ROWID,
					KEY_VENDORNAME, KEY_MARKET, KEY_COMMODITY, KEY_PRICE, KEY_UNIT}, null, null, null, null, null);
		}
		
		//---retrieves a particular commodity---
		public Cursor getItem(long rowId) throws SQLException
		{
			Cursor mCursor =
					db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
							KEY_VENDORNAME, KEY_MARKET, KEY_COMMODITY, KEY_PRICE, KEY_UNIT}, KEY_ROWID + "=" + rowId, null,
							null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			}
			return mCursor;
		}
		/*
		//---updates a contact---
		public boolean updateContact(long rowId, String name, String email)
		{
			ContentValues args = new ContentValues();
			args.put(KEY_NAME, name);
			args.put(KEY_EMAIL, email);
			return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
		}*/
		}
