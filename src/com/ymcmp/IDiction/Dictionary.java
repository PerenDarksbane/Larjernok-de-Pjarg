/*
 * The MIT License
 *
 * Copyright 2015 plank.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ymcmp.IDiction;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 *
 * @author plank
 * @param <K>
 * @param <V>
 */
public class Dictionary<K, V> {

    private final ArrayList<Map.Entry<K, V>> VOCABULARY = new ArrayList<>();

    public boolean add(Map.Entry<K, V> newEntry) {
        return VOCABULARY.add(newEntry);
    }

    public boolean addAll(Collection<Map.Entry<K, V>> col) {
        return VOCABULARY.addAll(col);
    }

    public void addAll(Map<K, V> map) {
        map.entrySet().stream().forEach((newEntry) -> VOCABULARY.add(newEntry));
    }

    public boolean containsKey(K key) {
        return VOCABULARY.stream().anyMatch((entry) -> (entry.getKey().equals(key)));
    }

    public boolean containsValue(V value) {
        return VOCABULARY.stream().anyMatch((entry) -> (entry.getValue().equals(value)));
    }

    public Map.Entry<K, V> getIndex(int i) {
        return VOCABULARY.get(i);
    }

    public List<Map.Entry<K, V>> getRange(int low, int high) {
        return VOCABULARY.subList(low, high);
    }

    public List<V> getValues() {
        List<V> values = new ArrayList<>();
        VOCABULARY.forEach((item) -> values.add(item.getValue()));
        return values;
    }

    public List<K> getKeys() {
        List<K> keys = new ArrayList<>();
        VOCABULARY.forEach((item) -> keys.add(item.getKey()));
        return keys;
    }

    public List<V> getValues(K key) {
        if (!containsKey(key)) {
            return null;
        }
        List<V> values = new ArrayList<>();
        VOCABULARY.stream().filter((entry) -> (entry.getKey().equals(key))).forEach((entry) -> {
            values.add(entry.getValue());
        });
        return values;
    }

    public List<K> getKeys(V value) {
        if (!containsValue(value)) {
            return null;
        }
        List<K> keys = new ArrayList<>();
        VOCABULARY.stream().filter((entry) -> (entry.getValue().equals(value))).forEach((entry) -> {
            keys.add(entry.getKey());
        });
        return keys;
    }

    public void sort() {
        VOCABULARY.sort((Map.Entry<K, V> o1, Map.Entry<K, V> o2) -> {
            return (o1.getKey() + "").compareTo(o2.getKey() + "");
        });
    }

    public int removeDupByKey() {
        int dupCount = 0;
        Map.Entry<K, V> lastElement = new SimpleEntry<>(null, null);
        for (int i = 0; i < VOCABULARY.size(); i++) {
            if (VOCABULARY.get(i).getKey().equals(lastElement.getKey())) {
                VOCABULARY.remove(i);
                dupCount++;
            }
            lastElement = VOCABULARY.get(i);
        }
        return dupCount;
    }

    public int removeDupByValue() {
        int dupCount = 0;
        Map.Entry<K, V> lastElement = new SimpleEntry<>(null, null);
        for (int i = 0; i < VOCABULARY.size(); i++) {
            if (VOCABULARY.get(i).getValue().equals(lastElement.getValue())) {
                VOCABULARY.remove(i);
                dupCount++;
            }
            lastElement = VOCABULARY.get(i);
        }
        return dupCount;
    }

    public void trimToSize() {
        VOCABULARY.trimToSize();
    }

    public int size() {
        return VOCABULARY.size();
    }

    public boolean isEmpty() {
        return VOCABULARY.isEmpty();
    }

    public boolean contains(Map.Entry<K, V> o) {
        return VOCABULARY.contains(o);
    }

    public Object[] toArray() {
        return VOCABULARY.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return VOCABULARY.toArray(a);
    }

    public void clear() {
        VOCABULARY.clear();
    }

    public Iterator<Map.Entry<K, V>> iterator() {
        return VOCABULARY.iterator();
    }

    public void forEach(Consumer<? super Map.Entry<K, V>> action) {
        VOCABULARY.forEach(action);
    }

    @Override
    public boolean equals(Object o) {
        return VOCABULARY.equals(o);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.VOCABULARY);
        return hash;
    }

    public Stream<Map.Entry<K, V>> stream() {
        return VOCABULARY.stream();
    }

    public Stream<Map.Entry<K, V>> parallelStream() {
        return VOCABULARY.parallelStream();
    }

}
