package com.mycompany.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ValidacionesTest {

    @Test
    public void testMensajeCorto() {
        String msg = "Hola";

        assertTrue(msg.length() <= 190);
    }

    @Test
    public void testMensajeLargo() {
        String msg = "a".repeat(200);

        assertTrue(msg.length() > 190);
    }
}