package com.github.bedrin.batchy.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiHashMap<K,V> extends HashMap<K,List<V>> {

    public List<V> add(K key, V value) {
        List<V> existingList = get(key);
        List<V> newList;
        if (existingList == null) {
            newList = new ArrayList<V>();
        } else {
            newList = new ArrayList<V>(existingList);
        }
        newList.add(value);
        return put(key, newList);
    }

    public V getFirst(K key) {
        List<V> values = get(key);
        if (null == values || values.isEmpty()) return null;
        else return values.get(0);
    }

    public V getLast(K key) {
        List<V> values = get(key);
        if (null == values || values.isEmpty()) return null;
        else return values.get(values.size() - 1);
    }

    public V[] getArray(K key) {
        List<V> valuesList = get(key);
        if (null == valuesList) return null;
        else return (V[]) valuesList.toArray();
    }

    public Map<K,V[]> asArrayMap() {
        Map<K,V[]> map = new HashMap<K, V[]>();
        for (K key : keySet()) {
            map.put(key, getArray(key));
        }
        return map;
    }

}