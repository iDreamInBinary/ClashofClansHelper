package science.anthonyalves.clashofclanshelper.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.ArrayMap;

import java.util.ArrayList;

public class ChildrenDAO {

    private final Context mContext;
    UserDBHelper mUserDbHelper;
    SQLiteDatabase db;


    public ChildrenDAO(Context context) {
        mContext = context;
        mUserDbHelper = new UserDBHelper(mContext);
        db = mUserDbHelper.getWritableDatabase();
    }

    private void openDb() {
        if (!db.isOpen()) {
            db = mUserDbHelper.getReadableDatabase();
        }
    }

    public ArrayMap<String, ArrayList<Integer>> getAll() {

        // SELECT th8 FROM MaxLevel where _building = 'building'
        Cursor cursor = db.query(UserDBHelper.TABLE_BUILDINGS, null, null, null, null, null, null, null);

        ArrayMap<String, ArrayList<Integer>> map = new ArrayMap<>(cursor.getCount());
        while (cursor.moveToNext()) {
            String building = cursor.getString(cursor.getColumnIndex(UserDBHelper.COLUMN_ENTITY));
            if (!map.containsKey(building)) {
                map.put(building, new ArrayList<Integer>());
            }

            int value = cursor.getInt(cursor.getColumnIndex(UserDBHelper.COLUMN_LEVEL));
            map.get(building).add(value);
        }

        db.close();
        cursor.close();
        return map;
    }

    public int[] getChildrenLevels(String building) {

        openDb();


        String[] selectArgs = {building};
        String[] columns = {UserDBHelper.COLUMN_LEVEL};

        // SELECT th8 FROM MaxLevel where _building = 'building'
        Cursor cursor = db.query(UserDBHelper.TABLE_BUILDINGS, columns, UserDBHelper.COLUMN_ENTITY + " = ?", selectArgs, null, null, null, null);

        int[] values = new int[cursor.getCount()];
        int i = 0;
        while (cursor.moveToNext()) {
            values[i] = cursor.getInt(cursor.getColumnIndex(UserDBHelper.COLUMN_LEVEL));
            i++;
        }

        db.close();
        cursor.close();
        return values;
    }

    public boolean addChild(String building, int level) {
        if (!db.isOpen()) {
            db = mUserDbHelper.getWritableDatabase();
        }

        ContentValues insertValues = new ContentValues();
        insertValues.put(UserDBHelper.COLUMN_ENTITY, building);
        insertValues.put(UserDBHelper.COLUMN_LEVEL, level);

        /**
         * insert Returns the row ID of the newly inserted row, or -1 if an error occurred
         */
        long status = db.insert(UserDBHelper.TABLE_BUILDINGS, null, insertValues);
        db.close();

        return (status >= 0);
    }

    public boolean upgradeChild(String buildingCode, int level) {
        openDb();

        ContentValues insertValues = new ContentValues();
        insertValues.put(UserDBHelper.COLUMN_ENTITY, buildingCode);
        insertValues.put(UserDBHelper.COLUMN_LEVEL, level+1);

        int status = db.update(UserDBHelper.TABLE_BUILDINGS, insertValues, UserDBHelper.COLUMN_ENTITY + " = ? AND " + UserDBHelper.COLUMN_LEVEL + " = ?", new String[]{buildingCode, String.valueOf(level)});
        return (status > 0);
    }
}
