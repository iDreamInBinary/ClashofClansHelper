package science.anthonyalves.clashofclanshelper.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClashDBHelper extends SQLiteOpenHelper {

    public static final String COC_DB_NAME = "cochelper.sqlite";
    public static int COC_DB_VERSION = 1;

    public static final String TABLE_MAX_AMOUNT = "MaxAmount";
    public static final String TABLE_MAX_LEVEL = "MaxLevel";
    public static final String TABLE_PICTURES = "Pictures";
    public static final String TABLE_TROOPS = "Troops";


    public static final String COLUMN_TH_PREFIX = "th";
    public static final String COLUMN_BUILDING = "_building";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_LAB_LEVEL = "lab_level";


    Context mContext;

    public ClashDBHelper(Context context) {
        super(context, COC_DB_NAME, null, COC_DB_VERSION);
        mContext = context;

        // check if we have loaded the database
        SQLiteDatabase checkDB = null;

        try {
            String myPath = String.valueOf(mContext.getDatabasePath(COC_DB_NAME));
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            log("gg");
        }

        if (checkDB == null) {
            // copy the db
            getReadableDatabase();
            try {
                copyDB();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (checkDB != null)
            checkDB.close();


    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // static db

        File dbFile = new File(String.valueOf(mContext.getDatabasePath(COC_DB_NAME)));

        if (dbFile.exists()) {
            dbFile.delete();
        }

        COC_DB_VERSION = newVersion;
        onCreate(db);

    }

    private boolean isInDBFolder() {
        String dbPath = String.valueOf(mContext.getDatabasePath(COC_DB_NAME));

        File dbFile = new File(dbPath);
        if (dbFile.exists()) {
            return true;
        }
        return false;
    }

    private void copyDB() throws IOException {
        // Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(COC_DB_NAME);

        // Path to the just created empty db
        File outFileName = mContext.getDatabasePath(COC_DB_NAME);

        // Open the empty db as the output stream
        FileOutputStream myOutput = new FileOutputStream(outFileName);
        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void log(String s) {
        Log.d(getClass().getSimpleName(), s);
    }
}
