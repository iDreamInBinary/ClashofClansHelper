package science.anthonyalves.clashofclanshelper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

public class MyVibrator {

    public interface VibratorCallback {
        void vibratorStatus(boolean b);
    }

    private final Context mContext;
    VibratorCallback mVibratorCallback;

    public boolean isVibrationRunning = false;
    private SharedPreferences mSharedPreferences;

    private String TAG = this.getClass().getSimpleName();


    public MyVibrator(Context context, VibratorCallback callback) {
        mContext = context;
        mVibratorCallback = callback;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void start() {
        final boolean isVibrationOn = mSharedPreferences.getBoolean("vibration_switch", false);

        if (!isVibrationOn) { // check if vibration is enabled in the app.
            return;
        }

        final Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        if (!v.hasVibrator()) { // check if phone has a vibrator
            log("Device does not have vibrator");
            return;
        }

        // just in case
        if (isVibrationRunning) {
            return;
        }


        final int vibrationPulse = Integer.parseInt(mSharedPreferences.getString("vibration_pulse", Constants.DEFAULT_VIBRATION_PULSE));
        int vibrationDuration = Integer.parseInt(mSharedPreferences.getString("vibration_duration", Constants.DEFAULT_VIBRATION_DURATION));

        if (vibrationDuration == 0) {
            vibrationDuration = Integer.MAX_VALUE;
        }


        log("Starting Alert");
        final int finalVibrationDuration = vibrationDuration;
        new Thread() {
            @Override
            public void run() {
                int pulses = 0;
                isVibrationRunning = true;
                mVibratorCallback.vibratorStatus(isVibrationRunning);

                while (isVibrationRunning && ((pulses * vibrationPulse) < finalVibrationDuration)) {
                    try {
                        v.vibrate(vibrationPulse);
                        Thread.sleep(vibrationPulse * 2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    pulses++;
                }
                isVibrationRunning = false;
                mVibratorCallback.vibratorStatus(isVibrationRunning);
            }
        }.start();
    }

    public void stop() {
        if (isVibrationRunning) {
            isVibrationRunning = false;
            mVibratorCallback.vibratorStatus(isVibrationRunning);
        }
    }

    private void log(String s) {
        Log.d(TAG, s);
    }
}
