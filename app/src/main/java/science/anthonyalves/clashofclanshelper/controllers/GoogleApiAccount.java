package science.anthonyalves.clashofclanshelper.controllers;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import science.anthonyalves.clashofclanshelper.utils.Constants;

public class GoogleApiAccount implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public interface AccountLoader {
        void userConnectedCallback(Person person);
    }

    AccountLoader mAccountLoader;

    GoogleApiClient mGoogleApiClient;
    Activity mActivity;

    public GoogleApiAccount(Activity activity, AccountLoader accountLoader) {
        mActivity = activity;
        mAccountLoader = accountLoader;
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(Drive.API)
                .addApi(Plus.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public boolean disconnect(){
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            return true;
        }
        return false;
    }

    public boolean revokeAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            mGoogleApiClient.connect();
                        }
                    });
            return true;
        }
        return false;
    }

    public boolean connect() {
        mGoogleApiClient.connect();
        return true;
    }

    public boolean chooseAccount() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.clearDefaultAccountAndReconnect();
        }
        mGoogleApiClient.connect();
        return true;
    }

    public GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
        Person currentPerson = Plus.PeopleApi .getCurrentPerson(mGoogleApiClient);
        if (currentPerson != null) {
            mAccountLoader.userConnectedCallback(currentPerson);
        }

        // TODO load data from drive
    }

    @Override
    public void onConnectionSuspended(int i) {
        showToast("sus");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(mActivity, Constants.RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // TODO Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), mActivity, 0).show();
        }
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == Constants.RESOLVE_CONNECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                showToast("sdf");

            }
        }
    }

    public void showToast(String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }
}
