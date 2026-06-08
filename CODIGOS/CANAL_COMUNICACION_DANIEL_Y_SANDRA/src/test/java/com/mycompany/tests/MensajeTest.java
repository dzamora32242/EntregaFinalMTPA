package com.mycompany.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.mycompany.servidordifusion.*;

public class MensajeTest {

    /**
     * Comprueba el funcionamiento del almacenamiento de mensajes dentro de un salón.
     * Se verifica que al añadir un nuevo MensajeUsuario, el contador total de mensajes
     * del salón se incremente de manera correcta, asegurando que la inserción ha sido exitosa.
     */
    @Test
    public void testAgregarMensaje() {
        Salon s = new Salon("UEMC");

        s.addMensaje(new MensajeUsuario("Sandra", "Hola"));

        assertEquals(1, s.getNumeroMensajes());
    }

    /**
     * Verifica el comportamiento de la recuperación del historial reciente de un salón.
     * Se intenta probar que, tras haber insertado un mensaje válido en un salón nuevo, 
     * la cadena de texto proporcionada al solicitar su historial no se devuelva vacía.
     */
    @Test
    public void testHistorialNoVacio() {
        Salon s = new Salon("UEMC");

        s.addMensaje(new MensajeUsuario("Sandra", "Hola"));

        String historial = s.getHistorialReciente(10);

        assertFalse(historial.isEmpty());
    }
}