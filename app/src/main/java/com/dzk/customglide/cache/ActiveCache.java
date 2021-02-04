package com.dzk.customglide.cache;

import com.dzk.customglide.resource.Value;
import com.dzk.customglide.resource.ValueCallback;
import com.dzk.customglide.utils.Tool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ActiveCache {
    private ValueCallback valueCallback;
    private Map<String,Value> mapList = new HashMap<>();
    public ActiveCache(ValueCallback callback) {
        this.valueCallback = callback;
    }

    public Value get(String key) {
        return mapList.get(key);
    }

    public void put(String key, Value value) {
        Tool.checkNotEmpty(key);
        value.setCallback(this.valueCallback);
        mapList.put(key,value);
    }

    public void recycleActive() {
        Set<Map.Entry<String, Value>> entries = mapList.entrySet();
        Iterator<Map.Entry<String, Value>> iterator = entries.iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Value> entry = iterator.next();
            entry.getValue().recycle();
            mapList.remove(entry.getKey());
        }

    }
}
