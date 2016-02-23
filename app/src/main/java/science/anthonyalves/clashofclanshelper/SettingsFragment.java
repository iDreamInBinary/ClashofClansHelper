package science.anthonyalves.clashofclanshelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;

public class SettingsFragment extends PreferenceFragment {

    Activity mActivity;
    String TAG;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        TAG = this.getClass().getSimpleName();
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.global_prefs);

        Preference clearSugButton = (Preference) findPreference(getString(R.string.revoke_permission_button));
        /*
        clearSugButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage(getString(R.string.revoke_dialog_message))
                        .setTitle(getString(R.string.revoke_dialog_title));

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (MainActivity.getmGoogleAccount().revokeAccess()) {
                            MainActivity.showSnackbar("Revoked access on " + MainActivity.mProfileEmailTV.getText(), Snackbar.LENGTH_LONG);
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // nothing
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
        */
    }
}