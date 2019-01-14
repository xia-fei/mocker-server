package org.wing.mocker.http.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ApiDataService {

    private Map<String, String> dataMap = new HashMap<>();

    private AtomicLong idCounter = new AtomicLong();

    public Long save(String data) {
        long id = idCounter.getAndIncrement();
        dataMap.put(String.valueOf(id), data);
        return id;
    }

    public String get(long id) {
        return dataMap.get(String.valueOf(id));
    }

}
