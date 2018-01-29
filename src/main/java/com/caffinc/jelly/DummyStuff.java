package com.caffinc.jelly;

public class DummyStuff implements Jellied {
    @Slice
    public String getSomething() {
        return "SOMETHING!";
    }

    public String getSomeOtherThing() {
        return "SOME OTHER THING!";
    }
}
