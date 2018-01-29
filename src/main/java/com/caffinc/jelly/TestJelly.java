package com.caffinc.jelly;

import com.caffinc.jelly.resources.JellyResource;
import com.caffinc.jetter.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestJelly {
    private static final Logger LOG = LoggerFactory.getLogger(TestJelly.class);

    public static void main(String[] args) {
        try {
            new Api(53559).setBaseUrl("/").addServiceResource(JellyResource.class).enableCors().startNonBlocking();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to start Jelly Service", e);
        }

        DummyStuff ds = new DummyStuff().getJelliedObject();
        System.out.println(ds.getSomething());
        System.out.println(ds.getSomeOtherThing());
        try {
            System.out.println(ds.someException());
        } catch (Exception e) {
            LOG.warn("Exception as expected", e);
        }
    }
}
