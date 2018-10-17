package com.caffinc.jelly.test;

import com.caffinc.jelly.core.Jelly;
import com.caffinc.jelly.core.JellyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Arrays;

public class TestJelly {
    private static final Logger LOG = LoggerFactory.getLogger(TestJelly.class);

    public static void main(String[] args) {
        /*
        Initialize the daemon
         */
        int port = 53558;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        Jelly.initialize(port, "0", Arrays.asList("http://localhost:53559/"), new JellyConfig());

        // Get the proxied object
        DummyStuff ds = new DummyStuff().getJelliedObject();

        // Runs remotely
        System.out.println(ds.getSomething());
        // Runs locally
        System.out.println(ds.getSomeOtherThing());
        // Runs remotely
        try {
            System.out.println(ds.someException());
        } catch (ParseException e) {
            LOG.warn("Exception as expected", e);
        }

        System.out.println(ds.doubleIt(2));
//        Jelly.unload();
    }
}
