package org.example.javalab.component;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Cache {
    private final Map<String,Object> hashMap;

    public Cache() {
        this.hashMap = new HashMap<>();
    }
    public void put(String key, Object value){
        hashMap.put(key, value);
    }
    public Object get(String key){
        return hashMap.get(key);
    }
    public void remove(String key){
        hashMap.remove(key);
    }
    public boolean contains(String key){
        return hashMap.containsKey(key);
    }
    public void clear(){
        hashMap.clear();
    }

}
