package nl.sander;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnionTest {

    @Test
    public void testGet() {
        Union stringIntegerUnion = new Union(String.class, Integer.class, Long.class);
        stringIntegerUnion.set("2");

        assertEquals("2", stringIntegerUnion.get());
    }

    @Test
    public void testGet2() {
        Union stringIntegerUnion = new Union(String.class, Integer.class, Long.class);
        stringIntegerUnion.set(1);
        int value = stringIntegerUnion.get();
        assertEquals(1, value);
    }

    @Test
    public void testMap() {
        Union union = new Union(String.class, Integer.class);
        union.set(1);
        StringBuilder result = new StringBuilder();
        union.when(Integer.class, v -> result.append("int ").append(v));
        union.when(String.class, v -> result.append("string ").append(v));
        assertEquals("int 1", result.toString());
    }
}