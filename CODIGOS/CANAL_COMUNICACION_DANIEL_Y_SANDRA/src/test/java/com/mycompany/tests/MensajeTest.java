package com.mycompany.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.mycompany.servidordifusion.*;

public class MensajeTest {

    @Test
    public void testAgregarMensaje() {
        Salon s = new Salon("UEMC");

        s.addMensaje(new MensajeUsuario("Sandra", "Hola"));

        assertEquals(1, s.getNumeroMensajes());
    }

    @Test
    public void testHistorialNoVacio() {
        Salon s = new Salon("UEMC");

        s.addMensaje(new MensajeUsuario("Sandra", "Hola"));

        String historial = s.getHistorialReciente(10);

        assertFalse(historial.isEmpty());
    }
}