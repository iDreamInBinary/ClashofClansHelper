package science.anthonyalves.clashofclanshelper.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import science.anthonyalves.clashofclanshelper.BuildingsFragment;
import science.anthonyalves.clashofclanshelper.R;
import science.anthonyalves.clashofclanshelper.sqlite.ChildrenDAO;
import science.anthonyalves.clashofclanshelper.sqlite.Troop;
import science.anthonyalves.clashofclanshelper.sqlite.TroopDAO;
import science.anthonyalves.clashofclanshelper.utils.Constants;


public class TroopAdapter extends RecyclerView.Adapter<TroopAdapter.ViewHolder> {

    ArrayMap<String, Troop> mData = null;
    SimpleArrayMap<String, String[]> mUnformattedPictures = null;

    Context mContext;

    final long ANIMATION_TIME = 500;
    private SharedPreferences mSharedPreferences;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView troopImage;
        TextView troopName;
        TextView maxLevel;
        Button upgrade;

        public ViewHolder(View itemView) {
            super(itemView);
            troopImage = (ImageView) itemView.findViewById(R.id.entity_image);
            troopName = (TextView) itemView.findViewById(R.id.entity_name);
            maxLevel = (TextView) itemView.findViewById(R.id.entity_max_level);
            upgrade = (Button) itemView.findViewById(R.id.entity_upgrade);

            troopImage.setOnClickListener(this);
            upgrade.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            switch (v.getId()) {
                case R.id.entity_image:
                    Toast.makeText(v.getContext(), "Info", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.entity_upgrade:
                    TroopDAO dao = new TroopDAO(mContext);
                    String troop = mData.keyAt(pos);
                    int level = mData.get(troop).mCurrentLevel;
                    int maxLev = mData.get(troop).mMaxLevel;

                    if (dao.upgradeTroop(troop, level)) {
                        maxLevel.setText("Level " + (level+1) + " of " + maxLev);
                        mData.get(troop).mCurrentLevel += 1;
                        notifyItemChanged(mData.indexOfKey(troop));
                    }
                    break;
            }
        }
    }

    public TroopAdapter(Context context, ArrayMap<String, Troop> data, SimpleArrayMap<String, String[]> pictures) {
        mData = data;
        mContext = context;
        mUnformattedPictures = pictures;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);


        ArrayList<String> temp = new ArrayList<>(mData.size());
        log(mData.keySet().size() + "");
        for (int i = 0; i < mData.keySet().size(); i++) {
            if (isMaxed(mData.keyAt(i))) {
                temp.add(mData.keyAt(i));
            }
        }

        for (int i = 0; i < temp.size(); i++) {
            mData.remove(temp.get(i));
        }

        notifyDataSetChanged();
    }

    private boolean isMaxed(String s) {

        if (!mSharedPreferences.getBoolean(Constants.HIDE_MAXED_TROOPS, true)) {
            return false;
        }

        String str = mData.get(s).mCurrentLevel + ",  " + mData.get(s).mMaxLevel + (mData.get(s).mCurrentLevel >= mData.get(s).mMaxLevel);
        log(str);

        return (mData.get(s).mCurrentLevel >= mData.get(s).mMaxLevel);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.troop_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.upgrade.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String troop = mData.keyAt(position);
        final Troop temp = mData.get(troop);

        int thLevel = BuildingsFragment.TH_LEVEL;
        int maxLevel = temp.mMaxLevel;

        String prettyBuildingName = uglyToPretty(troop);
        holder.troopImage.setImageResource(getImageId(troop, maxLevel));
        holder.troopName.setText(prettyBuildingName);
        holder.maxLevel.setText("Level " + temp.mCurrentLevel + " of " + maxLevel);

        if (isMaxed(troop)) {
            holder.upgrade.setVisibility(View.GONE);
        }
    }

    private int getImageId(String buildingCode, int level) {
        String imageString = buildingCode + "_" + level;
        int id = mContext.getResources().getIdentifier(imageString, "drawable", mContext.getPackageName());
        if (id == 0) {
            //try to get the picture in the pictures table for special cases.
            String resource = mUnformattedPictures.get(buildingCode)[level];
            id = mContext.getResources().getIdentifier(resource, "drawable", mContext.getPackageName());
            if (id == 0) {
                // TODO default troop pic
                id = R.drawable.blank_profile_picture;
            }
        }
        return id;
    }

    private String uglyToPretty(String s) {
        String[] words = s.split("_");
        String buildingName = "";
        for (String word : words) {
            word = (word.charAt(0) + "").toUpperCase() + word.substring(1) + " ";
            buildingName += word;
        }

        if (buildingName.charAt(buildingName.length() - 1) == ' ') {
            buildingName = buildingName.substring(0, buildingName.length() - 1);
        }
        return buildingName;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    private void log(String s) {
        Log.d(getClass().getSimpleName(), s);
    }

}
