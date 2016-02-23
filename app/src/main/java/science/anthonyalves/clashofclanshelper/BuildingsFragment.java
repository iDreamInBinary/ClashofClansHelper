package science.anthonyalves.clashofclanshelper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import science.anthonyalves.clashofclanshelper.adapters.BuildingsAdapter;
import science.anthonyalves.clashofclanshelper.setup.BuildingSetup;
import science.anthonyalves.clashofclanshelper.sqlite.ChildrenDAO;
import science.anthonyalves.clashofclanshelper.sqlite.EntityContent;
import science.anthonyalves.clashofclanshelper.sqlite.MaxDAO;
import science.anthonyalves.clashofclanshelper.sqlite.PictureDAO;

public class BuildingsFragment extends Fragment implements View.OnClickListener {

    FloatingActionButton mSearchFAB;
    RecyclerView mRecyclerView;
    Activity mActivity;
    View mView;
    View activityView;
    public static int TH_LEVEL = 10;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        activityView = MainActivity.mView;
        mActivity = getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.buildings_fragment_layout, container, false);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.buildings_recyclerview);
        mSearchFAB = (FloatingActionButton) activityView.findViewById(R.id.search_fab);

        mSearchFAB.setVisibility(View.GONE);
        mSearchFAB.setOnClickListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        // TODO
        if (mSharedPreferences.getBoolean("building_first", false)) {
            FragmentManager fm = mActivity.getFragmentManager();
            BuildingSetup setupDialog = new BuildingSetup();
            setupDialog.setCancelable(false);
            setupDialog.show(fm, "fragment_edit_name");
        } else {
            initData();
        }

        return mView;
    }

    public void initData() {
        ArrayMap<String, String[]> pictures = new PictureDAO(mActivity).getAll();

        ArrayMap<String, EntityContent> data = new ArrayMap<>();

        MaxDAO maxDAO = new MaxDAO(mActivity);
        ChildrenDAO childrenDAO = new ChildrenDAO(mActivity);
        ArrayMap<String, Integer> maxLevels = maxDAO.getAllBuildingsMaxLevelAtTH(TH_LEVEL);
        ArrayMap<String, Integer> maxAmounts = maxDAO.getAllBuildingsMaxAmountAtTH(TH_LEVEL);
        ArrayMap<String, ArrayList<Integer>> childrenLevels = childrenDAO.getAll();

        for (int i = 0; i < maxLevels.size(); i++) {
            String buildingName = maxLevels.keyAt(i);
            int maxLevel = maxLevels.get(buildingName);
            int maxAmount = maxAmounts.get(buildingName);
            ArrayList<Integer> children = childrenLevels.get(buildingName);

            EntityContent temp = new EntityContent(buildingName, maxLevel, maxAmount, children);
            data.put(buildingName, temp);
        }

        mRecyclerView.setAdapter(new BuildingsAdapter(mActivity, data, pictures));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSearchFAB.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        showToast(item.getTitle().toString());
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_sort) {
            PopupMenu popup = new PopupMenu(mActivity, mActivity.findViewById(R.id.menu_sort));
            popup.getMenuInflater().inflate(R.menu.sort_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    return onOptionsItemSelected(item);
                }
            });
            popup.show();
        } else if (id == R.id.menu_expand_all) {
            ((BuildingsAdapter) mRecyclerView.getAdapter()).expandAll();
        } else if (id == R.id.menu_collapse_all) {
            ((BuildingsAdapter) mRecyclerView.getAdapter()).collapseAll();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showToast(String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }

    public void showSnackbar(String message, int length) {
        Snackbar.make(activityView.findViewById(R.id.search_fab), message, length).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_fab) {
            if (MainActivity.getmGoogleAccount().revokeAccess()) {
                showSnackbar("Revoked access on " + MainActivity.mProfileEmailTV.getText(), Snackbar.LENGTH_LONG);
            }
        }
    }

    public void log(String s) {
        Log.d(getClass().getSimpleName(), s);
    }
}
