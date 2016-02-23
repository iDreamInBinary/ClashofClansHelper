package science.anthonyalves.clashofclanshelper.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import science.anthonyalves.clashofclanshelper.BuildingsFragment;
import science.anthonyalves.clashofclanshelper.R;
import science.anthonyalves.clashofclanshelper.dialogfragments.AddEntityFragment;
import science.anthonyalves.clashofclanshelper.sqlite.ChildrenDAO;
import science.anthonyalves.clashofclanshelper.sqlite.EntityContent;
import science.anthonyalves.clashofclanshelper.utils.Constants;


public class BuildingsAdapter extends RecyclerView.Adapter<BuildingsAdapter.ViewHolder> {

    ArrayMap<String, EntityContent> mData = null;
    SimpleArrayMap<String, String[]> mUnformattedPictures = null;
    SharedPreferences mSharedPreferences;

    Activity mActivity;

    final long ANIMATION_TIME = 500;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView entityImage;
        ImageView dropdown;
        TextView entityName;
        TextView maxLevel;
        LinearLayout childLayout;
        LinearLayout subEntityLayout;
        RelativeLayout parentLayout;
        ImageView addEntity;

        public ViewHolder(View itemView) {
            super(itemView);
            entityImage = (ImageView) itemView.findViewById(R.id.entity_image);
            dropdown = (ImageView) itemView.findViewById(R.id.entity_dropdown);
            entityName = (TextView) itemView.findViewById(R.id.entity_name);
            maxLevel = (TextView) itemView.findViewById(R.id.entity_max_level);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.parent_relative_layout);
            childLayout = (LinearLayout) itemView.findViewById(R.id.child_linear_layout);
            subEntityLayout = (LinearLayout) childLayout.findViewById(R.id.sub_entities_layout);
            addEntity = (ImageView) childLayout.findViewById(R.id.add_entity);

            entityImage.setOnClickListener(this);

            dropdown.setOnClickListener(this);
            parentLayout.setOnClickListener(this);
            addEntity.setOnClickListener(this);
        }

        private void collapse(final String buildingCode) {
            childLayout.setVisibility(View.GONE);
            childLayout.animate()
                    .translationY(0)
                    .alpha(0.0f)
                    .setDuration(ANIMATION_TIME)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });
            dropdown.setImageResource(R.mipmap.ic_chevron_down_grey600_24dp);
            mData.get(buildingCode).isExpanded = false;
        }

        private void expand(String buildingCode) {
            childLayout.setAlpha(0.0f);
            childLayout.animate()
                    .setDuration(ANIMATION_TIME)
                    .translationY(childLayout.getHeight())
                    .alpha(1.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            childLayout.setVisibility(View.VISIBLE);
                        }
                    });
            dropdown.setImageResource(R.mipmap.ic_chevron_up_grey600_24dp);
            mData.get(buildingCode).isExpanded = true;
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            switch (v.getId()) {
                case R.id.entity_image:
                    break;
                case R.id.sub_upgrade_button:
                    Toast.makeText(v.getContext(), "Upgrade", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.add_entity:
                    Toast.makeText(v.getContext(), "Add Entity", Toast.LENGTH_SHORT).show();
                    String buildingName = mData.keyAt(pos);
                    int maxLevel = mData.get(buildingName).mMaxLevel;
                    addEntity(buildingName, maxLevel, pos, getImageId(buildingName, maxLevel));
                    break;
                case R.id.entity_dropdown:
                case R.id.parent_relative_layout:
                    pos = getAdapterPosition();
                    notifyItemChanged(pos);
                    switch (childLayout.getVisibility()) {
                        case View.GONE:
                            expand(mData.keyAt(pos));
                            break;
                        case View.VISIBLE:
                            collapse(mData.keyAt(pos));
                            break;
                    }
                    break;
            }
        }
    }

    private void addEntity(final String building, int maxLevel, final int position, int imgId) {
        AddEntityFragment addEntity = new AddEntityFragment(new AddEntityFragment.EntityDialogListener() {
            @Override
            public void onPositiveClick(int level) {
                // TODO add new level to UserDB
                ChildrenDAO dao = new ChildrenDAO(mActivity);
                if (dao.addChild(building, level)) {
                    // TODO add new level to mData
                    if (mData.get(building).mChildrenLevels == null) {
                        mData.get(building).mChildrenLevels = new ArrayList<>();
                    }

                    if (dao.getChildrenLevels(building).length <= mData.get(building).mMaxAmount) {
                        mData.get(building).mChildrenLevels.add(level);
                        notifyItemChanged(position);
                    }

                    Toast.makeText(mActivity, "Building added", Toast.LENGTH_SHORT);

                } else {
                    Toast.makeText(mActivity, "Unable to add building", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onNegativeClick() {

            }
        }, maxLevel, uglyToPretty(building), imgId);
        addEntity.setCancelable(false);
        addEntity.show(mActivity.getFragmentManager(), "addEntity");
    }


    public BuildingsAdapter(Activity activity, ArrayMap<String, EntityContent> data, SimpleArrayMap<String, String[]> pictures) {
        mData = data;
        mActivity = activity;
        mUnformattedPictures = pictures;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);


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


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        switch (viewType) {
            case 0:
                viewHolder.childLayout.setVisibility(View.GONE);
                viewHolder.dropdown.setImageResource(R.mipmap.ic_chevron_down_grey600_24dp);
                break;
            case 1:
                viewHolder.childLayout.setVisibility(View.VISIBLE);
                viewHolder.dropdown.setImageResource(R.mipmap.ic_chevron_up_grey600_24dp);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.subEntityLayout.removeAllViews();
        holder.addEntity.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(mData.keyAt(position)).isExpanded)
            return 1;
        else
            return 0;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String buildingCode = mData.keyAt(position);

        int thLevel = BuildingsFragment.TH_LEVEL;
        int maxLevel = mData.get(buildingCode).mMaxLevel;


        String prettyBuildingName = uglyToPretty(buildingCode);
        holder.entityImage.setImageResource(getImageId(buildingCode, maxLevel));
        holder.entityName.setText(prettyBuildingName);
        holder.maxLevel.setText("Max level at TH" + thLevel + ": " + maxLevel);

        if (mData.get(buildingCode).mChildrenLevels != null) {

            if (hasMaxedAmount(buildingCode)) {
                holder.addEntity.setVisibility(View.GONE);
            }

            for (int i = 0; i < mData.get(buildingCode).mChildrenLevels.size(); i++) {
                final int level = mData.get(buildingCode).mChildrenLevels.get(i);
                if (level == maxLevel && mSharedPreferences.getBoolean(Constants.HIDE_MAXED_DEFENSES, false)) {
                    continue;
                }
                LinearLayout sub = (LinearLayout) LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.sub_item, null);
                int imageId = getImageId(buildingCode, level);
                ((ImageView) sub.findViewById(R.id.sub_entity_image)).setImageResource(imageId);
                final TextView levelTV = (TextView) sub.findViewById(R.id.sub_entity_level);
                levelTV.setText("Current Level: " + level);
                Button upgrade = (Button) sub.findViewById(R.id.sub_upgrade_button);
                if (level == maxLevel) {
                    upgrade.setVisibility(View.GONE);
                }
                upgrade.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChildrenDAO childrenDAO = new ChildrenDAO(mActivity);
                        if (childrenDAO.upgradeChild(buildingCode, level)) {
                            levelTV.setText("Current Level: " + (level + 1));
                            for (int i = 0; i < mData.get(buildingCode).mChildrenLevels.size(); i++) {
                                if (mData.get(buildingCode).mChildrenLevels.get(i) == level) {
                                    mData.get(buildingCode).mChildrenLevels.set(i, level + 1);
                                    break;
                                }
                            }
                            notifyItemChanged(mData.indexOfKey(buildingCode));
                        }
                    }
                });
                holder.subEntityLayout.addView(sub);
            }
        }
    }

    private boolean hasMaxedAmount(String building) {
        EntityContent temp = mData.get(building);

        if (temp.mChildrenLevels == null) {
            return false;
        }

        int size = temp.mChildrenLevels.size();
        int maxAmount = temp.mMaxAmount;

        return (size >= maxAmount);
    }

    private boolean isMaxed(String buildingCode) {

        if (!mSharedPreferences.getBoolean(Constants.HIDE_MAXED_DEFENSES, true)) {
            return false;
        }

        EntityContent temp = mData.get(buildingCode);

        if (temp.mChildrenLevels == null) {
            return false;
        }

        int size = temp.mChildrenLevels.size();

        int marks = 0;
        for (int i = 0; i < size; i++) {
            int level = temp.mChildrenLevels.get(i);
            if (level == temp.mMaxLevel) {
                marks++;
            }
        }

        return (marks == size);
    }

    public void expandAll() {
        for (int i = 0; i < mData.size(); i++) {
            mData.valueAt(i).isExpanded = true;
        }
        notifyDataSetChanged();
    }

    public void collapseAll() {
        for (int i = 0; i < mData.size(); i++) {
            mData.valueAt(i).isExpanded = false;
        }
        notifyDataSetChanged();
    }

    private int getImageId(String buildingCode, int level) {
        String imageString = buildingCode + "_" + level;
        int id = mActivity.getResources().getIdentifier(imageString, "drawable", mActivity.getPackageName());
        if (id == 0) {
            //try to get the picture in the pictures table for special cases.
            String resource = mUnformattedPictures.get(buildingCode)[level];
            id = mActivity.getResources().getIdentifier(resource, "drawable", mActivity.getPackageName());
            if (id == 0) {
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
