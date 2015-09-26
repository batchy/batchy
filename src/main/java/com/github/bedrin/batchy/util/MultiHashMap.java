package com.github.bedrin.batchy.util;

import java.util.*;

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

    public void addAll(MultiHashMap<K,V> that) {
        for (Map.Entry<K, List<V>> entry : that.entrySet()) {
            add(entry.getKey(), new IteratorEnumeration<V>(entry.getValue()));
        }
    }

    public List<V> add(K key, Enumeration<V> values) {
        List<V> existingList = get(key);
        List<V> newList;
        if (existingList == null) {
            newList = new ArrayList<V>();
        } else {
            newList = new ArrayList<V>(existingList);
        }
        while (values.hasMoreElements()) {
            newList.add(values.nextElement());
        }
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