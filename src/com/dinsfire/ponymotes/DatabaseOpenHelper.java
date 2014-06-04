package com.dinsfire.ponymotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_EMOTES = "mobileEmotes";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_EMOTE_NAME = "emoteName";
	public static final String COLUMN_TAGS = "tags";
	public static final String COLUMN_NSFW = "isNSFW";
	public static final String COLUMN_DATE_MODIFIED = "dateModified";
	public static final String COLUMN_SOURCE = "source";
	
	public static final String EMOTE_INDEX = "emoteIndex";
	public static final String TAGS_INDEX = "tagsIndex";
	public static final String SOURCE_INDEX = "sourceIndex";
	
	private static final String DATABASE_NAME = "clientEmotes.db";
	private static final int DATABASE_VERSION = 2;
	
	public static final String SERVER_DATABASE_NAME = "mobileDatabase.db";

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_EMOTES + "(" 
			+ COLUMN_EMOTE_NAME + " text primary key not null,"
			+ COLUMN_TAGS + " text not null,"
			+ COLUMN_NSFW + " integer not null,"
			+ COLUMN_DATE_MODIFIED + " integer not null,"
			+ COLUMN_SOURCE + " text not null"
			+ ");";
	
	private static final String INDEX_EMOTE = "CREATE INDEX " 
			+ EMOTE_INDEX + " ON " + TABLE_EMOTES 
			+ " (" + COLUMN_EMOTE_NAME + ");";
	
	private static final String INDEX_TAGS = "CREATE INDEX " 
			+ TAGS_INDEX + " ON " + TABLE_EMOTES 
			+ " (" + COLUMN_TAGS + "," + COLUMN_EMOTE_NAME + ");";
	
	private static final String INDEX_SOURCE = "CREATE INDEX " 
			+ SOURCE_INDEX + " ON " + TABLE_EMOTES 
			+ " (" + COLUMN_SOURCE + "," + COLUMN_EMOTE_NAME + ");";

	public DatabaseOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	public DatabaseOpenHelper(Context context, String name, int version) {
		super(context, name, null, version);
		// TODO Auto-generated constructor stub
	}
	
	public DatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		db.execSQL(INDEX_EMOTE);
		db.execSQL(INDEX_TAGS);
		db.execSQL(INDEX_SOURCE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DatabaseOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMOTES);
        onCreate(db);
		
	}

}
