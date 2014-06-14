package com.dinsfire.ponymotes;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DatabaseController {

	private SQLiteDatabase emoteDatabase;
	private DatabaseOpenHelper dbHelper;
	private DatabaseFromFileHelper dbFromFileHelper;

	public DatabaseController(Context context) {
		dbHelper = new DatabaseOpenHelper(context);
	}

	public DatabaseController(Context context, String name, int version) {
		dbHelper = new DatabaseOpenHelper(context, name, version);
	}

	public DatabaseController(Context context, String fileName) {
		dbFromFileHelper = new DatabaseFromFileHelper(context, fileName);
		emoteDatabase = dbFromFileHelper.openDatabase();
	}

	public void open() throws SQLException {
		emoteDatabase = dbHelper.getWritableDatabase();
	}

	public void close() {
		emoteDatabase.close();
	}

	public SQLiteDatabase getSQL() {
		return emoteDatabase;
	}

	public void addEmote(String emoteName, String tags, int isNSFW,
			String dateModified, String source) {

		ContentValues values = new ContentValues();

		values.put(DatabaseOpenHelper.COLUMN_EMOTE_NAME, emoteName);
		values.put(DatabaseOpenHelper.COLUMN_TAGS, tags);
		values.put(DatabaseOpenHelper.COLUMN_NSFW, isNSFW);
		values.put(DatabaseOpenHelper.COLUMN_DATE_MODIFIED, dateModified);
		values.put(DatabaseOpenHelper.COLUMN_SOURCE, source);

		emoteDatabase.insert(DatabaseOpenHelper.TABLE_EMOTES, null, values);
		// System.out.println("Added: " + String.valueOf(insertId));
	}

	public void batchAddEmotes(ArrayList<Emote> toBeAdded) {

		if (toBeAdded.size() > 0) {
			emoteDatabase.beginTransaction();
			String sql = "INSERT INTO " + DatabaseOpenHelper.TABLE_EMOTES
					+ "  VALUES(?,?,?,?,?)";
			SQLiteStatement insert = emoteDatabase.compileStatement(sql);

			try {

				for (Emote emote : toBeAdded) {
					insert.bindString(1, emote.getEmoteName());
					insert.bindString(2, emote.getTags());
					insert.bindLong(3, emote.isIntNSFW());
					insert.bindLong(4, emote.getDateModified());
					insert.bindString(5, emote.getSource());
					// System.out.println(insert.toString());
					insert.execute();
				}

				emoteDatabase.setTransactionSuccessful();

			} catch (Exception e) {
				Log.e("batchAdd", e.toString());
			} finally {
				emoteDatabase.endTransaction();
			}
		}
	}

	public void batchDeleteEmotes(ArrayList<?> toBeDeleted) {

		if (toBeDeleted.size() > 0) {
			emoteDatabase.beginTransaction();
			String sql = "DELETE FROM " + DatabaseOpenHelper.TABLE_EMOTES
					+ " WHERE emoteName = (?)";
			SQLiteStatement insert = emoteDatabase.compileStatement(sql);

			try {

				for (int i = 0; i < toBeDeleted.size(); i++) {
					insert.bindString(1, toBeDeleted.get(i).toString());
					insert.execute();
				}

				emoteDatabase.setTransactionSuccessful();

			} catch (Exception e) {
				Log.e("batchDelete", e.toString());
			} finally {
				emoteDatabase.endTransaction();
			}
		}

	}

	public ArrayList<String> searchFor(String searchTerm, String limit) {

		ArrayList<String> result = new ArrayList<String>();

		String[] splited = searchTerm.split("\\s+");
		String query = "";

		// TODO This is ugly, find a better way
		for (int i = 0; i < splited.length; i++) {
			if (splited[i].startsWith("+")) {

				query += DatabaseOpenHelper.COLUMN_TAGS + " LIKE ?";
				splited[i] = splited[i].replace("+", "");

			} else if (splited[i].startsWith("r/")) {

				query += DatabaseOpenHelper.COLUMN_SOURCE + " LIKE ?";
				splited[i] = splited[i].replace("r/", "");

			} else if (splited[i].startsWith("sr:")) {

				query += DatabaseOpenHelper.COLUMN_SOURCE + " LIKE ?";
				splited[i] = splited[i].replace("sr:", "");

			} else if (splited[i].startsWith("-")) {

				if (splited[i].startsWith("-+")) {
					query += DatabaseOpenHelper.COLUMN_TAGS + " NOT LIKE ?";
					splited[i] = splited[i].replace("-+", "");
				} else {
					query += DatabaseOpenHelper.COLUMN_EMOTE_NAME
							+ " NOT LIKE ?";
					splited[i] = splited[i].replace("-", "");
				}

			} else
				query += DatabaseOpenHelper.COLUMN_EMOTE_NAME + " LIKE ?";

			splited[i] = "%" + splited[i] + "%";

			if (i != splited.length - 1)
				query += " AND ";
		}

		// Cursor cursor = emoteDatabase.rawQuery(query, splited);

		if (limit.equals("0")) {
			limit = null;
		}

		Cursor cursor = emoteDatabase.query(DatabaseOpenHelper.TABLE_EMOTES,
				new String[] { DatabaseOpenHelper.COLUMN_EMOTE_NAME }, query,
				splited, null, null, DatabaseOpenHelper.COLUMN_EMOTE_NAME,
				limit);

		cursor.moveToFirst();

		while (cursor.isAfterLast() == false) {
			result.add(cursor.getString(cursor
					.getColumnIndex(DatabaseOpenHelper.COLUMN_EMOTE_NAME)));
			cursor.moveToNext();
		}

		cursor.close();

		return result;
	}

	public Cursor getInfoForEmote(String emoteName) {

		String columns[] = { DatabaseOpenHelper.COLUMN_EMOTE_NAME,
				DatabaseOpenHelper.COLUMN_DATE_MODIFIED };
		String where = DatabaseOpenHelper.COLUMN_EMOTE_NAME + " = ?";
		String[] arguments = { emoteName };

		Cursor cursor = emoteDatabase.query(DatabaseOpenHelper.TABLE_EMOTES,
				columns, where, arguments, null, null, null);

		cursor.moveToFirst();

		return cursor;
	}

	public int getNumberOfRows() {

		Cursor cursor = emoteDatabase.rawQuery("SELECT * FROM "
				+ DatabaseOpenHelper.TABLE_EMOTES, null);

		cursor.moveToFirst();

		int numOfRows = cursor.getCount();

		cursor.close();

		return numOfRows;
	}

	public Cursor getAllRows() {

		Cursor cursor = emoteDatabase.rawQuery("SELECT * FROM "
				+ DatabaseOpenHelper.TABLE_EMOTES, null);

		cursor.moveToFirst();

		return cursor;
	}

	// public ArrayList<Emote> getDifferences(DatabaseController localDB) {
	public ArrayList<String> getDifferences(boolean downloadEmotes,
			boolean downloadMissingEmotes, boolean includeNSFW,
			DatabaseController localDB) {

		Cursor newCursor = getAllRows();

		ArrayList<Emote> newEmotes = new ArrayList<Emote>(newCursor.getCount());
		ArrayList<Emote> toDownload = new ArrayList<Emote>(newCursor.getCount());
		ArrayList<String> toDelete = new ArrayList<String>(newCursor.getCount());

		Log.i("database", "Starting the comparison");

		while (newCursor.isAfterLast() == false) {
			String emoteName = newCursor.getString(newCursor
					.getColumnIndex(DatabaseOpenHelper.COLUMN_EMOTE_NAME));
			int dateModified = newCursor.getInt(newCursor
					.getColumnIndex(DatabaseOpenHelper.COLUMN_DATE_MODIFIED));
			String tags = newCursor.getString(newCursor
					.getColumnIndex(DatabaseOpenHelper.COLUMN_TAGS));
			int isNSFW = newCursor.getInt(newCursor
					.getColumnIndex(DatabaseOpenHelper.COLUMN_NSFW));
			String source = newCursor.getString(newCursor
					.getColumnIndex(DatabaseOpenHelper.COLUMN_SOURCE));

			Emote newEmote = new Emote(emoteName, tags, isNSFW, dateModified,
					source);

			if (!newEmote.isNSFW() || includeNSFW) {

				Cursor oldCursor = localDB.getInfoForEmote(emoteName);

				// Check to see if the emote exists in old table
				if (oldCursor.getCount() > 0) {
					// String oldEmoteName =
					// oldCursor.getString(oldCursor.getColumnIndex(DatabaseOpenHelper.COLUMN_EMOTE_NAME));
					int oldDateModified = oldCursor
							.getInt(oldCursor
									.getColumnIndex(DatabaseOpenHelper.COLUMN_DATE_MODIFIED));
					// System.out.println("Checking to see if " + oldEmoteName +
					// " needs updating by comparing " + oldDateModified +
					// " to " + dateModified);

					if (dateModified > oldDateModified) {
						// System.out.println("Old version of: " + emoteName);

						newEmotes.add(newEmote);
					} else {
						// The file is in the table and up to date

						// Check to see if emote is missing and to download it
						if (downloadMissingEmotes) {
							if (!FileIO.emoteExistsInStorage(emoteName)) {
								// Log.i("database", "Downloading missing: " +
								// emoteName);
								toDownload.add(newEmote);
							}
						}
					}
				} else {
					// Since the emote isn't in the table, add to the database
					// and download

					// System.out.println("Not in local database: " +
					// emoteName);
					// localDB.addEmote(emoteName, tags, dateModified, source);

					newEmotes.add(newEmote);
				}

				oldCursor.close();

			} else {

				toDelete.add(emoteName);
				if (FileIO.emoteExistsInStorage(emoteName)) {
					FileIO.deleteEmote(emoteName);
					Log.i("database", "Deleting: " + emoteName);
				}
			}

			newCursor.moveToNext();
		}

		newCursor.close();

		Log.i("database", "Adding to database " + newEmotes.size() + " emotes.");

		localDB.batchDeleteEmotes(newEmotes);
		localDB.batchDeleteEmotes(toDelete);
		localDB.batchAddEmotes(newEmotes);

		ArrayList<String> stringNewEmotes = new ArrayList<String>(
				newEmotes.size());
		ArrayList<String> stringToDownload = new ArrayList<String>(
				toDownload.size());

		for (int i = 0; i < newEmotes.size(); i++)
			stringNewEmotes.add(newEmotes.get(i).getEmoteName());

		for (int i = 0; i < toDownload.size(); i++)
			stringToDownload.add(toDownload.get(i).getEmoteName());

		// TODO: update this code. Implement the check somewhere else.
		// SOMETHING.
		if (downloadEmotes) {
			if (downloadMissingEmotes)
				stringNewEmotes.addAll(stringToDownload);

			Log.i("database", "Need to download: " + newEmotes.size()
					+ " out of " + String.valueOf(getNumberOfRows()));
			return stringNewEmotes;
		} else if (downloadMissingEmotes) {
			Log.i("database", "Need to download: " + newEmotes.size()
					+ " out of " + String.valueOf(getNumberOfRows()));
			return stringToDownload;
		} else
			return new ArrayList<String>();

	}

}
