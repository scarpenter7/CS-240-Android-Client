package cs240.fmc.model.ClientObjects;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import cs240.fmc.model.ServerObjects.Event;
import cs240.fmc.model.ServerObjects.Person;

public class DataCache {

    private static DataCache instance;

    private Map<String, Person> people = new HashMap<>();
    private Person user;
    private Map<String, Event> events = new HashMap<>();
    private List<String> eventTypes = new LinkedList<>();
    private Queue<Float> colors = new ArrayDeque<>();
    private Map<String, Float> eventTypeColors = new HashMap<>();
    private Set<String> paternalAncestors = new HashSet<>();
    private Set<String> maternalAncestors = new HashSet<>();
    private Settings settings = new Settings();

    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    public static void populateDataCache(ArrayList<Person> people, ArrayList<Event> events, String userPersonID) {
        DataCache cache = DataCache.getInstance();
        for (Person person: people) {
            cache.getPeople().put(person.getPersonID(), person);
        }
        for (Event event: events) {
            cache.getEvents().put(event.getEventID(), event);
        }
        Person userPerson = cache.getPeople().get(userPersonID);
        cache.setUser(userPerson);
        addPinColors();
        divideParentalAncestors();
        //System.out.println("DataCache populated");
    }

    private static void addPinColors() {
        DataCache cache = DataCache.getInstance();
        Queue<Float> colors = cache.getColors();
        cache.getEventTypes().add("birth");
        cache.getEventTypeColors().put("birth", BitmapDescriptorFactory.HUE_GREEN);
        cache.getEventTypes().add("marriage");
        cache.getEventTypeColors().put("marriage", BitmapDescriptorFactory.HUE_ROSE);
        cache.getEventTypes().add("death");
        cache.getEventTypeColors().put("death", BitmapDescriptorFactory.HUE_ORANGE);

        colors.add(BitmapDescriptorFactory.HUE_CYAN);
        colors.add(BitmapDescriptorFactory.HUE_MAGENTA);
        colors.add(BitmapDescriptorFactory.HUE_BLUE);
        colors.add(BitmapDescriptorFactory.HUE_RED);
        colors.add(BitmapDescriptorFactory.HUE_VIOLET);
        colors.add(BitmapDescriptorFactory.HUE_YELLOW);
        colors.add(BitmapDescriptorFactory.HUE_AZURE);
        colors.add(BitmapDescriptorFactory.HUE_GREEN);
        colors.add(BitmapDescriptorFactory.HUE_ROSE);
        colors.add(BitmapDescriptorFactory.HUE_ORANGE);
    }

    private static void divideParentalAncestors() {
        setMotherSide();
        setFatherSide();
    }

    private static void setMotherSide() {
        DataCache cache = DataCache.getInstance();
        Person user = cache.getUser();
        String motherID = user.getMotherID();
        Person mother = cache.getPeople().get(motherID);
        traverseFamilyTree(mother, "mother");
    }

    private static void setFatherSide() {
        DataCache cache = DataCache.getInstance();
        Person user = cache.getUser();
        String fatherID = user.getFatherID();
        Person father = cache.getPeople().get(fatherID);
        traverseFamilyTree(father, "father");
    }

    private static void traverseFamilyTree(Person rootPerson, String parentalSide) {
        DataCache cache = DataCache.getInstance();
        if (rootPerson == null) {
            return;
        }
        if (rootPerson.getFatherID() != null && !rootPerson.getFatherID().equals("")) {
            String fatherID = rootPerson.getFatherID();
            Person father = cache.getPeople().get(fatherID);
            traverseFamilyTree(father, parentalSide);
        }
        if (rootPerson.getMotherID() != null && !rootPerson.getMotherID().equals("")) {
            String motherID = rootPerson.getMotherID();
            Person mother = cache.getPeople().get(motherID);
            traverseFamilyTree(mother, parentalSide);
        }
        String personID = rootPerson.getPersonID();
        if (parentalSide.equals("mother")) {
            cache.getMaternalAncestors().add(personID);
        }
        else {
            cache.getPaternalAncestors().add(personID);
        }
    }

    public  DataCache() {}

    public Map<String, Person> getPeople() {
        return people;
    }

    public Person getPersonFromEvent(Event event) {
        String personID = event.getPersonID();
        Person person = people.get(personID);
        return person;
    }

    public void setPeople(Map<String, Person> people) {
        this.people = people;
    }

    public List<Person> getChildrenOfPerson(Person person) {
        List<Person> childrenOfPerson = new ArrayList<>();
        String personID = person.getPersonID();
        for (Person child: people.values()) {
            String childMotherID = child.getMotherID();
            String childFatherID = child.getFatherID();

            if (childMotherID != null && childMotherID.equals(personID)) {
                childrenOfPerson.add(child);
            }
            else if (childFatherID != null && childFatherID.equals(personID)) {
                childrenOfPerson.add(child);
            }
        }
        return childrenOfPerson;
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public List<Event> getPersonEvents(Person person) {
        List<Event> personEvents = new ArrayList<>();
        if (personEventsAllowed(person)) {
            String personID = person.getPersonID();
            for (Event event: events.values()) {
                if (event.getPersonID().equals(personID)) {
                    personEvents.add(event);
                }
            }
            Collections.sort(personEvents);
            return personEvents;
        }
        return personEvents;
    }

    public Event getEarliestEvent(Person person) {
        List<Event> personEvents = getPersonEvents(person);
        if (!personEvents.isEmpty()) {
            return personEvents.get(0);
        }
        return null;
    }

    public List<String> getEventTypes() {
        return eventTypes;
    }

    public Queue<Float> getColors() {
        return colors;
    }

    public Person getUser() {
        return user;
    }

    public void setUser(Person user) {
        this.user = user;
    }

    public Map<String, Float> getEventTypeColors() {
        return eventTypeColors;
    }

    public Float getNextColor() {
        float color = colors.remove();
        colors.add(color);
        return color;
    }

    public Set<String> getPaternalAncestors() {
        return paternalAncestors;
    }

    public Set<String> getMaternalAncestors() {
        return maternalAncestors;
    }

    public Settings getSettings() {
        return settings;
    }

    public boolean personEventsAllowed(Person person) {
        if (person == null) {
            return false;
        }

        String gender = person.getGender();
        if ((gender.equals("m") && !settings.maleEventsOn()) ||
                (gender.equals("f") && !settings.femaleEventsOn())) {
            return false;
        }

        String personID = person.getPersonID();
        boolean personInPaternalSide = paternalAncestors.contains(personID);
        boolean personInMaternalSide = maternalAncestors.contains(personID);
        if ((personInPaternalSide && !settings.fatherSideOn()) ||
                (personInMaternalSide && !settings.motherSideOn())) {
            return false;
        }

        return true;
    }

    public List<Person> getPeopleSearch(String searchString) {
        searchString = searchString.toLowerCase();
       Collection<Person> peopleList = getPeopleList();
        ArrayList<Person> searchPeopleList = new ArrayList<>();

        for (Person person: peopleList) {
            String personName = person.toString().toLowerCase();
            if (personName.contains(searchString)) {
                searchPeopleList.add(person);
            }
        }

        return searchPeopleList;
    }

    private Collection<Person> getPeopleList() {
        Collection<Person> peopleList;
        peopleList = people.values();
        return peopleList;
    }

    public ArrayList<Event> getEventSearch(String searchString) {
        searchString = searchString.toLowerCase();
        ArrayList<Event> peopleList = getAllowedEventsList();
        ArrayList<Event> searchEventList = new ArrayList<>();

        for (Event event: peopleList) {
            String eventInfo = event.toString().toLowerCase();
            if (eventInfo.contains(searchString)) {
                searchEventList.add(event);
            }
        }

        return searchEventList;
    }

    public ArrayList<Event> getAllowedEventsList() {
        Collection<Event> allEventsList = events.values();
        ArrayList<Event> eventsAllowed = new ArrayList<>();
        for (Event event: allEventsList) {
            Person person = getPersonFromEvent(event);
            if (personEventsAllowed(person)) {
                eventsAllowed.add(event);
            }
        }
        return eventsAllowed;
    }
}
