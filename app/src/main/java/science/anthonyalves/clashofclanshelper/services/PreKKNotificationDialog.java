package science.anthonyalves.clashofclanshelper.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import science.anthonyalves.clashofclanshelper.R;
import science.anthonyalves.clashofclanshelper.utils.Constants;
import science.anthonyalves.clashofclanshelper.utils.MyFlashlight;
import science.anthonyalves.clashofclanshelper.utils.MyVibrator;

public class PreKKNotificationDialog extends Activity implements MyVibrator.VibratorCallback, MyFlashlight.FlashlightCallback {

    private MyVibrator myVibrator;
    private MyFlashlight myFlashlight;

    private boolean isVibrationRunning = false;
    private boolean isLedRunning = false;

    public static boolean isRunning = false;
    AlertDialog alert;
    AlertDialog.Builder builder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context mContext = getApplicationContext();
        myVibrator = new MyVibrator(mContext, this);
        myFlashlight = new MyFlashlight(mContext, this);

        builder = new AlertDialog.Builder(this);
        builder.setTitle("New Clash of Clans Notification!");
        builder.setCancelable(false);
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                myFlashlight.stop();
                myVibrator.stop();
                dialog.dismiss();
                isRunning = false;
                finish();
            }
        });
        builder.setPositiveButton("Launch Clash", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PackageManager pm = getPackageManager();
                Intent launchIntent = pm.getLaunchIntentForPackage(Constants.COC_PACKAGE_NAME);
                if (launchIntent != null) {
                    startActivity(launchIntent);
                } else {
                    Toast.makeText(mContext, "Clash of Clans not found :/", Toast.LENGTH_SHORT).show();
                }
                myFlashlight.stop();
                myVibrator.stop();
                dialog.dismiss();
                isRunning = false;
                finish();
            }
        });
        alert = builder.create();

        isRunning = true;
        myFlashlight.start();
        myVibrator.start();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myFlashlight.isFlashlightRunning) {
            myFlashlight.stop();
        }

        if (myVibrator.isVibrationRunning) {
            myVibrator.stop();
        }

        isRunning = false;
    }

    @Override
    public void vibratorStatus(boolean b) {
        isVibrationRunning = b;
        checkDialog();
    }

    @Override
    public void flashlightStatus(boolean b) {
        isLedRunning = b;
        checkDialog();
    }

    /**
     * Make sure both the methods are finished before dismissing the entire alert
     */
    private void checkDialog() {
        if (!(isLedRunning || isVibrationRunning)) {
            alert.dismiss();
            isRunning = false;
            finish();
        }
    }
}
