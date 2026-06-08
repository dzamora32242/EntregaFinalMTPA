package com.mycompany.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.mycompany.servidordifusion.*;

public class SalonTest {

    /**
     * Verifica la correcta instanciación de un salón.
     * Se asegura de que al crear un nuevo salón con un nombre específico,
     * este nombre se asigne correctamente y coincida al ser recuperado.
     */
    @Test
    public void testCrearSalon() {
        Salon s = new Salon("UEMC");

        assertEquals("UEMC", s.getNombre());
    }

    /**
     * Comprueba el funcionamiento de la inserción de clientes en un salón.
     * Busca asegurar que, al añadir un usuario a un salón previamente vacío,
     * el contador de miembros del salón se incremente para reflejar esta nueva incorporación.
     */
    @Test
    public void testMeterCliente() {
        Salon s = new Salon("UEMC");
        ClienteDifusion c = null;

        s.meterCliente(c);

        assertEquals(1, s.getNumeroUsuarios());
    }

    /**
     * Verifica el proceso de eliminación de clientes de un salón.
     * El objetivo es validar que, tras añadir y posteriormente sacar a un usuario,
     * el número total de miembros del salón se actualice de forma precisa (volviendo a cero).
     */
    @Test
    public void testSacarCliente() {
        Salon s = new Salon("IUEMC");
        ClienteDifusion c = null;

        s.meterCliente(c);
        s.sacarCliente(c);

        assertEquals(0, s.getNumeroUsuarios());
    }

    /**
     * Valida el estado inicial de un salón en el momento de su creación.
     * Se pretende probar que un salón recién inicializado no contiene ningún cliente
     * preexistente en su lista de miembros.
     */
    @Test
    public void testSalonVacio() {
        Salon s = new Salon("IA");
        assertEquals(0, s.getNumeroUsuarios());
    }

    /**
     * Comprueba el estado del historial de mensajes al crear un salón nuevo.
     * Se busca verificar la respuesta proporcionada por el sistema cuando se solicita
     * el historial reciente en un canal que todavía carece de actividad y mensajes.
     */
    @Test
    public void testHistorialVacio() {
        Salon s = new Salon("IA");
        assertTrue(s.getHistorialReciente(10).isEmpty());
    }

}