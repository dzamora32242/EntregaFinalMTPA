package com.mycompany.servidordifusion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import com.mycompany.common.Mensaje;

public class ServidorDifusion implements Runnable {
    private ServerSocket servidor;
    private static ArrayList<ClienteDifusion> listaUsuarios = new ArrayList<>();
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private ArrayList<Salon> salones = new ArrayList<>();
    private Thread t;
    private static boolean aceptarClientes = true;
    private static boolean mensajeriaActiva = true;

    public static ArrayList<ClienteDifusion> getListaUsuarios() {
        return listaUsuarios;
    }

    public ServidorDifusion() throws Exception {
        servidor = new ServerSocket(5665);
        cargarUsuarios();
        salones.add(new Salon("IA"));
        salones.add(new Salon("Deportes"));
        salones.add(new Salon("Therian"));
        salones.add(new Salon("Manga"));
        salones.add(new Salon("UEMC")); 
        menuServidor();
        t = new Thread(this);
        t.start();
    }
    
    public void run() {
        try {
            escuchaUsuarios();
        } catch (Exception e) {
            System.out.println("Error en servidor: " + e.getMessage());
        }
    }

    /**
     * Lanza un hilo independiente que muestra un menú interactivo en la consola del servidor.
     * Permite al administrador controlar la aceptación de clientes, activar/desactivar la 
     * mensajería y consultar estadísticas de uso en tiempo real.
     */
public void menuServidor() {
    new Thread(() -> {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- MENÚ SERVIDOR ---");
            System.out.println("1. Dejar de aceptar clientes");
            System.out.println("2. Activar/desactivar mensajería");
            System.out.println("3. Ver estadísticas");

            int opcion = sc.nextInt();

            switch (opcion) {
                case 1:
                    aceptarClientes = false;
                    System.out.println("No se aceptarán más clientes");
                    break;

                case 2:
                    mensajeriaActiva = !mensajeriaActiva;
                    System.out.println("Mensajería ahora: " + mensajeriaActiva);
                    break;

                case 3:
                    mostrarEstadisticas();
                    break;
            }
        }
    }).start();
}

    /**
     * Bucle infinito que escucha peticiones de conexión entrantes por el puerto del servidor.
     * Si la opción de aceptar clientes está habilitada, instancia un nuevo objeto ClienteDifusion
     * por cada conexión recibida y lo añade a la lista general de usuarios.
     * 
     * @throws Exception Si ocurre algún error en la aceptación del socket cliente.
     */
    public void escuchaUsuarios() throws Exception {
        while (true) {
            System.out.println("Esperando clientes....");
            if (!aceptarClientes) {
                System.out.println("No se aceptan nuevos clientes");
                continue;
            }
            Socket sck = servidor.accept();
            System.out.println("Un cliente conectado...");
            ClienteDifusion unCliente = new ClienteDifusion(sck, usuarios, salones);
            listaUsuarios.add(unCliente);
        }
    }

    /**
     * Envía un mensaje a todos los clientes que están actualmente conectados al servidor.
     * La difusión masiva solo se realiza si el administrador mantiene la mensajería activa.
     * 
     * @param mensaje El objeto Mensaje que se va a enviar a toda la red.
     */
    public static void difusionMensaje(Mensaje mensaje) {
        if (!mensajeriaActiva) {
            System.out.println("Mensajería desactivada");
        return;
        }
        System.out.println("Difundiendo mensaje a todos los clientes");
        for (ClienteDifusion unCliente : new ArrayList<>(listaUsuarios)) {
            try {
                unCliente.sendMessage(mensaje);
            } catch (Exception e) {
                System.out.println("Error difusión: " + e.toString());
            }
        }
    }

    /**
     * Imprime en la consola del servidor las métricas de uso actuales.
     * Muestra el total de clientes conectados y desglosa el número de usuarios y mensajes por salón.
     */
    public void mostrarEstadisticas() {
        System.out.println("Usuarios conectados: " + listaUsuarios.size());

        for (Salon salon : salones) {
            System.out.println("Salón: " + salon.getNombre());
            System.out.println("Usuarios: " + salon.getNumeroUsuarios());
            System.out.println("Mensajes: " + salon.getNumeroMensajes());
        }
    }

    /**
     * Lee el archivo "usuarios.txt" para inicializar la lista de usuarios registrados en memoria.
     * Esto permite a los usuarios mantener sus cuentas (usuario y contraseña) a pesar de que el 
     * servidor sea reiniciado.
     */
    public void cargarUsuarios() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("usuarios.txt"));

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");

                usuarios.add(new Usuario(partes[0], partes[1]));
            }

            br.close();
            System.out.println("Usuarios cargados");

        } catch (Exception e) {
            System.out.println("No hay archivo de usuarios aún");
        }
    }

}
