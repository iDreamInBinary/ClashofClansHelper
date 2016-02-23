package science.anthonyalves.clashofclanshelper.sqlite;


import java.util.ArrayList;

public class EntityContent {
    public String mEntityName;
    public int mMaxLevel;
    public int mMaxAmount;
    public ArrayList<Integer> mChildrenLevels;
    public boolean isExpanded = false;

    public EntityContent(String name, int maxLevel, int maxAmount, ArrayList<Integer> childrenLevels){
        mEntityName = name;
        mMaxLevel = maxLevel;
        mMaxAmount = maxAmount;
        mChildrenLevels = childrenLevels;
    }
}
