package org.example.javalab.component;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Cache {
    private final Map<String,Object> hashMap;
    private final int maxSize = 20;

    public Cache() {
        this.hashMap = new HashMap<>();
    }
    public void put(String key, Object value){
        if(hashMap.size()==maxSize){
            removeOldestKey();
        }
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
    private void removeOldestKey(){
        String oldestKey = null;
        for(String key : hashMap.keySet()){
            if(oldestKey==null||oldestKey.hashCode()< oldestKey.hashCode()){
                oldestKey=key;
            }
        }
        if(oldestKey!=null){
            hashMap.remove(oldestKey);
        }
    }
}
