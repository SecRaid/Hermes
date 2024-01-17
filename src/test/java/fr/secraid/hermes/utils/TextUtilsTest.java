package fr.secraid.hermes.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextUtilsTest {

    @Test
    void capitalize() {
        assertEquals("Test string", TextUtils.capitalize("TesT sTRiNg"));
    }

    @Test
    void normalizeCommandName() {
        assertEquals("some-cool-command", TextUtils.normalizeCommandName("someCoolCommand"));
    }
}