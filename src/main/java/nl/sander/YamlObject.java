package nl.sander;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//this is almost exactly unlike xpath
public class YamlObject {
    private final Union union = new Union(Map.class, List.class, String.class);

    public static YamlObject of(Object value) {
        YamlObject yamlObject = new YamlObject();
        yamlObject.union.set(value);
        return yamlObject;
    }

    public YamlObject get(String path) {
        YamlObject result = this;
        for (String element : path.split("/")) {
            result = result.doGget(element);
        }
        return result;
    }

    private YamlObject doGget(String key) {
        Optional<YamlObject> optionalYamlObject = union.map(List.class, l -> YamlObject.of(l.get(Integer.parseInt(key))));
        return optionalYamlObject
                .orElseGet(() -> union.map(Map.class, m -> YamlObject.of(m.get(key)))
                        .orElseThrow(() -> new IllegalArgumentException("Not a collection object: " + union)));
    }

    public void clear(String path) {
        get(path).clear();
    }

    public void clear() {
        union.when(Map.class, Map::clear);
        union.when(List.class, List::clear);
    }

    public void remove(String path) {
        executeOn(path, (fetchedObject, lastPathElement) -> {
            fetchedObject.union.when(Map.class, m -> m.remove(lastPathElement));
            fetchedObject.union.when(List.class, l -> l.remove(Integer.parseInt(lastPathElement)));
        });
    }

    private void executeOn(String path, BiConsumer<YamlObject, String> consumer) {
        if (path.contains("/")) {
            while (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            int lastSlash = path.lastIndexOf('/');
            String head = path.substring(0, lastSlash);
            YamlObject object = get(head);

            String tail = path.substring(lastSlash + 1);
            consumer.accept(object, tail);
        } else {
            consumer.accept(this, path);
        }
    }

    public void remove(int index) {
        union.when(List.class, l -> l.remove(index));
    }

    public YamlObject retain(String... keys) {
        List<String> keyList = Arrays.asList(keys);
        union.when(Map.class, m -> m.keySet().removeIf(key -> !keyList.contains(key)));
        return this;
    }

    public Map<String, Object> asMap() {
        return union.map(Map.class, m -> (Map<String, Object>) m).orElseThrow(() -> new RuntimeException("Not a map"));
    }

    public void removeIf(String path) {
        executeOn(path, this::removeIf);
    }

    private void removeIf(YamlObject fetchedYamlObject, String expression) {
        boolean opEquals;
        if (expression.contains("==")) {
            opEquals = true;
        } else if (expression.contains("!=")) {
            opEquals = false;
        } else {
            throw new IllegalArgumentException("syntax exception");
        }

        String[] tokens = expression.split("[!=]=");
        String key = tokens[0].trim();
        String operand = tokens[1].trim();

        if (key.equals("_key") || key.equals("_value")) {
            fetchedYamlObject.union.when(Map.class, m -> {
                for (Iterator<Map.Entry<?, ?>> iterator = m.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<?, ?> entry = iterator.next();
                    Object selector;
                    if (key.equals("_key")) {
                        selector = entry.getKey();
                    } else {
                        selector = entry.getValue();
                    }
                    if ((opEquals && selector.equals(operand)) || (!opEquals && !selector.equals(operand))) {
                        iterator.remove();
                    }
                }
            });
        } else {
            String value = fetchedYamlObject.get(key).toString();
            boolean remove = (operand.equals(value) && opEquals) || (!operand.equals(value) && !opEquals);
            if (remove) {
                fetchedYamlObject.union.when(Map.class, m -> m.remove(key));
            }
        }
    }

    public void forEach(Consumer<YamlObject> consumer) {
        union.when(List.class, list -> list.forEach(item -> consumer.accept(YamlObject.of(item))));
    }

    @Override
    public String toString() {
        return union.map(String.class, m -> m).orElse(super.toString());
    }
}
