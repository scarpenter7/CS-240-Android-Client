package cs240.fmc.request;

import cs240.fmc.model.ServerObjects.Event;
import cs240.fmc.model.ServerObjects.Person;
import cs240.fmc.model.ServerObjects.User;

/** Package of parameters passed into a LoadService
 *
 */
public class LoadRequest {
    private User[] users;
    private Person[] persons;
    private Event[] events;

    /** Constructor
     *
     * @param users Array of users to load in
     * @param persons Array of people to load in
     * @param events Array of events to load in
     */
    public LoadRequest(User[] users, Person[] persons, Event[] events) {
        this.users = users;
        this.persons = persons;
        this.events = events;
    }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public Person[] getPersons() {
        return persons;
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }
}
