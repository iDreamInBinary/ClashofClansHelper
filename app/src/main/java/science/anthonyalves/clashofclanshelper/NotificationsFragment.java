package science.anthonyalves.clashofclanshelper;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import science.anthonyalves.clashofclanshelper.utils.Constants;
import science.anthonyalves.clashofclanshelper.utils.MyFlashlight;
import science.anthonyalves.clashofclanshelper.utils.MyVibrator;

public class NotificationsFragment extends PreferenceFragment implements View.OnClickListener {

    Activity mActivity;
    String TAG;
    SharedPreferences mSharedPreferences;
    LinearLayout settingsView;
    RelativeLayout warningView;
    LayoutInflater mInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        TAG = this.getClass().getSimpleName();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.notification_prefs);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        settingsView = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);
        mInflater = inflater;

        LinearLayout testLayout = (LinearLayout) inflater.inflate(R.layout.test_notifications_layout, null);

        Button testLED = (Button) testLayout.findViewById(R.id.test_led);
        Button testVibration = (Button) testLayout.findViewById(R.id.test_vibration);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Testing Notification");
        builder.setCancelable(false);

        testLED.setOnClickListener(new View.OnClickListener() {
            boolean testRunning = false;
            AlertDialog alert;

            @Override
            public void onClick(View v) {
                if (!testRunning) {
                    final MyFlashlight testFlashlight = new MyFlashlight(mActivity, new MyFlashlight.FlashlightCallback() {
                        @Override
                        public void flashlightStatus(boolean b) {
                            testRunning = b;
                            if (!b) {
                                alert.cancel();
                            }
                        }
                    });
                    testFlashlight.start();
                    builder.setNegativeButton("Stop", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            testFlashlight.stop();
                            dialog.cancel();
                        }
                    });
                    alert = builder.create();
                    alert.show();
                }
            }
        });

        testVibration.setOnClickListener(new View.OnClickListener() {
            boolean testRunning = false;
            AlertDialog alert;

            @Override
            public void onClick(View v) {
                if (!testRunning) {
                    final MyVibrator testVibrator = new MyVibrator(mActivity, new MyVibrator.VibratorCallback() {
                        @Override
                        public void vibratorStatus(boolean b) {
                            testRunning = b;
                            if (!b) {
                                alert.cancel();
                            }
                        }
                    });
                    testVibrator.start();
                    builder.setNegativeButton("Stop", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            testVibrator.stop();
                            dialog.cancel();
                        }
                    });
                    alert = builder.create();
                    alert.show();
                }
            }
        });


        settingsView.addView(testLayout);


        return settingsView;
    }

    @Override
    public void onResume() {
        super.onResume();

        log("on resume");
        boolean enabled = isNotificationAccessEnabled();
        if (!enabled && warningView == null) { // we don't want duplicate warningViews xD
            warningView = (RelativeLayout) mInflater.inflate(R.layout.notification_warning_layout, null);
            assert settingsView != null;
            settingsView.addView(warningView);
            Button enableAccessButton = (Button) warningView.findViewById(R.id.enable_access_button);
            enableAccessButton.setOnClickListener(this);
        } else if (enabled && warningView != null) { // remove the warningView if the user successfully enabled
            settingsView.removeView(warningView);
        }
    }

    private boolean isNotificationAccessEnabled() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            AccessibilityManager am = (AccessibilityManager) mActivity.getSystemService(Context.ACCESSIBILITY_SERVICE);

            assert am != null;
            List<AccessibilityServiceInfo> runningServices = am.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
            log("length: " + runningServices.size());

            for (AccessibilityServiceInfo service : runningServices) {
                log(service.getId());
                if (Constants.ACCESSIBILITY_SERVICE_ID.equals(service.getId())) {
                    return true;
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return mSharedPreferences.getBoolean(Constants.IS_BINDED_SHAREDPREF_KEY, false);
        }


        return false;
    }

    private void log(String s) {
        Log.d(TAG, s);
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.enable_access_button) {
            Intent settingsIntent = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                Log.d("sdf", "Access not enabled, pre KK");
                settingsIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Log.d("sdf", "Access not enabled, post KK");
                settingsIntent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            }
            startActivity(settingsIntent);
            super.onCreate(null);
        }
    }
}
