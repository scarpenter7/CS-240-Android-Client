package cs240.fmc.userInterface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cs240.fmc.R;
import cs240.fmc.userInterface.MainActivity.MainActivity;
import cs240.fmc.userInterface.MainActivity.MapsFragment;

public class EventActivity extends AppCompatActivity {
    private static final String EXTRA_EVENT_ID = "cs240.fmc.PersonActivity.eventID";
    private MapsFragment mapsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        FragmentManager fm = this.getSupportFragmentManager();
        String eventID = getIntent().getStringExtra(EXTRA_EVENT_ID);
        if (eventID != null) {
            launchMapsFrag(fm, eventID);
            return;
        }
        invalidateOptionsMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void launchMapsFrag(FragmentManager fm, String eventID) {
        mapsFrag = (MapsFragment)fm.findFragmentById(R.id.eventActivityFragHolder);
        Bundle bundle = new Bundle();
        bundle.putString("eventID", eventID);
        if (mapsFrag == null) {
            mapsFrag = new MapsFragment();
            mapsFrag.setArguments(bundle);
            fm.beginTransaction()
                    .add(R.id.eventActivityFragHolder, mapsFrag)
                    .commit();
        }
    }

    public static Intent newEventIntent(Context packageContext, String eventID) {
        Intent intent = new Intent(packageContext, EventActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, eventID);
        return intent;
    }
}