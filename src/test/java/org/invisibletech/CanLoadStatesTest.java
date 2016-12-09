package org.invisibletech;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CanLoadStatesTest {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void shouldBeAbleLoadAllStates() {
        List<State> actual = State.load(this.getClass().getResourceAsStream("/data/states.json"));

        assertEquals("Expected number of states not loaded.", 43, actual.size());
        assertFalse("Expected all states to have non-empty borders", actual.stream().filter(s -> s.border.length == 0).findAny().isPresent());
    }
}
