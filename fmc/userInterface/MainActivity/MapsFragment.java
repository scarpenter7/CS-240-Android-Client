package cs240.fmc.userInterface.MainActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cs240.fmc.R;
import cs240.fmc.model.ClientObjects.DataCache;
import cs240.fmc.model.ServerObjects.Event;
import cs240.fmc.model.ServerObjects.Person;
import cs240.fmc.model.ClientObjects.Settings;
import cs240.fmc.userInterface.PersonActivity;

import static com.joanzapata.iconify.fonts.FontAwesomeIcons.fa_female;
import static com.joanzapata.iconify.fonts.FontAwesomeIcons.fa_male;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback,
        GoogleMap.OnMarkerClickListener{


    private GoogleMap map;
    private ImageView genderPicView;
    private Drawable genderIcon;
    private TextView eventInfo;
    private Event selectedEvent;
    private Set<Marker> eventMarkers = new HashSet<>();
    List<Polyline> mapLines = new ArrayList<>();

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        @Override
        public void onMapReady(GoogleMap googleMap) {

            try {
                String eventID = getArguments().getString("eventID");
                DataCache cache = DataCache.getInstance();
                Event event = cache.getEvents().get(eventID);
                float latitude = event.getLatitude();
                float longitude = event.getLongitude();
                LatLng eventLocation = new LatLng(latitude, longitude);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(eventLocation));
                Marker eventMarker = findMarker(event);
                onMarkerClick(eventMarker);
            }
            catch (NullPointerException e) {
                LatLng origin = new LatLng(0, 0);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initializeEventInfoNonSelected(view);
        return view;
    }

    private void initializeEventInfoNonSelected(View view) {
        genderPicView = view.findViewById(R.id.genderIcon);
        genderIcon = new IconDrawable(getActivity(), fa_male).colorRes(R.color.undecidedGender).sizeDp(60);
        genderPicView.setImageDrawable(genderIcon);
        eventInfo = view.findViewById(R.id.eventInfo);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);
        DataCache cache = DataCache.getInstance();
        loadMapPinsAndLines();
        try {
            String eventID = getArguments().getString("eventID");
            Event event = cache.getEvents().get(eventID);

            float latitude = event.getLatitude();
            float longitude = event.getLongitude();
            LatLng eventLocation = new LatLng(latitude, longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(eventLocation));
            Marker eventMarker = findMarker(event);
            onMarkerClick(eventMarker);
        }
        catch (NullPointerException e) {
            LatLng origin = new LatLng(0, 0);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
        }
    }

    public Marker findMarker(Event event) {
        for (Marker marker: eventMarkers) {
            if (marker.getTag().equals(event)) {
                return marker;
            }
        }
        return null;
    }

    @Override
    public void onMapLoaded() {
        // You probably don't need this callback. It occurs after onMapReady and I have seen
        // cases where you get an error when adding markers or otherwise interacting with the map in
        // onMapReady(...) because the map isn't really all the way ready. If you see that, just
        // move all code where you interact with the map (everything after
        // map.setOnMapLoadedCallback(...) above) to here.
    }

    public void loadMapPinsAndLines() {
        DataCache cache = DataCache.getInstance();
        clearMapMarkers();
        clearMapLines();
        for (Map.Entry<String,Event> entry : cache.getEvents().entrySet()) {
            Event event = entry.getValue();
            Person person = cache.getPersonFromEvent(event);
            if (cache.personEventsAllowed(person)) {
                addPin(event);
            }
        }
    }

    private void addPin(Event event) {
        DataCache cache = DataCache.getInstance();
        String eventType = event.getEventType().toLowerCase();
        LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
        MarkerOptions marker = new MarkerOptions().position(location);
        float color = 0;
        if (!cache.getEventTypes().contains(eventType)) {
            cache.getEventTypes().add(eventType);
            color = cache.getNextColor();
            cache.getEventTypeColors().put(eventType, color);
        }
        else {
            color = cache.getEventTypeColors().get(eventType);
        }
        marker.icon(BitmapDescriptorFactory.defaultMarker(color));

        Marker pin = map.addMarker(marker);
        pin.setTag(event);
        eventMarkers.add(pin);
        map.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker == null) {
            return true;
        }
        Event selectedEvent = (Event)marker.getTag();
        setSelectedEvent(selectedEvent);
        final String eventString = selectedEvent.toString();
        eventInfo.setText(eventString);
        setGenderIcon(selectedEvent);
        eventInfo.setTag(selectedEvent);
        View.OnClickListener listener = eventInfoClicker(eventInfo);
        eventInfo.setOnClickListener(listener);
        genderPicView.setOnClickListener(listener);

        DataCache cache = DataCache.getInstance();
        Settings settings = cache.getSettings();
        clearMapLines();
        drawLines(settings, selectedEvent);
        return true;
    }

    private void setGenderIcon(Event selectedEvent) {
        DataCache cache = DataCache.getInstance();
        Person person = cache.getPersonFromEvent(selectedEvent);
        String gender = person.getGender();
        if (gender.equals("m")) {
            genderIcon = new IconDrawable(getActivity(), fa_male).colorRes(R.color.maleIcon).sizeDp(60);
        }
        else {
            genderIcon = new IconDrawable(getActivity(), fa_female).colorRes(R.color.femaleIcon).sizeDp(60);
        }
        genderPicView.setImageDrawable(genderIcon);
    }

    public void setEventInfoNonSelected() {
        genderIcon = new IconDrawable(getActivity(), fa_male).colorRes(R.color.undecidedGender).sizeDp(60);
        genderPicView.setImageDrawable(genderIcon);
        eventInfo.setText(R.string.defaultEventSelection);
    }

    public void drawLines(Settings settings, Event selectedEvent) {
        if (settings.lifeStoryLinesOn()) {
            drawLifeStory(selectedEvent);
        }
        if (settings.familyTreeLinesOn()) {
            drawFamilyTreeLines(selectedEvent);
        }
        if (settings.spouseLineOn()) {
            drawSpouseLink(selectedEvent);
        }
    }

    private void drawLifeStory(Event selectedEvent) {
        DataCache cache = DataCache.getInstance();
        Person person = cache.getPersonFromEvent(selectedEvent);
        List<Event> personEvents = cache.getPersonEvents(person);
        for (int i = 0; i < personEvents.size() - 1; ++i) {
            Event startEvent = personEvents.get(i);
            LatLng startEventLocation = startEvent.getLocation();

            Event endEvent = personEvents.get(i + 1);
            LatLng endEventLocation = endEvent.getLocation();

            PolylineOptions line = new PolylineOptions()
                    .add(startEventLocation, endEventLocation).width(3).color(Color.GRAY);
            mapLines.add(map.addPolyline(line));
        }
    }

    private void drawFamilyTreeLines(Event selectedEvent) {
        DataCache cache = DataCache.getInstance();
        Person person = cache.getPersonFromEvent(selectedEvent);
        Person mother = cache.getPeople().get(person.getMotherID());
        Person father = cache.getPeople().get(person.getFatherID());
        if (cache.personEventsAllowed(mother)) {
            Event earliestMotherEvent = cache.getEarliestEvent(mother);
            drawAncestorLines(mother, earliestMotherEvent, selectedEvent,
                                Color.rgb(255, 153, 185), 1);
        }
        if (cache.personEventsAllowed(father)) {
            Event earliestFatherEvent = cache.getEarliestEvent(father);
            drawAncestorLines(father, earliestFatherEvent, selectedEvent,
                                Color.rgb(50, 132, 255), 1);
        }
    }

    private void drawAncestorLines(Person rootPerson, Event currentEvent, Event previousEvent,
                                   int lineColor, int generationNumber) {
        traverseTree(rootPerson, currentEvent, lineColor, generationNumber);

        LatLng startEventLocation = currentEvent.getLocation();
        LatLng endEventLocation = previousEvent.getLocation();
        float lineWidth = (float) (20.0/generationNumber);
        PolylineOptions line = new PolylineOptions()
                .add(startEventLocation, endEventLocation).width(lineWidth).color(lineColor);
        mapLines.add(map.addPolyline(line));
    }

    private void traverseTree(Person rootPerson, Event currentEvent, int lineColor, int generationNumber) {
        DataCache cache = DataCache.getInstance();
        if (rootPerson.getFatherID() != null && !rootPerson.getFatherID().equals("")) {
            String fatherID = rootPerson.getFatherID();
            Person father = cache.getPeople().get(fatherID);
            if (cache.personEventsAllowed(father)) {
                Event earliestFatherEvent = cache.getEarliestEvent(father);
                drawAncestorLines(father, earliestFatherEvent, currentEvent,
                        lineColor, generationNumber + 1);
            }
        }
        if (rootPerson.getMotherID() != null && !rootPerson.getMotherID().equals("")) {
            String motherID = rootPerson.getMotherID();
            Person mother = cache.getPeople().get(motherID);
            if (cache.personEventsAllowed(mother)) {
                Event earliestMotherEvent = cache.getEarliestEvent(mother);
                drawAncestorLines(mother, earliestMotherEvent, currentEvent,
                        lineColor, generationNumber + 1);
            }
        }
    }

    private void drawSpouseLink(Event event) {
        DataCache cache = DataCache.getInstance();
        Person person = cache.getPersonFromEvent(event);
        Person spouse = cache.getPeople().get(person.getSpouseID());

        if (spouse == null || !cache.personEventsAllowed(spouse)) { return; }

        List<Event> spouseEvents = cache.getPersonEvents(spouse);
        if (!spouseEvents.isEmpty()) {
            Event earliestSpouseEvent = spouseEvents.get(0);

            LatLng spouseEventLocation = earliestSpouseEvent.getLocation();
            LatLng eventLocation = event.getLocation();

            PolylineOptions line = new PolylineOptions()
                    .add(eventLocation, spouseEventLocation).width(4).color(Color.RED);
            mapLines.add(map.addPolyline(line));
        }
    }

    private void clearMapLines() {
        List<Polyline> copyList = new ArrayList<>(mapLines);
        for (Polyline line: copyList) {
            mapLines.remove(line);
            line.remove();
        }
    }

    private void clearMapMarkers() {
        Set<Marker> copySet = new HashSet<>(eventMarkers);
        for (Marker marker: copySet) {
            eventMarkers.remove(marker);
            marker.remove();
        }
    }

    private View.OnClickListener eventInfoClicker(View eventInfoTextView) {
        Event event = (Event)eventInfoTextView.getTag();
        final String personID = event.getPersonID();
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = PersonActivity.newPersonIntent(MapsFragment.this.getActivity(), personID);
                startActivity(intent);
            }
        };
        return listener;
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }
}