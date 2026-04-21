package com.constructflow.service.observer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActivityHubTest {

    @BeforeEach
    void resetHub() {
        ActivityHub.INSTANCE.clear();
    }

    @AfterEach
    void tearDown() {
        ActivityHub.INSTANCE.clear();
    }

    // ---------- Singleton invariants ----------

    @Test
    void sameInstanceAcrossLookups() {
        ActivityHub a = ActivityHub.INSTANCE;
        ActivityHub b = ActivityHub.INSTANCE;
        assertSame(a, b, "ActivityHub.INSTANCE must resolve to the same object every time");
    }

    @Test
    void enumValuesContainsExactlyOneInstance() {
        ActivityHub[] values = ActivityHub.values();
        assertEquals(1, values.length, "Enum-based singleton must expose exactly one value");
        assertSame(ActivityHub.INSTANCE, values[0]);
    }

    @Test
    void reflectionCannotInstantiateASecondHub() {
        // Enum singletons cannot be instantiated reflectively — the JDK guards the constructor.
        Constructor<?>[] ctors = ActivityHub.class.getDeclaredConstructors();
        assertTrue(ctors.length > 0, "Enum should still expose a constructor to reflection");
        Constructor<?> ctor = ctors[0];
        ctor.setAccessible(true);
        assertThrows(IllegalArgumentException.class, () -> ctor.newInstance("ATTACKER", 99),
                "Attempting to instantiate an enum via reflection must fail");
    }

    @Test
    void serialisationRoundTripReturnsSameInstance() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(ActivityHub.INSTANCE);
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            Object deserialised = in.readObject();
            assertSame(ActivityHub.INSTANCE, deserialised,
                    "Serialisation round-trip of an enum singleton must yield the same instance");
        }
    }

    // ---------- Observer contract ----------

    @Test
    void subscribedObserversReceivePublishedActivities() {
        List<Activity> received = new ArrayList<>();
        ActivityObserver observer = received::add;

        ActivityHub.INSTANCE.subscribe(observer);
        assertEquals(1, ActivityHub.INSTANCE.subscriberCount());

        UUID taskId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        ActivityHub.INSTANCE.publish(new Activity.TaskCreated(taskId, projectId, "Pour foundation"));

        assertEquals(1, received.size());
        assertInstanceOf(Activity.TaskCreated.class, received.get(0));
        assertEquals(taskId, ((Activity.TaskCreated) received.get(0)).taskId());
    }

    @Test
    void allRegisteredObserversReceiveTheSameEvent() {
        List<Activity> a = new ArrayList<>();
        List<Activity> b = new ArrayList<>();
        ActivityHub.INSTANCE.subscribe(a::add);
        ActivityHub.INSTANCE.subscribe(b::add);

        ActivityHub.INSTANCE.publish(new Activity.TaskOverdue(UUID.randomUUID(), "Inspect scaffolding"));

        assertEquals(1, a.size());
        assertEquals(1, b.size());
        assertSame(a.get(0), b.get(0), "Observers receive the exact same event object");
    }

    @Test
    void unsubscribedObserverStopsReceivingEvents() {
        List<Activity> received = new ArrayList<>();
        ActivityObserver observer = received::add;

        ActivityHub.INSTANCE.subscribe(observer);
        ActivityHub.INSTANCE.publish(new Activity.TaskOverdue(UUID.randomUUID(), "Task A"));
        ActivityHub.INSTANCE.unsubscribe(observer);
        ActivityHub.INSTANCE.publish(new Activity.TaskOverdue(UUID.randomUUID(), "Task B"));

        assertEquals(1, received.size(), "Observer should only see events published while subscribed");
    }

    @Test
    void misbehavingObserverDoesNotPreventOthersFromReceiving() {
        List<Activity> good = new ArrayList<>();
        ActivityHub.INSTANCE.subscribe(a -> { throw new RuntimeException("boom"); });
        ActivityHub.INSTANCE.subscribe(good::add);

        ActivityHub.INSTANCE.publish(new Activity.TaskOverdue(UUID.randomUUID(), "Task Z"));

        assertEquals(1, good.size(), "A throwing observer must not block downstream observers");
    }
}
