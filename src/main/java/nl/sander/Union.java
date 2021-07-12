package nl.sander;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class Union {

    private final List<Class<?>> types = new ArrayList<>();
    private Object value;


    public Union(Class<?>... types) {
        if (Arrays.stream(types).distinct().count() != types.length) {
            throw new IllegalArgumentException("types must be unique");
        } else {
            for (Class<?> left : types) {
                for (Class<?> right : types) {
                    if (left != right && (left.isAssignableFrom(right) || right.isAssignableFrom(left))) {
                        throw new IllegalArgumentException("types must not belong to same class hierarchy");
                    }
                }
            }
            this.types.addAll(Arrays.asList(types));
        }
    }


    public void set(Object newValue) {
        if (newValue == null) {
            throw new NullPointerException();
        }
        checkType(newValue.getClass());
        this.value = newValue;
    }


    public <T> T get() {
        return (T) this.value;
    }

    public <T> void when(Class<T> type, Consumer<T> consumer) {
        checkType(type);
        if (type.isAssignableFrom(value.getClass())) {
            consumer.accept((T) value);
        }
    }

    public <T, R> Optional<R> map(Class<T> type, Function<T, R> function) {
        checkType(type);

        if (type.isAssignableFrom(value.getClass())) {
            return Optional.ofNullable(function.apply((T) value));
        } else {
            return Optional.empty();
        }
    }

    private void checkType(Class<?> someType) {
        Class<?> selectedType = null;
        for (Class<?> type : types) {
            if (type.isAssignableFrom(someType)) {
                selectedType = type;
            }
        }
        if (selectedType == null) {
            throw new IllegalArgumentException("unknown type for this union: " + someType.getName());
        }
    }

    @Override
    public String toString() {
        return "Union{ value=" + value.getClass() + '}';
    }
}
