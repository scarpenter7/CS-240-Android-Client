package cs240.fmc.userInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;

import java.util.List;

import cs240.fmc.R;
import cs240.fmc.model.ClientObjects.DataCache;
import cs240.fmc.model.ServerObjects.Event;
import cs240.fmc.model.ServerObjects.Person;

import static com.joanzapata.iconify.fonts.FontAwesomeIcons.fa_female;
import static com.joanzapata.iconify.fonts.FontAwesomeIcons.fa_male;
import static cs240.fmc.userInterface.EventActivity.newEventIntent;
import static cs240.fmc.userInterface.PersonActivity.newPersonIntent;

public class SearchActivity extends AppCompatActivity {

    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;

    private EditText searchBarEditText;
    private ImageView magnifyingGlassIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final RecyclerView searchRecyclerView =findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        magnifyingGlassIcon = findViewById(R.id.magnifyingGlassIcon);
        magnifyingGlassIcon.setImageResource(R.drawable.magnifying_glass_icon_gray);

        searchBarEditText = findViewById(R.id.searchBarEditText);
        TextWatcher textWatcher = createTextWatcher(searchRecyclerView);
        searchBarEditText.addTextChangedListener(textWatcher);
    }

    private TextWatcher createTextWatcher(final RecyclerView searchRecyclerView) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                launchSearch(searchRecyclerView);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };
        return textWatcher;
    }

    private void launchSearch(RecyclerView searchRecyclerView) {
        String searchString = searchBarEditText.getText().toString();

        DataCache cache = DataCache.getInstance();
        List<Person> people = cache.getPeopleSearch(searchString);
        List<Event> events = cache.getEventSearch(searchString);

        SearchAdapter adapter = new SearchAdapter(people, events);
        searchRecyclerView.setAdapter(adapter);
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<Person> people;
        private final List<Event> events;

        SearchAdapter(List<Person> people, List<Event> events) {
            this.people = people;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position) {
            return position < people.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person_search_item, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.event_search_item, parent, false);
            }
            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if (position < people.size()) {
                holder.bind(people.get(position));
            } else {
                holder.bind(events.get(position - people.size()));
            }
        }

        @Override
        public int getItemCount() {
            return people.size() + events.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView genderOrPinIcon;
        private final TextView eventOrPersonInfo;

        private final int viewType;
        private Person person;
        private Event event;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                eventOrPersonInfo = itemView.findViewById(R.id.personInfoSearch);
                genderOrPinIcon = itemView.findViewById(R.id.leftGenderIconSearch);
            }
            else {
                eventOrPersonInfo = itemView.findViewById(R.id.eventInfoSearch);
                genderOrPinIcon = itemView.findViewById(R.id.leftPinIconSearch);
                genderOrPinIcon.setImageResource(R.drawable.pin_icon);
            }
        }

        private void bind(Person person) {
            this.person = person;
            eventOrPersonInfo.setText(person.toString());
            setGenderIcon(person);
        }

        private void bind(Event event) {
            this.event = event;
            eventOrPersonInfo.setText(event.toString());
        }

        private void setGenderIcon(Person person) {
            String gender = person.getGender();
            Drawable genderIcon;
            if (gender.equals("m")) {
                genderIcon = new IconDrawable(SearchActivity.this, fa_male)
                        .colorRes(R.color.maleIcon).sizeDp(60);
            }
            else {
                genderIcon = new IconDrawable(SearchActivity.this, fa_female)
                        .colorRes(R.color.femaleIcon).sizeDp(60);
            }
            genderOrPinIcon.setImageDrawable(genderIcon);
        }

        @Override
        public void onClick(View view) {
            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                Intent intent = newPersonIntent(SearchActivity.this, person.getPersonID());
                startActivity(intent);
            } else {
                Intent intent = newEventIntent(SearchActivity.this, event.getEventID());
                startActivity(intent);
            }
        }
    }
}