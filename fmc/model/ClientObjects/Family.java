package cs240.fmc.model.ClientObjects;

import java.util.ArrayList;
import java.util.List;

import cs240.fmc.model.ServerObjects.Person;

public class Family {
    private Person father;
    private Person mother;
    private Person spouse;
    private List<Person> children;
    private List<Person> everyone = new ArrayList<>();

    public Family(Person father, Person mother, Person spouse, List<Person> children) {

        this.children = children;
        if (father != null) {
            this.father = father;
            everyone.add(father);
        }
        if (mother != null) {
            this.mother = mother;
            everyone.add(mother);
        }
        if (spouse != null) {
            this.spouse = spouse;
            everyone.add(spouse);
        }
        everyone.addAll(children);
    }

    public String personToString(Person person) {
        String relationship;
        if (person.equals(father)) {
            relationship = "Father";
        }
        else if (person.equals(mother)) {
            relationship = "Mother";
        }
        else if (person.equals(spouse)) {
            relationship = "Spouse";
        }
        else if (everyone.contains(person)) {
            relationship = "Child";
        }
        else {
            relationship = "Error, person not in family!";
        }
        String personInfo = person.getFirstName() + " " + person.getLastName() + "\n" + relationship;
        return personInfo;
    }

    public Person getFather() {
        return father;
    }

    public Person getMother() {
        return mother;
    }

    public Person getSpouse() {
        return spouse;
    }

    public List<Person> getChildren() {
        return children;
    }

    public List<Person> getEveryone() {
        return everyone;
    }
}
