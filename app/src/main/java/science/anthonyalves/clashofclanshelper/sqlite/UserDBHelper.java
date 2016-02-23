package science.anthonyalves.clashofclanshelper.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UserDBHelper extends SQLiteOpenHelper {

    public static final String USER_DB_NAME = "userinfo.sqlite";
    public static int COC_DB_VERSION = 1;

    public static final String TABLE_BUILDINGS = "buildings";
    public static final String TABLE_TROOPS = "troops";

    public static final String COLUMN_ENTITY = "_entity";
    public static final String COLUMN_LEVEL = "level";


    Context mContext;

    public UserDBHelper(Context context) {
        super(context, USER_DB_NAME, null, COC_DB_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE \"" + TABLE_BUILDINGS + "\" (\n" +
                "\t`" + COLUMN_ENTITY + "`\tTEXT NOT NULL,\n" +
                "\t`" + COLUMN_LEVEL + "`\tINTEGER NOT NULL\n" +
                ")");
        db.execSQL("CREATE TABLE \"" + TABLE_TROOPS + "\" (\n" +
                "\t`" + COLUMN_ENTITY + "`\tTEXT NOT NULL,\n" +
                "\t`" + COLUMN_LEVEL + "`\tINTEGER NOT NULL\n" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TROOPS);
        onCreate(db);
    }

    private void copyDB() throws IOException {
        // Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(USER_DB_NAME);

        // Path to the just created empty db
        File outFileName = mContext.getDatabasePath(USER_DB_NAME);

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
