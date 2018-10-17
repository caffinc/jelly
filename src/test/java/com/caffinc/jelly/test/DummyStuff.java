package com.caffinc.jelly.test;

import com.caffinc.jelly.annotations.Slice;
import com.caffinc.jelly.core.Jellied;

import java.text.ParseException;

public class DummyStuff implements Jellied {
    @Slice
    public String getSomething() {
        return "SOMETHING!";
    }

    public String getSomeOtherThing() {
        return "SOME OTHER THING!";
    }

    @Slice
    public String someException() throws ParseException {
        throw new ParseException("EHEHEHEH!", 0);
    }

    @Slice
    public int doubleIt(int x) {
        return 2 * x;
    }
}
