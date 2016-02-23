package science.anthonyalves.clashofclanshelper.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.ArrayMap;

import science.anthonyalves.clashofclanshelper.BuildingsFragment;

public class TroopDAO {

    private final Context mContext;
    ClashDBHelper mClashDbHelper;
    UserDBHelper mUserDBHelper;
    SQLiteDatabase mClashdb;
    SQLiteDatabase mUserdb;


    public TroopDAO(Context context) {
        mContext = context;
        mClashDbHelper = new ClashDBHelper(mContext);
        mUserDBHelper = new UserDBHelper(mContext);
        mClashdb = mClashDbHelper.getReadableDatabase();
        mUserdb = mUserDBHelper.getWritableDatabase();
    }

    public ArrayMap<String, Troop> getAll() {
        int labLevel = getLabLevel(BuildingsFragment.TH_LEVEL);

        if (!mClashdb.isOpen()) {
            mClashdb = mClashDbHelper.getReadableDatabase();
        }

        if (!mUserdb.isOpen()) {
            mUserdb = mUserDBHelper.getWritableDatabase();
        }

        Cursor userCursor = mClashdb.query(ClashDBHelper.TABLE_TROOPS, null, null, null, null, null, null, null);

        ArrayMap<String, Troop> map = new ArrayMap<>(userCursor.getCount());
        while (userCursor.moveToNext()) {
            String troop = userCursor.getString(userCursor.getColumnIndex(ClashDBHelper.COLUMN_NAME));
            String[] columns = {ClashDBHelper.COLUMN_LEVEL};
            String[] selectArgs = {String.valueOf(labLevel)};
            Cursor clashCursor = mClashdb.query(troop, columns, ClashDBHelper.COLUMN_LAB_LEVEL + " = ?", selectArgs, null, null, null);
            int maxTroopLevel = 0;
            while (clashCursor.moveToNext()) {
                maxTroopLevel = clashCursor.getInt(clashCursor.getColumnIndex(ClashDBHelper.COLUMN_LEVEL));
            }

            columns = new String[]{String.valueOf(UserDBHelper.COLUMN_LEVEL)};
            selectArgs = new String[]{troop};
            clashCursor = mUserdb.query(UserDBHelper.TABLE_TROOPS, columns, UserDBHelper.COLUMN_ENTITY + " = ?", selectArgs, null, null, null);
            int level = 0;
            while (clashCursor.moveToNext()) {
                level = clashCursor.getInt(clashCursor.getColumnIndex(ClashDBHelper.COLUMN_LEVEL));
            }

            map.put(troop, new Troop(troop, level, maxTroopLevel));
        }

        mClashdb.close();
        userCursor.close();
        return map;
    }

    private int getLabLevel(int thLevel) {
        if (!mUserdb.isOpen()) {
            mUserdb = mUserDBHelper.getWritableDatabase();
        }

        String[] columns = {UserDBHelper.COLUMN_LEVEL};
        String[] selectArgs = {BuildingNames.LABORATORY};

        Cursor cursor = mUserdb.query(UserDBHelper.TABLE_BUILDINGS, columns, UserDBHelper.COLUMN_ENTITY + " = ?", selectArgs, null, null, null, "1");
        int level = 1;
        while (cursor.moveToNext()) {
            level = cursor.getInt(cursor.getColumnIndex(UserDBHelper.COLUMN_LEVEL));
        }

        mClashdb.close();
        cursor.close();
        return level;
    }

    public boolean upgradeTroop(String buildingCode, int level) {
        if (!mUserdb.isOpen()) {
            mUserdb = mUserDBHelper.getWritableDatabase();
        }

        ContentValues insertValues = new ContentValues();
        insertValues.put(UserDBHelper.COLUMN_ENTITY, buildingCode);
        insertValues.put(UserDBHelper.COLUMN_LEVEL, level + 1);

        int status = mUserdb.update(UserDBHelper.TABLE_TROOPS, insertValues, UserDBHelper.COLUMN_ENTITY + " = ? AND " + UserDBHelper.COLUMN_LEVEL + " = ?", new String[]{buildingCode, String.valueOf(level)});
        if (status <= 0) {
            long insertStatus = mUserdb.insert(UserDBHelper.TABLE_TROOPS, null, insertValues);
            return (insertStatus > 0);
        } else {
            return true;
        }
    }


}
