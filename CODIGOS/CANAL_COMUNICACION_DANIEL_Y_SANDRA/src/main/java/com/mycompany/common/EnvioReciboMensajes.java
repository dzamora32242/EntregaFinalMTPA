package com.mycompany.common;

import java.io.InputStream;
import java.io.OutputStream;

public class EnvioReciboMensajes {
    public static void enviar(OutputStream os, Mensaje mensaje) throws Exception {
        os.write(mensaje.trasnformacionString().getBytes());
    }


    public static Mensaje recibir(InputStream is) throws Exception {
        byte[] buffer = new byte[1024];

        int n = is.read(buffer);

        if (n < 0)
            return null;
        
        return Mensaje.transformacionInstancia(new String(buffer, 0, n));
    }
}
