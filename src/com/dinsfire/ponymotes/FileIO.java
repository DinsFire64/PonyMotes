package com.dinsfire.ponymotes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import android.os.Environment;
import android.util.Log;

public class FileIO {
    static final public String FULL_EMOTE_PATH = Environment.getExternalStorageDirectory().toString()
	    + "/RedditEmotes/";

    // folder does NOT need to have the preceding slash
    // folder is the folder starting from /sdcard/<folder>
    public ArrayList<File> getListOfFiles(String folder) {

	ArrayList<File> folderListing = null;
	try {
	    if (isExternalStorageReadable()) {
		File f = new File(Environment.getExternalStorageDirectory().toString() + "/" + folder);

		folderListing = new ArrayList<File>(Arrays.asList(f.listFiles()));
	    }
	} catch (Exception e) {
	    // TODO Error detection. Yeah...
	}

	return folderListing;

    }

    public ArrayList<File> getListOfEmotes() {

	ArrayList<File> folderListing = null;
	try {
	    if (isExternalStorageReadable()) {
		File f = new File(FULL_EMOTE_PATH);

		folderListing = new ArrayList<File>(Arrays.asList(f.listFiles()));
	    }
	} catch (Exception e) {
	    // TODO Error detection. Yeah...
	}

	return folderListing;

    }

    public static void doesDirectoryExistIfNotMakeIt() {
	File theDir = new File(FULL_EMOTE_PATH);
	File nomediaFile = new File(FULL_EMOTE_PATH, ".nomedia");

	if (!theDir.exists()) {
	    theDir.mkdir();
	}

	if (!nomediaFile.exists()) {
	    try {
		nomediaFile.createNewFile();
	    } catch (IOException e) {
		Log.e("nomediafile", e.toString());
	    }
	}

    }

    static public String getEmotePath(String emoteName) {
	return FULL_EMOTE_PATH + emoteName + ".png";
    }

    static public File getEmoteByName(String emoteName) {

	try {
	    if (isExternalStorageReadable()) {
		File f = new File(FULL_EMOTE_PATH + emoteName + ".png");

		return f;
	    }
	} catch (Exception e) {
	    // TODO Error detection. Yeah...
	}

	return null;

    }

    static public Boolean emoteExistsInStorage(String emoteName) {

	try {
	    if (isExternalStorageReadable()) {
		File f = new File(FULL_EMOTE_PATH + emoteName + ".png");

		return f.exists();
	    }
	} catch (Exception e) {
	    // TODO Error detection. Yeah...
	}

	return false;

    }

    static public Boolean deleteEmote(String emoteName) {

	try {
	    if (isExternalStorageReadable()) {
		File f = new File(FULL_EMOTE_PATH + emoteName + ".png");
		return f.delete();
	    }
	} catch (Exception e) {
	    // TODO Error detection. Yeah...
	}

	return false;

    }

    public static String copyTempEmote(String emoteName) {

	String fullPath = null;

	try {
	    fullPath = FULL_EMOTE_PATH + "temp.png";
	    InputStream in = new FileInputStream(FULL_EMOTE_PATH + emoteName + ".png");
	    OutputStream out = new FileOutputStream(fullPath);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
		out.write(buf, 0, len);
	    }

	    in.close();
	    out.flush();
	    out.close();

	    Log.i("shareDebug", "fullPath: " + fullPath);
	    Log.i("shareDebug", "Temp created!");

	} catch (IOException e) {
	    Log.e("tempEmpte", e.toString());
	}

	return fullPath;
    }

    // Taken from Android Examples
    // Checks if external storage is available for read and write
    static public boolean isExternalStorageWritable() {
	String state = Environment.getExternalStorageState();
	if (Environment.MEDIA_MOUNTED.equals(state)) {
	    return true;
	}
	return false;
    }

    // /Checks if external storage is available to at least read
    static public boolean isExternalStorageReadable() {
	String state = Environment.getExternalStorageState();
	if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	    return true;
	}
	return false;
    }
}
