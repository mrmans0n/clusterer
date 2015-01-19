package io.nlopez.clusterer;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by mrm on 19/1/15.
 */
public class ReversibleHashMap<K, V> implements Map<K, V> {

    private final HashMap<K, V> normalMap;
    private final HashMap<V, K> reverseMap;

    public ReversibleHashMap() {
        normalMap = new HashMap<>();
        reverseMap = new HashMap<>();
    }

    @Override
    public void clear() {
        normalMap.clear();
        reverseMap.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return normalMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return reverseMap.containsKey(value);
    }

    @NonNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return normalMap.entrySet();
    }

    @Override
    public V get(Object key) {
        return normalMap.get(key);
    }

    public K getKey(Object value) {
        return reverseMap.get(value);
    }

    @Override
    public boolean isEmpty() {
        return normalMap.isEmpty();
    }

    @NonNull
    @Override
    public Set<K> keySet() {
        return normalMap.keySet();
    }

    @Override
    public V put(K key, V value) {
        reverseMap.put(value, key);
        return normalMap.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        reverseMap.remove(key);
        return normalMap.remove(key);
    }

    @Override
    public int size() {
        return normalMap.size();
    }

    @NonNull
    @Override
    public Collection<V> values() {
        return normalMap.values();
    }

}
