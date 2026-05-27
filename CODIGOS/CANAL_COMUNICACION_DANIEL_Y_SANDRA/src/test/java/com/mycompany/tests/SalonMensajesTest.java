package com.mycompany.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.mycompany.servidordifusion.*;

public class SalonMensajesTest {

    @Test
    public void testMultiplesMensajes() {
        Salon s = new Salon("UEMC");

        s.addMensaje(new MensajeUsuario("A", "1"));
        s.addMensaje(new MensajeUsuario("B", "2"));

        assertEquals(2, s.getNumeroMensajes());
    }
}