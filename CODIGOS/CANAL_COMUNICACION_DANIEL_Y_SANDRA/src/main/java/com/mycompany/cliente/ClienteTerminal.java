package com.mycompany.cliente;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClienteTerminal implements MensajeListener {

    private ClienteCore core;
    private boolean running;

    public ClienteTerminal(String host, int puerto) throws Exception {
        core = new ClienteCore(host, puerto);
        core.setListener(this);
        running = true;
    }

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int puerto = args.length > 1 ? Integer.parseInt(args[1]) : 5665;

        ClienteTerminal terminal = new ClienteTerminal(host, puerto);
        System.out.println("Conectado a " + host + ":" + puerto);
        System.out.println("Escribe 'help' para ver los comandos disponibles.");
        terminal.bucleComandos();
    }

    private void bucleComandos() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (running) {
            System.out.print("> ");
            String linea = br.readLine();
            if (linea == null) break;
            procesarComando(linea.trim());
        }
    }

    private void procesarComando(String linea) {
        if (linea.isEmpty()) return;
        String[] partes = linea.split(" ", 3);
        String cmd = partes[0].toLowerCase();

        try {
            switch (cmd) {
                case "help":
                    mostrarAyuda();
                    break;

                case "register":
                    if (partes.length < 2) { System.out.println("Uso: register <usuario>"); break; }
                    core.registrar(partes[1]);
                    break;

                case "login":
                    if (partes.length < 3) { System.out.println("Uso: login <usuario> <contraseña>"); break; }
                    core.login(partes[1], partes[2]);
                    break;

                case "logout":
                    core.logout();
                    break;

                case "channels":
                    core.getCanales();
                    break;

                case "join":
                    if (partes.length < 2) { System.out.println("Uso: join <salon>"); break; }
                    core.unirCanal(partes[1]);
                    break;

                case "leave":
                    if (partes.length < 2) { System.out.println("Uso: leave <salon>"); break; }
                    core.salirCanal(partes[1]);
                    break;

                case "msg":
                    if (partes.length < 3) { System.out.println("Uso: msg <salon> <texto>"); break; }
                    core.enviarMensajeCanal(partes[1], partes[2]);
                    break;

                case "priv":
                    if (partes.length < 3) { System.out.println("Uso: priv <usuario> <texto>"); break; }
                    core.enviarMensajePrivado(partes[1], partes[2]);
                    break;

                case "history":
                    if (partes.length < 2) { System.out.println("Uso: history <salon> [timestamp_ms]"); break; }
                    long ts = partes.length >= 3 ? Long.parseLong(partes[2]) : 0L;
                    core.pedirHistorial(partes[1], ts);
                    break;

                case "exit":
                    core.desconectar();
                    running = false;
                    System.out.println("Desconectado.");
                    break;

                default:
                    System.out.println("Comando desconocido. Escribe 'help' para ver los comandos.");
            }
        } catch (Exception e) {
            System.out.println("Error al ejecutar el comando: " + e.getMessage());
        }
    }

    private void mostrarAyuda() {
        System.out.println("Comandos disponibles:");
        System.out.println("  register <usuario>                   - Registrar nuevo usuario");
        System.out.println("  login <usuario> <contraseña>         - Iniciar sesión");
        System.out.println("  logout                               - Cerrar sesión");
        System.out.println("  channels                             - Listar salones disponibles");
        System.out.println("  join <salon>                         - Unirse a un salón");
        System.out.println("  leave <salon>                        - Salir de un salón");
        System.out.println("  msg <salon> <texto>                  - Enviar mensaje a un salón");
        System.out.println("  priv <usuario> <texto>               - Enviar mensaje privado");
        System.out.println("  history <salon> [timestamp_ms]       - Ver historial (0 = todo)");
        System.out.println("  exit                                 - Desconectarse y salir");
    }

    @Override
    public void onRegistro(boolean exito, String respuesta) {
        if (exito) {
            System.out.println("\n[REGISTRO OK] Tu contraseña es: " + respuesta);
        } else {
            System.out.println("\n[REGISTRO ERROR] " + respuesta);
        }
        System.out.print("> ");
    }

    @Override
    public void onLogin(boolean exito, String respuesta) {
        if (exito) {
            System.out.println("\n[LOGIN OK] Bienvenido, " + core.getUsername());
        } else {
            System.out.println("\n[LOGIN ERROR] " + respuesta);
        }
        System.out.print("> ");
    }

    @Override
    public void onLogout(String respuesta) {
        System.out.println("\n[LOGOUT] " + respuesta);
        if ("OK".equals(respuesta)) {
            running = false;
        }
        System.out.print("> ");
    }

    @Override
    public void onCanales(boolean exito, String respuesta) {
        if (exito) {
            System.out.println("\n[SALONES] " + respuesta);
        } else {
            System.out.println("\n[SALONES ERROR] " + respuesta);
        }
        System.out.print("> ");
    }

    @Override
    public void onUnirCanal(boolean exito, String respuesta) {
        if (exito) {
            System.out.println("\n[UNIDO AL SALON] Historial reciente:\n" + respuesta);
        } else {
            System.out.println("\n[JOIN ERROR] " + respuesta);
        }
        System.out.print("> ");
    }

    @Override
    public void onSalirCanal(boolean ok, String mensaje) {
        System.out.println("\n[LEAVE] " + mensaje);
        System.out.print("> ");
    }

    @Override
    public void onMensajeCanal(String usuario, String salon, String contenido) {
        System.out.println("\n[" + salon + "] " + usuario + ": " + contenido);
        System.out.print("> ");
    }

    @Override
    public void onMensajePrivado(String origen, String destino, String contenido) {
        System.out.println("\n[PRIVADO de " + origen + "] " + contenido);
        System.out.print("> ");
    }

    @Override
    public void onHistorial(String salon, String historial) {
        System.out.println("\n[HISTORIAL de " + salon + "]\n" + historial);
        System.out.print("> ");
    }

    @Override
    public void onNotifyJoin(String usuario, String salon) {
        System.out.println("\n[" + salon + "] " + usuario + " se ha unido al salón.");
        System.out.print("> ");
    }

    @Override
    public void onHeartbeatAck() {
        // silencioso en terminal
    }

    @Override
    public void onError(String mensaje) {
        System.out.println("\n[ERROR] " + mensaje);
        System.out.print("> ");
    }

    @Override
    public void onDesconexion() {
        System.out.println("\n[DESCONECTADO] Se ha perdido la conexión con el servidor.");
        running = false;
    }
}
