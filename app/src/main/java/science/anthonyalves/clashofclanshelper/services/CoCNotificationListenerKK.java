package science.anthonyalves.clashofclanshelper.services;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import science.anthonyalves.clashofclanshelper.utils.Constants;
import science.anthonyalves.clashofclanshelper.utils.MyFlashlight;
import science.anthonyalves.clashofclanshelper.utils.MyVibrator;

@TargetApi(Build.VERSION_CODES.KITKAT) //API 19
public class CoCNotificationListenerKK extends NotificationListenerService implements MyVibrator.VibratorCallback, MyFlashlight.FlashlightCallback {


    private Context mContext;

    private String TAG = this.getClass().getSimpleName();

    SharedPreferences mSharedPreferences;

    MyFlashlight myFlashlight;
    private boolean isLedRunning = false;

    MyVibrator myVibrator;
    private boolean isVibrationRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        myVibrator = new MyVibrator(mContext, this);
        myFlashlight = new MyFlashlight(mContext, this);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        String packageName = sbn.getPackageName();

        if (!packageName.equals(Constants.COC_PACKAGE_NAME)) {
            return;
        }


        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString(Notification.EXTRA_TITLE);

        //noinspection ConstantConditions
        String text = extras.getCharSequence(Notification.EXTRA_TEXT).toString();

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);


        if (am.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            return;
        }

        if (!isVibrationRunning) {
            myVibrator.start();
        }

        if (!isLedRunning) {
            myFlashlight.start();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        String packageName = sbn.getPackageName();

        if (!packageName.equals(Constants.COC_PACKAGE_NAME)) {
            return;
        }

        myVibrator.stop();
        myFlashlight.stop();
    }

    @Override
    public IBinder onBind(Intent mIntent) {
        IBinder mIBinder = super.onBind(mIntent);

        saveBind(true);
        return mIBinder;
    }

    @Override
    public boolean onUnbind(Intent mIntent) {
        boolean mOnUnbind = super.onUnbind(mIntent);

        // save to sharedpref
        saveBind(false);
        return mOnUnbind;
    }

    @Override
    public void vibratorStatus(boolean b) {
        isVibrationRunning = b;
    }

    @Override
    public void flashlightStatus(boolean b) {
        isLedRunning = b;
    }

    /**
     * * * * * * * * * * * * * * * * * * * *
     *                                     *
     * Custom methods                      *
     *                                     *
     * * * * * * * * * * * * * * * * * * * *
     */


    @SuppressLint("CommitPrefEdits")
    private void saveBind(boolean b) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Constants.IS_BINDED_SHAREDPREF_KEY, b); // value to store
        editor.commit();
        showToast("isBinded = " + b);
    }

    private void log(String s) {
        Log.d(TAG, s);
    }

    private void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }



}
