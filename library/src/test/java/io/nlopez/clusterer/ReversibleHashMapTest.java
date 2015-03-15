package io.nlopez.clusterer;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import io.nlopez.clusterer.utils.CustomTestRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mrm on 20/1/15.
 */
@RunWith(CustomTestRunner.class)
public class ReversibleHashMapTest {

    @Test
    public void test_put() {
        ReversibleHashMap<String, Integer> map = new ReversibleHashMap<>();
        map.put("One", 1);
        map.put("Two", 2);
        assertThat(map).hasSize(2).containsKeys("One", "Two").containsValue(1).containsValue(2);
        assertThat(map.getKey(1)).isEqualTo("One");
        assertThat(map.getKey(2)).isEqualTo("Two");
    }

    @Test
    public void test_clear() {
        ReversibleHashMap<String, Integer> map = new ReversibleHashMap<>();
        map.put("One", 1);
        map.put("Two", 2);
        assertThat(map).hasSize(2);
        map.clear();
        assertThat(map).isEmpty();
    }

    @Test
    public void test_put_all() {
        HashMap<String, Integer> tempMap = new HashMap<>();
        tempMap.put("One", 1);
        tempMap.put("Two", 2);

        ReversibleHashMap<String, Integer> map = new ReversibleHashMap<>();
        map.putAll(tempMap);

        assertThat(map).hasSize(2).containsKeys("One", "Two");
        assertThat(map.getKey(1)).isEqualTo("One");
        assertThat(map.getKey(2)).isEqualTo("Two");
    }

    @Test
    public void test_remove() {
        ReversibleHashMap<String, Integer> map = new ReversibleHashMap<>();
        map.put("One", 1);
        map.put("Two", 2);
        assertThat(map).hasSize(2).containsKeys("One", "Two");
        map.remove("One");
        assertThat(map).hasSize(1).containsKey("Two");
        map.remove("Two");
        assertThat(map).isEmpty();
    }
}
