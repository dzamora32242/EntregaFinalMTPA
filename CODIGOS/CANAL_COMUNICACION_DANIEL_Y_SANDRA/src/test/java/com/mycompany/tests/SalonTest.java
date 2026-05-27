package com.mycompany.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.mycompany.servidordifusion.*;

public class SalonTest {

    @Test
    public void testCrearSalon() {
        Salon s = new Salon("UEMC");

        assertEquals("UEMC", s.getNombre());
    }

    @Test
    public void testMeterCliente() {
        Salon s = new Salon("UEMC");
        ClienteDifusion c = null;

        s.meterCliente(c);

        assertEquals(1, s.getNumeroUsuarios());
    }

    @Test
    public void testSacarCliente() {
        Salon s = new Salon("IUEMC");
        ClienteDifusion c = null;

        s.meterCliente(c);
        s.sacarCliente(c);

        assertEquals(0, s.getNumeroUsuarios());
    }

    @Test
    public void testSalonVacio() {
        Salon s = new Salon("IA");
        assertEquals(0, s.getNumeroUsuarios());
    }

    @Test
    public void testHistorialVacio() {
        Salon s = new Salon("IA");
        assertTrue(s.getHistorialReciente(10).isEmpty());
    }

}