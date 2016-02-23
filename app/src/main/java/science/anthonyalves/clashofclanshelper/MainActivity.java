package science.anthonyalves.clashofclanshelper;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveApi.DriveIdResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;
import science.anthonyalves.clashofclanshelper.controllers.GoogleApiAccount;
import science.anthonyalves.clashofclanshelper.utils.Constants;

public class MainActivity extends AppCompatActivity implements GoogleApiAccount.AccountLoader, NavigationView.OnNavigationItemSelectedListener {

    Toolbar mToolbar;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    public static GoogleApiAccount mGoogleAccount;

    CircleImageView mProfilePictureIV;
    static TextView mProfileEmailTV;
    TextView mProfileNameTV;

    public static View mView;
    static CoordinatorLayout mCoordLayout;

    SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mView = getWindow().getDecorView().getRootView();


        // init Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // init DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //init NavigationView
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        /*
        mProfilePictureIV = (CircleImageView) findViewById(R.id.profile_picture);
        mProfileEmailTV = (TextView) findViewById(R.id.profile_email);
        mProfileNameTV = (TextView) findViewById(R.id.profile_name);
        mCoordLayout = (CoordinatorLayout) findViewById(R.id.coord_layout);
        */

        // TODO ask which tool to be shown at startup in settings.
        mNavigationView.setCheckedItem(R.id.nav_building);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadDefaultScreen();

        //mGoogleAccount = new GoogleApiAccount(this, this);
    }

    private void loadDefaultScreen() {
        String defScreen = mSharedPreferences.getString(Constants.DEFAULT_SCREEN, getResources().getString(R.string.buildings));
        switch (defScreen) {
            case Constants.BUILDINGS:
                mNavigationView.getMenu().getItem(0).setChecked(true);
                loadFragment(BuildingsFragment.class, defScreen);
                break;
            case Constants.TROOPS:
                mNavigationView.getMenu().getItem(1).setChecked(true);
                loadFragment(TroopsFragment.class, defScreen);
                break;
            case Constants.TROOP_CALCULATOR:
                mNavigationView.getMenu().getItem(2).setChecked(true);
                loadFragment(TroopCalculatorFragment.class, defScreen);
                break;
            case Constants.ADVANCED_NOTIFICATIONS:
                mNavigationView.getMenu().getItem(3).setChecked(true);
                loadFragment(NotificationsFragment.class, defScreen);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
        if (!mGoogleAccount.getGoogleApiClient().isConnected()) {
            mGoogleAccount.connect();
        }
        */
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*
        if (!mGoogleAccount.getGoogleApiClient().isConnected()) {
            mGoogleAccount.disconnect();
        }
        */
    }

    @Override
    public void userConnectedCallback(Person person) {
        String profilePicUrl = person.getImage().getUrl();
        profilePicUrl = profilePicUrl.substring(0, profilePicUrl.length() - 2) + 150;
        Picasso.with(this).load(profilePicUrl).into(mProfilePictureIV);
        String email = Plus.AccountApi.getAccountName(mGoogleAccount.getGoogleApiClient());
        mProfileEmailTV.setText(email);
        mProfileNameTV.setText(person.getDisplayName());

        showSnackbar("Signed in as " + email, Snackbar.LENGTH_LONG);

        //asdfr(Constants.CONFIG_TITLE);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!getSupportActionBar().getTitle().equals(mSharedPreferences.getString(Constants.DEFAULT_SCREEN, getResources().getString(R.string.buildings)))) {
                loadDefaultScreen();
            } else {
                super.onBackPressed();
            }
        }
    }

    public void GoogleDriveTest(final String title) {

        final Handler handler = new Handler();


        final String[] cont = new String[1];

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DriveFolder folder = Drive.DriveApi.getAppFolder(mGoogleAccount.getGoogleApiClient());
                DriveApi.MetadataBufferResult res = folder.listChildren(mGoogleAccount.getGoogleApiClient()).await();
                if (!res.getStatus().isSuccess()) {
                    Log.d("sdf", "Problem while retrieving files");
                    return;
                }
                Iterator<Metadata> iterator = res.getMetadataBuffer().iterator();
                Log.d("iterator", res.getMetadataBuffer().getCount() + "");
                while (iterator.hasNext()) {
                    final Metadata m = iterator.next();
                    DriveFile driveFile = Drive.DriveApi.getFile(mGoogleAccount.getGoogleApiClient(), m.getDriveId());
                    driveFile.delete(mGoogleAccount.getGoogleApiClient()).await();
                    Log.d("iterator", m.getTitle());
                }


            }

            private void b() {
                DriveIdResult result = Drive.DriveApi.fetchDriveId(mGoogleAccount.getGoogleApiClient(), Constants.CONFIG_TITLE).await();
                if (!result.getStatus().isSuccess()) {
                    // TODO make new fils
                    Log.d("test", "file not found");
                    DriveContentsResult result2 = Drive.DriveApi.newDriveContents(mGoogleAccount.getGoogleApiClient()).await();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(title)
                            .setMimeType("text/plain")
                            .build();
                    DriveFileResult result3 = Drive.DriveApi.getAppFolder(mGoogleAccount.getGoogleApiClient()).createFile(mGoogleAccount.getGoogleApiClient(), changeSet, result2.getDriveContents()).await();
                    readDriveFile(result3.getDriveFile());
                    return;
                }

                DriveFile file = Drive.DriveApi.getFile(mGoogleAccount.getGoogleApiClient(), result.getDriveId());
                readDriveFile(file);
            }

            private void a() {

                DriveContentsResult result = Drive.DriveApi.newDriveContents(mGoogleAccount.getGoogleApiClient()).await();
                if (!result.getStatus().isSuccess()) {
                    Log.d("test", "Error while trying to create new file contents");
                    return;
                }
                final DriveContents driveContents = result.getDriveContents();

                // Perform I/O off the UI thread.
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        // write content to DriveContents
                        OutputStream outputStream = driveContents.getOutputStream();
                        Writer writer = new OutputStreamWriter(outputStream);
                        try {
                            writer.write("Hello World!");
                            writer.close();
                        } catch (IOException e) {
                            Log.e("test", e.getMessage());
                        }

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle(Constants.CONFIG_TITLE)
                                .setMimeType("text/plain")
                                .setStarred(true).build();

                        // create a file on root folder
                        final DriveFileResult fileResult = Drive.DriveApi.getAppFolder(mGoogleAccount.getGoogleApiClient()).createFile(mGoogleAccount.getGoogleApiClient(), changeSet, driveContents).await();

                        if (!fileResult.getStatus().isSuccess()) {
                            Log.d("sdf", "Error while trying to create the file");
                            return;
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                showSnackbar("Created a file with content: " + fileResult.getDriveFile().getDriveId(), Snackbar.LENGTH_LONG);
                            }
                        });


                    }
                };

                t.start();

                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

            private void readDriveFile(DriveFile driveFile) {

                DriveContentsResult driveContentsResult = driveFile.open(mGoogleAccount.getGoogleApiClient(), DriveFile.MODE_READ_WRITE, null).await();
                if (!driveContentsResult.getStatus().isSuccess()) {
                    // TODO error
                    Log.d("test", "driveContentsResult not found");

                }
                DriveContents driveContents = driveContentsResult.getDriveContents();
                ParcelFileDescriptor parcelFileDescriptor = driveContents.getParcelFileDescriptor();

                FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                StringBuilder builder = new StringBuilder();
                int ch;
                try {
                    while ((ch = fileInputStream.read()) != -1) {
                        builder.append((char) ch);
                    }

                    // Append to the file.
                    FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
                    Writer writer = new OutputStreamWriter(fileOutputStream);
                    writer.write("hello world");

                    fileOutputStream.flush();
                    fileOutputStream.close();

                    while ((ch = fileInputStream.read()) != -1) {
                        builder.append((char) ch);
                    }


                    fileInputStream.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }


                final String finalContents1 = builder.toString();


                final Status status = driveContents.commit(mGoogleAccount.getGoogleApiClient(), null).await();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showSnackbar("Content: " + status.isSuccess(), Snackbar.LENGTH_LONG);
                    }
                });
            }
        });

        t.start();
    }

    public void test(View view) {
        if (mGoogleAccount.revokeAccess()) {
            showSnackbar("Revoked access on " + mProfileEmailTV.getText(), Snackbar.LENGTH_LONG);
        }
    }

    private void loadFragment(final Class<?> mClass, final String title) {
        Fragment fragment = null;

        getSupportActionBar().setTitle(title);

        try {
            fragment = (Fragment) mClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (fragment instanceof TestFragment) {
            ((TestFragment) fragment).setName(title);
        }
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_placeholder, fragment);
        fragmentTransaction.commit();
    }

    public void pickAccount(View view) {
        mGoogleAccount.chooseAccount();
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == Constants.RESOLVE_CONNECTION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mGoogleAccount.connect();
            } else {
                showToast("activity result failed");
            }
        }
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static GoogleApiAccount getmGoogleAccount() {
        return mGoogleAccount;
    }

    public static void showSnackbar(String message, int length) {
        Snackbar.make(mCoordLayout, message, length).show();
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        mDrawerLayout.closeDrawers();


        final String title = menuItem.getTitle().toString();


        if (title.equals(getSupportActionBar().getTitle())) {
            return true;
        }

        if (!title.equals(getString(R.string.start_coc))){
            menuItem.setChecked(true);
        }


        final Runnable test = new Runnable() {
            @Override
            public void run() {
                switch (menuItem.getItemId()) {
                    case R.id.nav_building:
                        loadFragment(BuildingsFragment.class, title);
                        break;
                    case R.id.nav_troop:
                        loadFragment(TroopsFragment.class, title);
                        break;
                    case R.id.nav_notification:
                        loadFragment(NotificationsFragment.class, title);
                        break;
                    case R.id.nav_calculator:
                        loadFragment(TroopCalculatorFragment.class, title);
                        break;
                    case R.id.nav_help_feedback:
                        loadFragment(TestFragment.class, title);
                        break;
                    case R.id.nav_settings:
                        loadFragment(SettingsFragment.class, title);
                        break;
                    case R.id.nav_start_coc:
                        PackageManager pm = getPackageManager();
                        Intent launchIntent = pm.getLaunchIntentForPackage(Constants.COC_PACKAGE_NAME);
                        if (launchIntent != null) {
                            startActivity(launchIntent);
                        } else {
                            Toast.makeText(MainActivity.this, "Clash of Clans not found :/", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };

//        ViewGroup view = (ViewGroup) findViewById(R.id.fragment_placeholder);
//        view.removeAllViews();

        new Thread() {
            @Override
            public void run() {

                //close the drawer before loading the new fragment.
                // smoother transition this way (drawer doesn't chop when closing)
                while (mDrawerLayout.isDrawerOpen(mNavigationView)) {

                }
                new Handler(Looper.getMainLooper()).post(test);
            }
        }.start();


        return true;
    }

    public void log(String s) {
        Log.d(getClass().getSimpleName(), s);
    }

}
