package science.anthonyalves.clashofclanshelper.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.ArrayMap;

public class PictureDAO {

    private final Context mContext;
    ClashDBHelper mClashDbHelper;
    SQLiteDatabase db;


    public PictureDAO(Context context) {
        mContext = context;
        mClashDbHelper = new ClashDBHelper(mContext);
        db = mClashDbHelper.getReadableDatabase();
    }


    private String getBuildingLevel(String building, int buildingLevel) {
        String[] selectArgs = {building};
        String[] columns = {buildingLevel + ""};

        // SELECT th8 FROM MaxLevel where _building = 'building'
        Cursor cursor = db.query(ClashDBHelper.TABLE_PICTURES, columns, ClashDBHelper.COLUMN_BUILDING + " = ?", selectArgs, null, null, null, "1");

        String name = null;

        if (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(buildingLevel + ""));
        }

        db.close();
        cursor.close();
        return name;
    }

    private String[] getBuildingAllLevels(String building) {
        String[] selectArgs = {building};

        // SELECT th8 FROM MaxLevel where _building = 'building'
        Cursor cursor = db.query(ClashDBHelper.TABLE_PICTURES, null, ClashDBHelper.COLUMN_BUILDING + " = ?", selectArgs, null, null, null, "1");

        String[] values = null;
        if (cursor.moveToNext()) {
            values = new String[cursor.getColumnCount() - 1];
            for (int i = 0; i < values.length; i++) {
                values[i] = cursor.getString(i + 1);
            }
        }

        db.close();
        cursor.close();
        return values;

    }

    public ArrayMap<String, String[]> getAll() {

        Cursor cursor = db.query(ClashDBHelper.TABLE_PICTURES, null, null, null, null, null, null, null);

        ArrayMap<String, String[]> map = new ArrayMap<>(cursor.getCount());

        while (cursor.moveToNext()) {
            String[] values = new String[cursor.getColumnCount() - 1];
            for (int i = 0; i < values.length; i++) {
                values[i] = cursor.getString(i+1);
            }
            map.put(cursor.getString(0), values);
        }

        if (cursor.getCount() <= 0){
            map = null;
        }

        db.close();
        cursor.close();
        return map;
    }
}
