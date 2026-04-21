package com.constructflow.service.factory.report;

import java.util.HashMap;
import java.util.Map;

public class ReportContext {
    private final Map<String, Object> data = new HashMap<>();

    public ReportContext put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }

    public Map<String, Object> all() {
        return Map.copyOf(data);
    }
}
