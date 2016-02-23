package science.anthonyalves.clashofclanshelper.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.ArrayMap;

public class MaxDAO {

    private final Context mContext;
    ClashDBHelper mClashDbHelper;
    SQLiteDatabase db;


    public MaxDAO(Context context) {
        mContext = context;
        mClashDbHelper = new ClashDBHelper(mContext);
        db = mClashDbHelper.getReadableDatabase();

    }


    private int getBuildingMaxBlankAtTH(String building, int thLevel, String table) {

        openDb();

        String thColumn = ClashDBHelper.COLUMN_TH_PREFIX + thLevel;
        String[] selectArgs = {building};
        String[] columns = {thColumn};

        // SELECT th8 FROM MaxLevel where _building = 'building'
        Cursor cursor = db.query(table, columns, ClashDBHelper.COLUMN_BUILDING + " = ?", selectArgs, null, null, null, "1");

        int max = -1;

        if (cursor.moveToNext()) {
            max = cursor.getInt(cursor.getColumnIndex(thColumn));
        }

        db.close();
        cursor.close();
        return max;
    }

    private int[] getBuildingAllMaxBlank(String building, String table) {

        openDb();

        String[] selectArgs = {building};

        // SELECT th8 FROM MaxLevel where _building = 'building'
        Cursor cursor = db.query(table, null, ClashDBHelper.COLUMN_BUILDING + " = ?", selectArgs, null, null, null, "1");

        int[] values = null;
        if (cursor.moveToNext()) {
            values = new int[cursor.getColumnCount() - 1];
            for (int i = 0; i < values.length; i++) {
                values[i] = cursor.getInt(i + 1);
            }
        }

        db.close();
        cursor.close();
        return values;

    }

    private ArrayMap<String, int[]> getAllMaxBlank(String table) {

        openDb();

        // SELECT th8 FROM MaxLevel where _building = 'building'
        Cursor cursor = db.query(table, null, null, null, null, null, null, null);

        ArrayMap<String, int[]> map = new ArrayMap<>(cursor.getCount());

        while (cursor.moveToNext()) {
            int[] values = new int[cursor.getColumnCount() - 1];
            for (int i = 0; i < values.length; i++) {
                String columnName = ClashDBHelper.COLUMN_TH_PREFIX + (i + 1);
                int index = cursor.getColumnIndex(columnName);
                values[i] = cursor.getInt(index);
            }
            map.put(cursor.getString(0), values);
        }

        if (cursor.getCount() <= 0) {
            map = null;
        }

        db.close();
        cursor.close();
        return map;
    }

    private ArrayMap<String, Integer> getAllBuildingsMaxBlankAtTH(int thLevel, String table) {

        openDb();

        String thColumn = ClashDBHelper.COLUMN_TH_PREFIX + thLevel;
        String[] columns = {ClashDBHelper.COLUMN_BUILDING, thColumn};

        // SELECT th8 FROM MaxLevel where _building = 'building'
        Cursor cursor = db.query(table, columns, null, null, null, null, null, null);

        ArrayMap<String, Integer> map = new ArrayMap<>(cursor.getCount());

        while (cursor.moveToNext()) {
            String entityName = cursor.getString(cursor.getColumnIndex(ClashDBHelper.COLUMN_BUILDING));
            int max = cursor.getInt(cursor.getColumnIndex(thColumn));
            map.put(entityName, max);
        }

        if (cursor.getCount() <= 0) {
            map = null;
        }

        db.close();
        cursor.close();
        return map;
    }

    private void openDb() {
        if (!db.isOpen()) {
            db = mClashDbHelper.getReadableDatabase();
        }
    }


    /**
     *
     * Public Methods
     *
     */

    public ArrayMap<String, Integer> getAllBuildingsMaxLevelAtTH(int thLevel) {
        return getAllBuildingsMaxBlankAtTH(thLevel, ClashDBHelper.TABLE_MAX_LEVEL);
    }

    public int getBuildingMaxLevelAtTH(String building, int thLevel) {
        return getBuildingMaxBlankAtTH(building, thLevel, ClashDBHelper.TABLE_MAX_LEVEL);
    }

    public int[] getBuildingAllMaxLevel(String building) {
        return getBuildingAllMaxBlank(building, ClashDBHelper.TABLE_MAX_LEVEL);
    }

    public ArrayMap<String, int[]> getAllMaxLevel() {
        return getAllMaxBlank(ClashDBHelper.TABLE_MAX_LEVEL);
    }

    public int getBuildingMaxAmountAtTH(String building, int thLevel) {
        return getBuildingMaxBlankAtTH(building, thLevel, ClashDBHelper.TABLE_MAX_AMOUNT);
    }

    public int[] getBuildingAllMaxAmount(String building) {
        return getBuildingAllMaxBlank(building, ClashDBHelper.TABLE_MAX_AMOUNT);

    }

    public ArrayMap<String, int[]> getAllMaxAmount() {
        return getAllMaxBlank(ClashDBHelper.TABLE_MAX_AMOUNT);
    }

    public ArrayMap<String, Integer> getAllBuildingsMaxAmountAtTH(int thLevel) {
        return getAllBuildingsMaxBlankAtTH(thLevel, ClashDBHelper.TABLE_MAX_AMOUNT);
    }
}
