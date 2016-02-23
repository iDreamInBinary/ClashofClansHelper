package science.anthonyalves.clashofclanshelper.sqlite;

public class Troop {
    public String mTroopName;
    public int mMaxLevel;
    public int mCurrentLevel;

    public Troop(String mTroopName, int mCurrentLevel, int mMaxLevel) {
        this.mTroopName = mTroopName;
        this.mCurrentLevel = mCurrentLevel;
        this.mMaxLevel = mMaxLevel;
    }
}
