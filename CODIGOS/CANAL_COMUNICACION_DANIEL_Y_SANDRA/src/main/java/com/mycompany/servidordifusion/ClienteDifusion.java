package com.mycompany.servidordifusion;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import com.mycompany.common.*;

public class ClienteDifusion extends Thread {

    private Socket cliente;
    private OutputStream os;
    private InputStream is;
    private String username = null;
    private ArrayList<Usuario> usuarios;
    private ArrayList<Salon> salones;
    private boolean mensajeriaActiva;

    public ClienteDifusion(Socket sck, ArrayList<Usuario> usuarios, ArrayList<Salon> salones, boolean mensajeriaActiva) throws Exception {
        cliente = sck;
        os = cliente.getOutputStream();
        is = cliente.getInputStream();
        this.usuarios = usuarios;
        this.salones = salones;
        this.mensajeriaActiva = mensajeriaActiva;
        start();
    }

    public String getUsername() {
        return username;
    }

    public void setMensajeriaActiva(boolean mensajeriaActiva) {
        this.mensajeriaActiva = mensajeriaActiva;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Mensaje msg = EnvioReciboMensajes.recibir(is);
                if (msg == null) break;
                procesarMensaje(msg);
            }
        } catch (Exception ex) {
            System.out.println("Cliente desconectado: " + username);
        }
        limpiarSalones();
    }

    /**
     * Elimina al cliente de todos los salones en los que estuviera presente.
     * Se utiliza al cerrar la sesión o al desconectarse.
     */
    private void limpiarSalones() {
        for (Salon salon : salones) {
            salon.sacarCliente(this);
        }
    }

    /**
     * Envía un objeto Mensaje al cliente de forma sincronizada.
     * @param mensaje El mensaje a enviar.
     * @throws Exception si ocurre un error durante el envío.
     */
    public synchronized void sendMessage(Mensaje mensaje) throws Exception {
        EnvioReciboMensajes.enviar(os, mensaje);
    }

    /**
     * Procesa un mensaje entrante y lo delega al método correspondiente según su tipo.
     * @param msg El mensaje recibido del cliente.
     * @throws Exception si ocurre un error al procesar el mensaje.
     */
    private void procesarMensaje(Mensaje msg) throws Exception {
        System.out.println("Mensaje recibido: " + msg.getTipo());
        switch (msg.getTipo()) {
            case REGISTER_REQ:
                procesarRegistro((RegisterReq) msg);
                break;
            case LOGIN_REQ:
                procesarLogin((LoginReq) msg);
                break;
            case SEND_CHANNEL_MSG_REQ:
                procesarEnvioMensaje((SendChannelMsgReq) msg);
                break;
            case LOGOUT_REQ:
                procesarLogout((LogoutReq) msg);
                break;
            case HEARTBEAT:
                procesarHeartbeat((Heartbeat) msg);
                break;
            case GET_CHANNELS_REQ:
                procesarObtenerCanales((GetChannelsReq) msg);
                break;
            case JOIN_CHANNEL_REQ:
                procesarUnirCanal((JoinChannelReq) msg);
                break;
            case LEAVE_CHANNEL_REQ:
                procesarLeaveChannel((LeaveChannelReq) msg);
                break;
            case HISTORY_REQ:
                procesarHistory((HistoryReq) msg);
                break;
            default:
                EnvioReciboMensajes.enviar(os, new ErrorRes("Primitiva desconocida"));
        }
    }

    /**
     * Guarda la lista actual de usuarios en el archivo "usuarios.txt".
     * Cada línea del archivo contiene el nombre de usuario y la contraseña separados por una coma.
     */
    public void guardarUsuarios() {
        try {
            PrintWriter pw = new PrintWriter("usuarios.txt");

            for (Usuario u : usuarios) {
                pw.println(u.getUsuario() + "," + u.getContrasena());
            }

            pw.close();
            System.out.println("Usuarios guardados correctamente");

        } catch (Exception e) {
            System.out.println("Error al guardar usuarios");
        }
    }

    /**
     * Guarda la lista actual de mensajes mandados a cada salon en el archivo "mensajes.txt".
     * Cada línea del archivo contiene toda la informacion del mensaje separados por una coma.
     */
    public void guardarMensajes() {
        try {
            PrintWriter pw = new PrintWriter("mensajes.txt");

            for (Salon salon : salones) {
                for (MensajeUsuario mensaje : salon.getMensajes()) {
                    pw.println(mensaje.getTiempo() + "," + salon.getNombre() + "," + mensaje.getEmisor() + "," + mensaje.getContenido());
                }
            }

            pw.close();
            System.out.println("Mensajes guardados correctamente");

        } catch (Exception e) {
            System.out.println("Error al guardar mensajes");
        }
    }

    /**
     * Procesa una solicitud de registro de un nuevo usuario.
     * Verifica si el usuario ya existe, si no exisite, lo crea con una contraseña generada y lo guarda.
     * @param req La solicitud de registro.
     * @throws Exception si ocurre un error durante el envío de la respuesta.
     */
    private void procesarRegistro(RegisterReq req) throws Exception {
        System.out.println("Registro de: " + req.getNombreUsuarioSolicitado());
        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(req.getNombreUsuarioSolicitado())) {
                EnvioReciboMensajes.enviar(os, new RegisterRes(false, "Usuario ya existe"));
                return;
            }
        }
        String contrasenaGenerada = req.getNombreUsuarioSolicitado() + (int) Math.floor(Math.random() * 10000);
        usuarios.add(new Usuario(req.getNombreUsuarioSolicitado(), contrasenaGenerada));
        
        guardarUsuarios();
        
        EnvioReciboMensajes.enviar(os, new RegisterRes(true,"Registro exitoso, contraseña generada: " + contrasenaGenerada));
    }

    /**
     * Procesa una solicitud de inicio de sesión.
     * Verifica las credenciales del usuario y actualiza el estado del cliente si son correctas.
     * @param req La solicitud de login.
     * @throws Exception si ocurre un error durante el envío de la respuesta.
     */
    private void procesarLogin(LoginReq req) throws Exception {
        System.out.println("Intento de login: " + req.getNombreUsuario());
        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(req.getNombreUsuario()) &&
                    u.getContrasena().equals(req.getContrasena())) {
                this.username = req.getNombreUsuario();
                EnvioReciboMensajes.enviar(os, new LoginRes(true, "OK"));
                System.out.println("Login correcto: " + username);
                return;
            }
        }
        System.out.println("Login fallido: " + req.getNombreUsuario());
        EnvioReciboMensajes.enviar(os, new LoginRes(false, "Credenciales incorrectas"));
    }

    /**
     * Procesa una solicitud de cierre de sesión.
     * Limpia el estado del usuario y cierra la conexión.
     * @param msg La solicitud de logout.
     * @throws Exception si ocurre un error.
     */
    private void procesarLogout(LogoutReq msg) throws Exception {
        if (username == null) {
            EnvioReciboMensajes.enviar(os, new LogoutRes("No hay ninguna sesión iniciada"));
            return;
        }
        System.out.println("Logout de: " + username);
        username = null;
        limpiarSalones();
        EnvioReciboMensajes.enviar(os, new LogoutRes("OK"));
        cliente.close();
    }

    /**
     * Procesa un mensaje de heartbeat para mantener la conexión activa.
     * Responde con un HeartbeatACK.
     * @param msg El mensaje de heartbeat.
     * @throws Exception si ocurre un error al enviar el ACK.
     */
    private void procesarHeartbeat(Heartbeat msg) throws Exception {
        System.out.println("Heartbeat de: " + username);
        EnvioReciboMensajes.enviar(os, new HeartbeatACK());
    }

    /**
     * Procesa una solicitud para obtener la lista de salones disponibles.
     * Requiere que el usuario haya iniciado sesión.
     * @param msg La solicitud para obtener canales.
     * @throws Exception si ocurre un error al enviar la respuesta.
     */
    private void procesarObtenerCanales(GetChannelsReq msg) throws Exception {
        if (username == null) {
            EnvioReciboMensajes.enviar(os, new ErrorRes("Debe iniciar sesión para consultar los canales"));
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < salones.size(); i++) {
            sb.append(salones.get(i).getNombre());
            if (i != salones.size() - 1) sb.append(", ");
        }
        EnvioReciboMensajes.enviar(os, new GetChannelsRes(true, sb.toString()));
    }

    /**
     * Procesa una solicitud para unirse a un salón de chat.
     * Añade al cliente al salón, le envía el historial reciente y notifica a los demás miembros.
     * @param msg La solicitud para unirse al canal.
     * @throws Exception si ocurre un error.
     */
    private void procesarUnirCanal(JoinChannelReq msg) throws Exception {
        if (username == null) {
            EnvioReciboMensajes.enviar(os, new ErrorRes("Debe iniciar sesión para unirse a un salón"));
            return;
        }
        String nombreSalon = msg.getNombreSalon();
        Salon salonEncontrado = null;
        for (Salon salon : salones) {
            if (salon.getNombre().equals(nombreSalon)) {
                salonEncontrado = salon;
                break;
            }
        }
        if (salonEncontrado == null) {
            EnvioReciboMensajes.enviar(os, new JoinChannelRes(false, "Salón no encontrado"));
            return;
        }
        salonEncontrado.meterCliente(this);
        String historial = salonEncontrado.getHistorialReciente(50);
        EnvioReciboMensajes.enviar(os, new JoinChannelRes(true, historial));
        salonEncontrado.difundir(new NotifyJoin(username, nombreSalon));

        System.out.println("Usuario " + username + " entra en el salón: " + nombreSalon);
    }

    /**
     * Procesa una solicitud para abandonar un salón de chat.
     * @param req La solicitud para abandonar el canal.
     * @throws Exception si ocurre un error.
     */
    private void procesarLeaveChannel(LeaveChannelReq req) throws Exception {
        if (username == null) {
            EnvioReciboMensajes.enviar(os, new LeaveChannelRes(false, "Debe iniciar sesión"));
            return;
        }
        for (Salon salon : salones) {
            if (salon.getNombre().equals(req.getSalon())) {
                salon.sacarCliente(this);
                System.out.println("Usuario " + username + " sale del salón: " + req.getSalon());
                EnvioReciboMensajes.enviar(os, new LeaveChannelRes(true, "Has salido del salón correctamente"));
                return;
            }
        }
        EnvioReciboMensajes.enviar(os, new LeaveChannelRes(false, "Salón no encontrado"));
    }

    /**
     * Procesa el envío de un mensaje, ya sea a un salón o a un usuario privado.
     * @param req La solicitud de envío de mensaje.
     * @throws Exception si ocurre un error.
     */
    private void procesarEnvioMensaje(SendChannelMsgReq req) throws Exception {
        if (username == null) {
            EnvioReciboMensajes.enviar(os, new ErrorRes("Debe iniciar sesión para enviar mensajes"));
            return;
        }

        if (req.getContenido().length() > 190) {
            EnvioReciboMensajes.enviar(os, new ErrorRes("El mensaje supera los 190 caracteres"));
            return;
        }

        if (!mensajeriaActiva) {
            EnvioReciboMensajes.enviar(os, new ErrorRes("La mensajeria está desactivada."));
            return;
        }

        System.out.println("Mensaje de " + username + " hacia " + req.getDestino());
        System.out.println("Contenido: " + req.getContenido());
        if (req.getEsUnSalon()) {
            System.out.println("Mensaje a salón: " + req.getDestino());
            for (Salon salon : salones) {
                if (salon.getNombre().equals(req.getDestino())) {
                    if (!salon.esMiembro(this)) {
                        EnvioReciboMensajes.enviar(os, new ErrorRes("No perteneces al salón " + req.getDestino()));
                        return;
                    }
                    salon.addMensaje(new MensajeUsuario(username, req.getContenido()));
                    salon.difundir(new SendChannelNotification(username, req.getDestino(), req.getContenido()));
                    guardarMensajes();
                    return;
                }
            }
            System.out.println("Error: salón no encontrado");
            EnvioReciboMensajes.enviar(os, new ErrorRes("Salón no encontrado"));
        } else {
            System.out.println("Mensaje privado de " + username + " a " + req.getDestino());
            String destino = req.getDestino();
            for (ClienteDifusion c : ServidorDifusion.getListaUsuarios()) {
                if (c.getUsername() != null && c.getUsername().equals(destino)) {
                    c.sendMessage(new SendPrivNotification(username, destino, req.getContenido()));
                    guardarMensajes();
                    return;
                }
            }
            System.out.println("Error: usuario destino no conectado");
            EnvioReciboMensajes.enviar(os, new ErrorRes("Usuario destino no conectado"));
        }
    }

    /**
     * Procesa una solicitud de historial de mensajes de un salón.
     * @param msg La solicitud de historial.
     * @throws Exception si ocurre un error.
     */
    private void procesarHistory(HistoryReq msg) throws Exception {
        System.out.println("Historial solicitado por " + username + " del salón " + msg.getSalon());
        if (username == null) {
            EnvioReciboMensajes.enviar(os, new ErrorRes("Debe iniciar sesión para consultar el historial"));
            return;
        }
        for (Salon salon : salones) {
            if (salon.getNombre().equals(msg.getSalon())) {
                String historial = salon.obtenerHistorialDesde(msg.getFecha_solicitada());
                EnvioReciboMensajes.enviar(os, new HistoryRes(msg.getSalon(), historial));
                return;
            }
        }
        EnvioReciboMensajes.enviar(os, new ErrorRes("Salón no encontrado"));
    }
}
