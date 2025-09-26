package com.aau.grouping_system.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

@Component // so we can do dependency injection
public class Database {
   private final Map<Integer, String> sessions = new ConcurrentHashMap<>();
   private final AtomicInteger idGenerator = new AtomicInteger();

   // Encapsulation & (maybe)Abstraction
    public void saveSession(String data) {
        int id = idGenerator.incrementAndGet();
        sessions.put(id, data);
    }

    public String getSession(Integer id) {
        return sessions.get(id);
    }

    public Map<Integer, String> getAllSessions() {
        return sessions;
    }

    public void deleteSession(Integer id) {
        sessions.remove(id);
    } 
}
