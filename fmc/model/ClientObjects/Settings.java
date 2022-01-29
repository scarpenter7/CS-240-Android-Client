package cs240.fmc.model.ClientObjects;

public class Settings {
    private boolean lifeStoryLines = true;
    private boolean familyTreeLines = true;
    private boolean spouseLines = true;
    private boolean fatherSide = true;
    private boolean  motherSide = true;
    private boolean maleEvents = true;
    private boolean femaleEvents = true;
    private boolean userLoggedIn = false;

    public void resetFilters() {
        lifeStoryLines = true;
        familyTreeLines = true;
        spouseLines = true;
        fatherSide = true;
        motherSide = true;
        maleEvents = true;
        femaleEvents = true;
    }

    public boolean lifeStoryLinesOn() {
        return lifeStoryLines;
    }

    public void setLifeStoryLines(boolean lifeStoryLines) {
        this.lifeStoryLines = lifeStoryLines;
    }

    public boolean familyTreeLinesOn() {
        return familyTreeLines;
    }

    public void setFamilyTreeLines(boolean familyTreeLines) {
        this.familyTreeLines = familyTreeLines;
    }

    public boolean spouseLineOn() {
        return spouseLines;
    }

    public void setSpouseLine(boolean spouseLines) {
        this.spouseLines = spouseLines;
    }

    public boolean fatherSideOn() {
        return fatherSide;
    }

    public void setFatherSide(boolean fatherSide) {
        this.fatherSide = fatherSide;
    }

    public boolean motherSideOn() {
        return motherSide;
    }

    public void setMotherSide(boolean motherSide) {
        this.motherSide = motherSide;
    }

    public boolean maleEventsOn() {
        return maleEvents;
    }

    public void setMaleEvents(boolean maleEvents) {
        this.maleEvents = maleEvents;
    }

    public boolean femaleEventsOn() {
        return femaleEvents;
    }

    public void setFemaleEvents(boolean femaleEvents) {
        this.femaleEvents = femaleEvents;
    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
    }
}
