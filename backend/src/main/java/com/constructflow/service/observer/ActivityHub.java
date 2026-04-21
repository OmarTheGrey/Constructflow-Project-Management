package com.constructflow.service.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Enum-based Singleton implementation of the Observer subject.
 *
 * Using an enum guarantees a single instance per classloader (JVM), is
 * thread-safe without explicit synchronisation, and is immune to both
 * reflection and serialisation attacks. This is the recommended Singleton
 * idiom in modern Java (Effective Java, Item 3).
 *
 * Subscribers register once; publishers fire events through the fixed
 * {@link #INSTANCE}. Observer callbacks are invoked synchronously — a
 * misbehaving observer is caught and logged rather than propagated.
 */
public enum ActivityHub {

    INSTANCE;

    private static final Logger log = LoggerFactory.getLogger(ActivityHub.class);

    private final List<ActivityObserver> observers = new CopyOnWriteArrayList<>();

    public void subscribe(ActivityObserver observer) {
        if (observer == null) return;
        observers.addIfAbsent(observer);
    }

    public void unsubscribe(ActivityObserver observer) {
        observers.remove(observer);
    }

    public void publish(Activity activity) {
        if (activity == null) return;
        for (ActivityObserver o : observers) {
            try {
                o.onActivity(activity);
            } catch (RuntimeException ex) {
                log.warn("Observer {} threw while handling {}: {}",
                        o.getClass().getSimpleName(),
                        activity.getClass().getSimpleName(),
                        ex.getMessage());
            }
        }
    }

    /** Testing helper — number of currently registered observers. */
    public int subscriberCount() {
        return observers.size();
    }

    /** Testing helper — drop every observer. */
    public void clear() {
        observers.clear();
    }
}
