package com.mycompany.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.mycompany.servidordifusion.Usuario;

public class UsuarioTest {

    /**
     * Verifica la correcta creación e inicialización de un objeto Usuario.
     * Se comprueba que al instanciar un usuario con un nombre y contraseña específicos,
     * estos valores se asignen de forma adecuada y coincidan exactamente al ser consultados.
     */
    @Test
    public void testCrearUsuario() {
        Usuario u = new Usuario("Sandra", "1234");

        assertEquals("Sandra", u.getUsuario());
        assertEquals("1234", u.getContrasena());
    }

    /**
     * Comprueba que el proceso de instanciación de un Usuario genera efectivamente un objeto válido.
     * El objetivo es asegurar que la creación de la instancia se realice de manera exitosa y 
     * no devuelva una referencia nula bajo condiciones normales.
     */
    @Test
    public void testUsuarioNoNulo() {
        Usuario u = new Usuario("Dani", "pass");

        assertNotNull(u);
    }
}