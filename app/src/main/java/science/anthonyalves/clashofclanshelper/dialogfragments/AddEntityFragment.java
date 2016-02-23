package science.anthonyalves.clashofclanshelper.dialogfragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import science.anthonyalves.clashofclanshelper.R;

public class AddEntityFragment extends DialogFragment {
    public interface EntityDialogListener {
        void onPositiveClick(int level);

        void onNegativeClick();
    }

    int imgId;
    String entityName;
    Spinner spinner;

    private final int mMaxLevel;

    View mView;


    Activity mActivity;

    EntityDialogListener mCallback;


    public AddEntityFragment(EntityDialogListener callback, int maxLevel, String name, int id) {
        mCallback = callback;
        mMaxLevel = maxLevel;
        entityName = name;
        imgId = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mActivity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();

        View view = inflater.inflate(R.layout.add_entity, null);
        mView = view;

        TextView entityNameTV = (TextView) mView.findViewById(R.id.entity_name);
        entityNameTV.setText(entityName);

        ImageView entityImage = (ImageView) mView.findViewById(R.id.entity_image);
        entityImage.setImageResource(imgId);

        spinner = (Spinner) mView.findViewById(R.id.entity_spinner);
        List<String> levels = new ArrayList<>(mMaxLevel);
        for (int i = 1; i <= mMaxLevel; i++) {
            levels.add(String.valueOf(i));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, levels);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mCallback.onPositiveClick(Integer.parseInt(spinner.getSelectedItem().toString()));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mCallback.onNegativeClick();
                    }
                });
        return builder.create();
    }
}
