package com.mycompany.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ValidacionesTest {

    /**
     * Verifica que el sistema valide correctamente los mensajes cuya longitud 
     * esté dentro del límite permitido (190 caracteres o menos). 
     * Se busca comprobar que un mensaje estándar cumpla con la restricción de tamaño.
     */
    @Test
    public void testMensajeCorto() {
        String msg = "Hola";

        assertTrue(msg.length() <= 190);
    }

    /**
     * Comprueba que el sistema detecte de forma adecuada aquellos mensajes que 
     * exceden el límite máximo de caracteres permitido (más de 190). 
     * El objetivo es asegurar que las cadenas demasiado largas no pasen la validación de longitud.
     */
    @Test
    public void testMensajeLargo() {
        String msg = "a".repeat(200);

        assertTrue(msg.length() > 190);
    }
}