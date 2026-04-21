package com.constructflow.service.observer;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Maintains in-memory tallies of activity types so dashboard widgets can poll
 * the hot counters without hitting the database on every refresh.
 */
@Component
public class DashboardCounterObserver implements ActivityObserver {

    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();

    @PostConstruct
    public void register() {
        ActivityHub.INSTANCE.subscribe(this);
    }

    @Override
    public void onActivity(Activity activity) {
        counters.computeIfAbsent(activity.getClass().getSimpleName(), k -> new AtomicLong())
                .incrementAndGet();
    }

    public long countOf(String activityType) {
        AtomicLong c = counters.get(activityType);
        return c == null ? 0L : c.get();
    }

    public Map<String, Long> snapshot() {
        Map<String, Long> out = new ConcurrentHashMap<>();
        counters.forEach((k, v) -> out.put(k, v.get()));
        return out;
    }
}
