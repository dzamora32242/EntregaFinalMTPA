package com.mycompany.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.mycompany.servidordifusion.Usuario;

public class UsuarioTest {

    @Test
    public void testCrearUsuario() {
        Usuario u = new Usuario("Sandra", "1234");

        assertEquals("Sandra", u.getUsuario());
        assertEquals("1234", u.getContrasena());
    }

    @Test
    public void testUsuarioNoNulo() {
        Usuario u = new Usuario("Dani", "pass");

        assertNotNull(u);
    }
}