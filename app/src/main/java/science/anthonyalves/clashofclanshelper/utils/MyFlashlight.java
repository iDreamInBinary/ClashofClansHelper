package science.anthonyalves.clashofclanshelper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

public class MyFlashlight {


    public interface FlashlightCallback {
        void flashlightStatus(boolean b);
    }

    private final Context mContext;
    FlashlightCallback mFlashlightCallback;

    public boolean isFlashlightRunning = false;
    private SharedPreferences mSharedPreferences;

    private String TAG = this.getClass().getSimpleName();

    Camera mCamera;
    boolean hasFlash;


    public MyFlashlight(Context context, FlashlightCallback callback) {
        mContext = context;
        mFlashlightCallback = callback;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        hasFlash = mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

    }

    @SuppressWarnings("deprecation")
    public void start() {
        boolean isLedOn = mSharedPreferences.getBoolean("led_switch", false);
        log("isLedOn" + isLedOn);
        if (!isLedOn) {
            return;
        }

        log("hasFlash" + hasFlash);
        if (!hasFlash) {
            log("Device does not have camera flash");
            return;
        }

        // just in case
        log("isFlashlightRunning" + isFlashlightRunning);
        if (isFlashlightRunning) {
            return;
        }

        final int ledPulse = Integer.parseInt(mSharedPreferences.getString("led_pulse", "100"));
        int ledDuration = Integer.parseInt(mSharedPreferences.getString("led_duration", "2000"));

        if (ledDuration == 0) {
            ledDuration = Integer.MAX_VALUE;
        }


        log("Starting Alert");
        final int finalLedDuration = ledDuration;

        final SurfaceTexture surfaceTexture = new SurfaceTexture(0);
        try {
            mCamera = Camera.open();
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Camera.Parameters params = mCamera.getParameters();

        new Thread() {
            @Override
            public void run() {
                isFlashlightRunning = true;
                mFlashlightCallback.flashlightStatus(isFlashlightRunning);

                mCamera.startPreview();

                int loops = finalLedDuration / (ledPulse * 2);
                for (int i = 0; i < loops && isFlashlightRunning; i++) {
                    try {
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCamera.setParameters(params);
                        Thread.sleep(ledPulse); // start the flash.

                        // then wait for flash to turn off
                        Thread.sleep(ledPulse);

                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(params);
                        // Off duration
                        Thread.sleep(ledPulse);


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(params);
                mCamera.stopPreview();
                mCamera.release();
                isFlashlightRunning = false;
                mFlashlightCallback.flashlightStatus(isFlashlightRunning);
            }
        }.start();
    }

    public void stop() {
        if (isFlashlightRunning) {
            isFlashlightRunning = false;
            mFlashlightCallback.flashlightStatus(isFlashlightRunning);
        }
    }

    private void log(String s) {
        Log.d(TAG, s);
    }
}
