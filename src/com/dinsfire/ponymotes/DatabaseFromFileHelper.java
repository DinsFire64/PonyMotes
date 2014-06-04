package com.dinsfire.ponymotes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
 
public class DatabaseFromFileHelper {
 
    private String dbName;
    private Context context;
 
    public DatabaseFromFileHelper(Context context, String dbName) {
        this.context = context;
        this.dbName = dbName;
    }
 
    public SQLiteDatabase openDatabase() {
        File dbFile = context.getDatabasePath(dbName);
 
        if (!dbFile.exists()) {
            try {
                copyDatabase(dbFile);
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }
 
        return SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }
 
    private void copyDatabase(File dbFile) throws IOException {
    	FileInputStream is = new FileInputStream(context.getCacheDir().toString() + "/" + dbName);
        OutputStream os = new FileOutputStream(dbFile);
 
        byte[] buffer = new byte[1024];
        while (is.read(buffer) > 0) {
            os.write(buffer);
        }
 
        os.flush();
        os.close();
        is.close();
    }
 
}
