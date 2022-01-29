package cs240.fmc.userInterface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;

import java.util.List;
import java.util.Map;

import cs240.fmc.R;
import cs240.fmc.model.ClientObjects.DataCache;
import cs240.fmc.model.ServerObjects.Event;
import cs240.fmc.model.ClientObjects.Family;
import cs240.fmc.model.ServerObjects.Person;

import static com.joanzapata.iconify.fonts.FontAwesomeIcons.fa_female;
import static com.joanzapata.iconify.fonts.FontAwesomeIcons.fa_male;

public class PersonActivity extends AppCompatActivity {
    private static final String EXTRA_PERSON_ID = "cs240.fmc.MainActivity.MapsFragment.personID";
    private TextView firstName;
    private TextView lastName;
    private TextView gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        ExpandableListView expandableListView = findViewById(R.id.personInformationExpandables);
        String personID = getIntent().getStringExtra(EXTRA_PERSON_ID);
        expandableListView.setAdapter(new ExpandableListAdapter(personID));

        firstName = findViewById(R.id.firstNameView);
        lastName = findViewById(R.id.lastNameView);
        gender = findViewById(R.id.genderView);

        DataCache cache = DataCache.getInstance();
        Person person = cache.getPeople().get(personID);
        setPersonTextViews(person);
        invalidateOptionsMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static Intent newPersonIntent(Context packageContext, String personID) {
        Intent intent = new Intent(packageContext, PersonActivity.class);
        intent.putExtra(EXTRA_PERSON_ID, personID);
        return intent;
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int LIFE_EVENTS_POSITION = 0;
        private static final int FAMILY_POSITION = 1;

        private List<Event> personEvents;
        private Family family;

        ExpandableListAdapter(String personID) {
            DataCache cache = DataCache.getInstance();
            Person person = cache.getPeople().get(personID);

            Map<String,Person> people = cache.getPeople();
            List<Person> childrenOfPerson = cache.getChildrenOfPerson(person);
            family = createFamily(people, childrenOfPerson, person);

            personEvents = cache.getPersonEvents(person);
        }

        private Family createFamily(Map<String,Person> people, List<Person> childrenOfPerson, Person person) {
            Person mother = people.get(person.getMotherID());
            Person father = people.get(person.getFatherID());
            Person spouse = people.get(person.getSpouseID());
            return new Family(father, mother, spouse, childrenOfPerson);
        }

        @Override
        public int getGroupCount() { return 2; }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_POSITION:
                    return personEvents.size();
                case FAMILY_POSITION:
                    return family.getEveryone().size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_POSITION:
                    return getString(R.string.lifeEvents);
                case FAMILY_POSITION:
                    return getString(R.string.family);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_POSITION:
                    return personEvents.get(childPosition);
                case FAMILY_POSITION:
                    return family.getEveryone().get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() { return false; }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case LIFE_EVENTS_POSITION:
                    titleView.setText(R.string.lifeEvents);
                    break;
                case FAMILY_POSITION:
                    titleView.setText(R.string.family);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case LIFE_EVENTS_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item, parent, false);
                    initializeLifeEventsView(itemView, childPosition);
                    break;
                case FAMILY_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.family_member_item, parent, false);
                    initializeFamilyView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeLifeEventsView(View lifeEventsView, final int childPosition) {
            TextView eventInfoView = lifeEventsView.findViewById(R.id.eventInfo);
            final Event event = personEvents.get(childPosition);
            final String eventInfo = event.toString();
            eventInfoView.setText(eventInfo);

            ImageView pinIcon = lifeEventsView.findViewById(R.id.leftPinIcon);
            pinIcon.setImageResource(R.drawable.pin_icon);

            lifeEventsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = EventActivity.newEventIntent(PersonActivity.this, event.getEventID());
                    startActivity(intent);
                }
            });
        }

        private void initializeFamilyView(View familyView, final int childPosition) {
            TextView personView = familyView.findViewById(R.id.personInfo);
            final Person person = family.getEveryone().get(childPosition);
            final String personInfo = family.personToString(person);
            personView.setText(personInfo);
            setGenderIcon(familyView, person);

            familyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = newPersonIntent(PersonActivity.this, person.getPersonID());
                    startActivity(intent);                }
            });
        }

        private void setGenderIcon(View familyView, Person person) {
            ImageView genderPicView = familyView.findViewById(R.id.leftGenderIcon);
            String gender = person.getGender();
            Drawable genderIcon;
            if (gender.equals("m")) {
                genderIcon = new IconDrawable(PersonActivity.this, fa_male)
                        .colorRes(R.color.maleIcon).sizeDp(60);
            }
            else {
                genderIcon = new IconDrawable(PersonActivity.this, fa_female)
                        .colorRes(R.color.femaleIcon).sizeDp(60);
            }
            genderPicView.setImageDrawable(genderIcon);
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }

    private void setPersonTextViews(Person person) {
        firstName.setText(person.getFirstName());
        lastName.setText(person.getLastName());
        if (person.getGender().equals("m")) {
            gender.setText(R.string.male);
        }
        else {
            gender.setText(R.string.female);
        }
    }
}