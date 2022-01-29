package cs240.fmc.userInterface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import cs240.fmc.R;
import cs240.fmc.model.ClientObjects.DataCache;
import cs240.fmc.model.ClientObjects.Settings;

public class SettingsActivity extends AppCompatActivity {

    private Switch lifeStorySwitch;
    private Switch familyTreeSwitch;
    private Switch spouseLineSwitch;
    private Switch fatherSideSwitch;
    private Switch motherSideSwitch;
    private Switch maleEventSwitch;
    private Switch femaleEventSwitch;
    private RelativeLayout logoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeSwitches();
        initializeLogoutView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeSwitches() {
        lifeStorySwitch = findViewById(R.id.lifeStorySwitch);
        familyTreeSwitch = findViewById(R.id.familyTreeSwitch);
        spouseLineSwitch = findViewById(R.id.spouseLineSwitch);
        fatherSideSwitch = findViewById(R.id.fatherSideSwitch);
        motherSideSwitch = findViewById(R.id.motherSideSwitch);
        maleEventSwitch = findViewById(R.id.maleEventsSwitch);
        femaleEventSwitch = findViewById(R.id.femaleEventsSwitch);

        setSwitchesBySettings();
    }

    private void setSwitchesBySettings() {
        DataCache cache = DataCache.getInstance();
        Settings settings = cache.getSettings();

        boolean lifeStoryEnabled = settings.lifeStoryLinesOn();
        lifeStorySwitch.setChecked(lifeStoryEnabled);

        boolean familyTreeEnabled = settings.familyTreeLinesOn();
        familyTreeSwitch.setChecked(familyTreeEnabled);

        boolean spouseLineEnabled = settings.spouseLineOn();
        spouseLineSwitch.setChecked(spouseLineEnabled);

        boolean fatherSideEnabled = settings.fatherSideOn();
        fatherSideSwitch.setChecked(fatherSideEnabled);

        boolean motherSideEnabled = settings.motherSideOn();
        motherSideSwitch.setChecked(motherSideEnabled);

        boolean maleEventsEnabled = settings.maleEventsOn();
        maleEventSwitch.setChecked(maleEventsEnabled);

        boolean femaleEventsEnabled = settings.femaleEventsOn();
        femaleEventSwitch.setChecked(femaleEventsEnabled);

        createSwitchListeners();
    }

    private void createSwitchListeners() {
        DataCache cache = DataCache.getInstance();
        final Settings settings = cache.getSettings();

        View.OnClickListener switchListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            switch (view.getId()) {
                case R.id.lifeStorySwitch:
                    settings.setLifeStoryLines(!settings.lifeStoryLinesOn());
                    break;
                case R.id.familyTreeSwitch:
                    settings.setFamilyTreeLines(!settings.familyTreeLinesOn());
                    break;
                case R.id.spouseLineSwitch:
                    settings.setSpouseLine(!settings.spouseLineOn());
                    break;
                case R.id.fatherSideSwitch:
                    settings.setFatherSide(!settings.fatherSideOn());
                    break;
                case R.id.motherSideSwitch:
                    settings.setMotherSide(!settings.motherSideOn());
                    break;
                case R.id.maleEventsSwitch:
                    settings.setMaleEvents(!settings.maleEventsOn());
                    break;
                case R.id.femaleEventsSwitch:
                    settings.setFemaleEvents(!settings.femaleEventsOn());
                    break;
            }
            }
        };
        setSwitchListeners(switchListener);
    }

    private void setSwitchListeners(View.OnClickListener switchListener) {
        lifeStorySwitch.setOnClickListener(switchListener);
        familyTreeSwitch.setOnClickListener(switchListener);
        spouseLineSwitch.setOnClickListener(switchListener);
        fatherSideSwitch.setOnClickListener(switchListener);
        motherSideSwitch.setOnClickListener(switchListener);
        maleEventSwitch.setOnClickListener(switchListener);
        femaleEventSwitch.setOnClickListener(switchListener);
    }

    private void initializeLogoutView() {
        logoutView = findViewById(R.id.logoutOption);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataCache.getInstance().getSettings().setUserLoggedIn(false);
                finish();
            }
        };
        logoutView.setOnClickListener(listener);
    }
}