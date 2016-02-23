package science.anthonyalves.clashofclanshelper;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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
import science.anthonyalves.clashofclanshelper.adapters.TroopAdapter;
import science.anthonyalves.clashofclanshelper.sqlite.ChildrenDAO;
import science.anthonyalves.clashofclanshelper.sqlite.EntityContent;
import science.anthonyalves.clashofclanshelper.sqlite.MaxDAO;
import science.anthonyalves.clashofclanshelper.sqlite.PictureDAO;
import science.anthonyalves.clashofclanshelper.sqlite.Troop;
import science.anthonyalves.clashofclanshelper.sqlite.TroopDAO;

public class TroopsFragment extends Fragment implements View.OnClickListener {

    RecyclerView mRecyclerView;
    Activity mActivity;
    View mView;
    View activityView;
    public static int TH_LEVEL = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        activityView = MainActivity.mView;
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.troops_fragment_layout, container, false);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.troops_recyclerview);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        ArrayMap<String, String[]> pictures = new PictureDAO(mActivity).getAll();
        ArrayMap<String, Troop> data = initData();

        mRecyclerView.setAdapter(new TroopAdapter(mActivity, data, pictures));

        return mView;
    }

    public ArrayMap<String, Troop> initData(){

        TroopDAO dao = new TroopDAO(mActivity);
        ArrayMap<String, Troop> data = dao.getAll();
        return data;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.troops_menu, menu);
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
