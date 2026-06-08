package com.mycompany.common;

import java.io.InputStream;
import java.io.OutputStream;

public class EnvioReciboMensajes {

    /**
     * Envía un objeto Mensaje a través de un flujo de salida.
     *
     * @param os      El flujo de salida donde se escribirá el mensaje.
     * @param mensaje El objeto que se desea enviar.
     * @throws Exception Si ocurre un error de entrada/salida durante el proceso de escritura.
     */
    public static void enviar(OutputStream os, Mensaje mensaje) throws Exception {
        os.write(mensaje.trasnformacionString().getBytes());
    }

    /**
     * Lee y recibe un objeto Mensaje desde un flujo de entrada.
     *
     * @param is El flujo de entrada  desde donde se leerá el mensaje.
     * @return Una instancia de Mensaje con los datos recibidos, 
     * o null si se alcanza el final del flujo.
     * 
     * @throws Exception Si ocurre un error de lectura 
     * o al transformar los datos en un objeto Mensaje
     */
    public static Mensaje recibir(InputStream is) throws Exception {
        byte[] buffer = new byte[65536];

        int n = is.read(buffer);

        if (n < 0)
            return null;

        return Mensaje.transformacionInstancia(new String(buffer, 0, n));
    }
}
