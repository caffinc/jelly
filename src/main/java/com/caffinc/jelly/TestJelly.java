package com.caffinc.jelly;

public class TestJelly {
    public static void main(String[] args) {
        DummyStuff ds = new DummyStuff().getJelliedObject();
        System.out.println(ds.getSomething());
        System.out.println(ds.getSomeOtherThing());
    }
}
