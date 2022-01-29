package cs240.fmc.userInterface.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.Marker;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import cs240.fmc.AsyncTasks.LoginAsyncTask;
import cs240.fmc.AsyncTasks.RegisterAsyncTask;
import cs240.fmc.R;
import cs240.fmc.model.ClientObjects.DataCache;
import cs240.fmc.model.ServerObjects.Event;
import cs240.fmc.result.LoginResult;
import cs240.fmc.result.RegisterResult;
import cs240.fmc.userInterface.SearchActivity;
import cs240.fmc.userInterface.SettingsActivity;

public class MainActivity extends AppCompatActivity implements LoginAsyncTask.Listener, RegisterAsyncTask.Listener {
    private LoginFragment loginFrag;
    private MapsFragment mapsFrag;
    private boolean userLoggedIn  = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        invalidateOptionsMenu();
        setContentView(R.layout.activity_main);
        Iconify.with(new FontAwesomeModule());
        FragmentManager fm = this.getSupportFragmentManager();
        launchLoginFrag(fm);
    }

    public void launchLoginFrag(FragmentManager fm) {
        loginFrag = (LoginFragment)fm.findFragmentById(R.id.loginFrag);
        if (loginFrag == null) {
            loginFrag = new LoginFragment();
            fm.beginTransaction()
                    .add(R.id.mainActivityFragHolder, loginFrag)
                    .commit();
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onError(Error e) {}

    @Override
    public void onRegisterSuccess(RegisterResult result) {
        //switch to map frag
        userLoggedIn = true;
        DataCache.getInstance().getSettings().setUserLoggedIn(true);
        launchMapFrag();
        invalidateOptionsMenu();
    }

    @Override
    public void onLoginSuccess(LoginResult result) {
        //switch to map frag
        userLoggedIn = true;
        DataCache.getInstance().getSettings().setUserLoggedIn(true);
        launchMapFrag();
        invalidateOptionsMenu();
    }

    private void launchMapFrag() {
        MapsFragment mapsFragment = new MapsFragment();
        Bundle args = new Bundle();
        mapsFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mapsFrag = mapsFragment;
        transaction.replace(R.id.mainActivityFragHolder, mapsFrag)
                .addToBackStack(null)
                .commit();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (userLoggedIn) {
            new MenuInflater(this).inflate(R.menu.map_menu, menu);
        }
        else {
            new MenuInflater(this).inflate(R.menu.login_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settingsButton:
                Intent SettingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(SettingsIntent);
                return true;
            case R.id.searchButton:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Event selectedEvent = null;
        if (mapsFrag != null) {
            selectedEvent = mapsFrag.getSelectedEvent();
        }
        if (selectedEvent != null) {
            mapsFrag.loadMapPinsAndLines();
            Marker selectedMarker = mapsFrag.findMarker(selectedEvent);
            mapsFrag.onMarkerClick(selectedMarker);
            if (selectedMarker == null) {
                mapsFrag.setEventInfoNonSelected();
            }
        }
        else if (mapsFrag != null) {
            mapsFrag.loadMapPinsAndLines();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        DataCache cache = DataCache.getInstance();
        userLoggedIn = cache.getSettings().isUserLoggedIn();
        if (!userLoggedIn) {
            setContentView(R.layout.activity_main);
            invalidateOptionsMenu();
            Iconify.with(new FontAwesomeModule());
            FragmentManager fm = this.getSupportFragmentManager();
            launchLoginFrag(fm);
        }
    }

    @Override
    public void onRegisterFail() {}

    @Override
    public void onDoneRetrieve(RegisterResult result) {}

    @Override
    public void onLoginFail() {}

    @Override
    public void onDoneRetrieve(LoginResult result) {}
}