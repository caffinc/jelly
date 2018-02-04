# jelly
## What the heck is Jelly?
Jelly is an experimental Remote Procedure Call system that tries to minimize boilerplate code in distributed systems.

Jelly's architecture is simple:
- Run several instances of the same application on multiple servers - these are slices. Everyone is a Client and a Server, capable of handling the method call locally.
- Requests made to a method on a *Jellied* object in a slice can get distributed to any of the other slices and run on the *Jellied* object on those slices.

The advantage is drastically simplified code for simple systems where heavy computation needs to be distributed across several instances, but minimal configuration is needed.

## Example
Let's take a look at a class that does something simple:

```
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
```

For a class to be a part of a distributed system, all it has to do is extend the `Jellied` interface, and apply the `@Slice` annotation on any method that needs to be distributed.

Next let's see how we use this:
```
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
        LOG.info(ds.getSomething());
        // Runs locally
        LOG.info(ds.getSomeOtherThing());
        // Runs remotely
        try {
            LOG.info(ds.someException());
        } catch (ParseException e) {
            LOG.warn("Exception as expected", e);
        }

        LOG.info(ds.doubleIt(2));
    }
}
```

That's it! If several instances of this application run on multiple servers who share their URLs with each other, a call to the method of one of the objects will get transparently sent over the line to one of the other servers according to the router's policy and executed, and the result (including Exceptions) get transmitted back to the caller.

## Upcoming features
- Improved routing and load balancing
- Automatic service discovery
- More documentation

## Why Jelly?
I want to build a system that has minimal configuration, and can be incorporated into a lot of existing code transparently to make distributed computing easy and quick. Plus it's fun and educational!

## More questions?
Shoot an email to admin@caffinc.com if you would like to play around with this code or help improve it.