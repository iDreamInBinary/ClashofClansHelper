package science.anthonyalves.clashofclanshelper.services;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import science.anthonyalves.clashofclanshelper.utils.Constants;
import science.anthonyalves.clashofclanshelper.utils.MyFlashlight;
import science.anthonyalves.clashofclanshelper.utils.MyVibrator;

public class CoCNotificationListener extends AccessibilityService {

    private Context mContext;

    private String TAG;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        TAG = this.getClass().getSimpleName();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event.getEventType() != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            return;
        }

        String packageName = event.getPackageName().toString();
        log(packageName);
        if (!packageName.equals(Constants.COC_PACKAGE_NAME)) {
            return;
        }

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        if (am.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            return;
        }

        if (!PreKKNotificationDialog.isRunning) {
            Intent intent = new Intent(mContext, PreKKNotificationDialog.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }



    }

    @Override
    public void onInterrupt() {
        log("interupt");
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

