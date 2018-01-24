package com.caffinc.jelly;

public class TestJelly {
    public static void main(String[] args) {
        DummyStuff ds = Jelly.build(new DummyStuff());
        System.out.println(ds.getSomething());
    }
}
