package nl.sander;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YamlObjectTest {

    @Test
    public void testSimpleRemove() {
        Map<Object, Object> mapValue = new HashMap<>();
        mapValue.put("app", "simple");
        mapValue.put("app.name", "noname");
        YamlObject yamlObject = YamlObject.of(mapValue);

        yamlObject.remove("app.name");
        assertEquals(1, mapValue.size());
        assertTrue(mapValue.containsKey("app"));
    }

    @Test
    public void testRemoveIf_notremoved() {
        Map<Object, Object> mapValue = new HashMap<>();
        mapValue.put("app", "simple");
        mapValue.put("app.name", "noname");
        YamlObject yamlObject = YamlObject.of(mapValue);

        yamlObject.removeIf("app.name == somename");
        assertEquals(2, mapValue.size());
        assertTrue(mapValue.containsKey("app"));
        assertTrue(mapValue.containsKey("app.name"));
    }

    @Test
    public void testRemoveIf_removed() {
        Map<Object, Object> mapValue = new HashMap<>();
        mapValue.put("app", "simple");
        mapValue.put("app.name", "noname");
        YamlObject yamlObject = YamlObject.of(mapValue);

        yamlObject.removeIf("app.name == noname");
        assertEquals(1, mapValue.size());
        assertTrue(mapValue.containsKey("app"));
    }

    @Test
    public void testRemoveIf_lookForKey() {
        Map<Object, Object> mapValue = new HashMap<>();
        mapValue.put("app", "simple");
        mapValue.put("app.name", "noname");
        YamlObject yamlObject = YamlObject.of(mapValue);

        yamlObject.removeIf("_key==app.name");
        assertEquals(1, mapValue.size());
        assertTrue(mapValue.containsKey("app"));
    }

    @Test
    public void testRemoveIf_lookForKey_negative() {
        Map<Object, Object> mapValue = new HashMap<>();
        mapValue.put("app", "simple");
        mapValue.put("app.name", "noname");
        YamlObject yamlObject = YamlObject.of(mapValue);

        yamlObject.removeIf("_key!=app");
        assertEquals(1, mapValue.size());
        assertTrue(mapValue.containsKey("app"));
    }

    @Test
    public void testRemoveIf_lookForValue() {
        Map<Object, Object> mapValue = new HashMap<>();
        mapValue.put("app", "simple");
        mapValue.put("app.name", "noname");
        YamlObject yamlObject = YamlObject.of(mapValue);

        yamlObject.removeIf("_value==noname");
        assertEquals(1, mapValue.size());
        assertTrue(mapValue.containsKey("app"));
    }

    @Test
    public void testRemoveIf_lookForValue_negative() {
        Map<Object, Object> mapValue = new HashMap<>();
        mapValue.put("app", "simple");
        mapValue.put("app.name", "noname");
        YamlObject yamlObject = YamlObject.of(mapValue);

        yamlObject.removeIf("_value != simple");
        assertEquals(1, mapValue.size());
        assertTrue(mapValue.containsKey("app"));
    }

    @Test
    public void testCompoundRemove() {
        Map<Object, Object> spec = new HashMap<>();
        spec.put("app", "simple");
        spec.put("app.name", "noname");

        Map<Object, Object> item = new HashMap<>();
        item.put("spec", spec);
        YamlObject yamlObject = YamlObject.of(item);

        yamlObject.remove("spec/app.name");
        assertEquals(1, spec.size());
        assertTrue(spec.containsKey("app"));
    }

    @Test
    public void testCompoundRemoveIf_lookForValue_negative() {
        Map<Object, Object> labels = new HashMap<>();
        labels.put("app", "simple");
        labels.put("app.name", "noname");

        Map<Object, Object> item = new HashMap<>();
        item.put("labels", labels);
        YamlObject yamlObject = YamlObject.of(item);

        yamlObject.removeIf("labels/_value != simple");
        assertEquals(1, labels.size());
        assertTrue(labels.containsKey("app"));
    }
}
