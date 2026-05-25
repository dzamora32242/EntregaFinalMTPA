package com.mycompany.servidordifusion;

import java.io.InputStream;
import java.io.OutputStream;
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

    public ClienteDifusion(Socket sck, ArrayList<Usuario> usuarios, ArrayList<Salon> salones) throws Exception {
        cliente = sck;
        os = cliente.getOutputStream();
        is = cliente.getInputStream();
        this.usuarios = usuarios;
        this.salones = salones;
        start();
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Mensaje msg = EnvioReciboMensajes.recibir(is);
                procesarMensaje(msg);
            }
        } catch (Exception ex) {
            System.out.println("Cliente desconectado: " + username);
        }
    }

    public void sendMessage(Mensaje mensaje) throws Exception {
        EnvioReciboMensajes.enviar(os, mensaje);
    }

    private void procesarMensaje(Mensaje msg) throws Exception {

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

    private void procesarHistory(HistoryReq msg) throws Exception {
        if (username == null){
            EnvioReciboMensajes.enviar(os, new ErrorRes("Debe iniciar sesión para consultar el historial"));
            return;
        }
        String nombreSalon = msg.getSalon();
        long fechaSolicitada = msg.getFecha_solicitada();

        for (Salon salon : salones){
            if (salon.getNombre().equals(nombreSalon)){
                String historial = salon.obtenerHistorialDesde(fechaSolicitada);
                EnvioReciboMensajes.enviar(os, new HistoryRes(nombreSalon, historial));
                return;
            }
        }

        EnvioReciboMensajes.enviar(os, new ErrorRes("No se ha encontrado ningún salon con el nombre especificado"));
    }

    private void procesarUnirCanal(JoinChannelReq msg) throws Exception {
        if (username == null) {
            EnvioReciboMensajes.enviar(os, new ErrorRes("Debe iniciar sesión para unirse a un salón"));
            return;
        }

        String nombreSalon = msg.getNombreSalon();

        boolean encontrado = false;

        Salon salonEncontrado = null;

        for (Salon salon : salones) {
            if (salon.getNombre().equals(nombreSalon)) {
                encontrado = true;
                salonEncontrado = salon;
            }
        }

        if (!encontrado) {
            EnvioReciboMensajes.enviar(os,
                    new JoinChannelRes(false, "No se ha encontrado ningún salón con el nombre especificado"));
            return;
        }
        
        salonEncontrado.meterCliente(cliente);
        
        EnvioReciboMensajes.enviar(os,
                new JoinChannelRes(true, "Te has unido correctamente al salón"));

        ServidorDifusion.difusionMensaje(new NotifyJoin(username, nombreSalon));
    }

    private void procesarLeaveChannel(LeaveChannelReq req) throws Exception {
        if (username == null) {
            EnvioReciboMensajes.enviar(os, new LeaveChannelRes(false, "Debe iniciar sesión"));
            return;
        }
        for (Salon salon : salones) {
            if (salon.getNombre().equals(req.getSalon())) {
                salon.sacarCliente(cliente);
            }
        }
    
        System.out.println("Usuario " + username + " sale del salón: " + req.getSalon());
        EnvioReciboMensajes.enviar(os, new LeaveChannelRes(true, "Has salido del salón correctamente"));
    }

    private void procesarObtenerCanales(GetChannelsReq msg) throws Exception {
        if (username == null) {
            EnvioReciboMensajes.enviar(os, new ErrorRes("Debe iniciar sesión para consultar los canales que hay"));
            return;
        }

        String respuesta = "";

        for (int i = 0; i < salones.size(); i++) {
            respuesta += salones.get(i).getNombre();
            if (i != salones.size() - 1) {
                respuesta += ", ";
            }
        }

        EnvioReciboMensajes.enviar(os, new GetChannelsRes(true, respuesta));
    }

    private void procesarHeartbeat(Heartbeat msg) throws Exception {
        System.out.println("Heartbeat de: " + username);
        EnvioReciboMensajes.enviar(os, new HeartbeatACK());
    }

    private void procesarLogout(LogoutReq msg) throws Exception {
        if (username == null) {
            EnvioReciboMensajes.enviar(os, new LogoutRes("No hay ninguna sesión iniciada"));
            return;
        }

        username = null;
        EnvioReciboMensajes.enviar(os, new LogoutRes("OK"));
        System.out.println("Logout de: " + msg.getNombreUsuario());
        cliente.close();
    }

    private void procesarRegistro(RegisterReq req) throws Exception {
        System.out.println("Registro de: " + req.getNombreUsuarioSolicitado());

        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(req.getNombreUsuarioSolicitado())) {
                EnvioReciboMensajes.enviar(os, new RegisterRes(false, "Usuario ya existe"));
                return;
            }
        }

        String contrasenaGenerada = req.getNombreUsuarioSolicitado() + Math.floor(Math.random() * 10000);

        Usuario nuevoUsuario = new Usuario(req.getNombreUsuarioSolicitado(), contrasenaGenerada);

        usuarios.add(nuevoUsuario);
        EnvioReciboMensajes.enviar(os, new RegisterRes(true, "Registro exitoso, contraseña generada: " + contrasenaGenerada));
    }

    private void procesarLogin(LoginReq req) throws Exception {
        System.out.println("Login de: " + req.getNombreUsuario());

        if (req.getNombreUsuario() != null && req.getContrasena().equals("1234")) {
            this.username = req.getNombreUsuario();
            EnvioReciboMensajes.enviar(os, new LoginRes(true, "OK"));
        } else {
            EnvioReciboMensajes.enviar(os, new LoginRes(false, "Credenciales incorrectas"));
        }
    }

    private void procesarEnvioMensaje(SendChannelMsgReq req) throws Exception {
        if (username == null) {
            EnvioReciboMensajes.enviar(os, new ErrorRes("Debe iniciar sesión para enviar mensajes"));
            return;
        }

        if (req.getEsUnSalon()) {
            // Envío a canal, se difunde a todos los clientes
            ServidorDifusion.difusionMensaje(new SendChannelNotification(username, req.getDestino(), req.getContenido()));
        } else { //MENSAJE PRIVADO SANDRA
            String destino = req.getDestino();
            boolean encontrado = false;

            for (ClienteDifusion cliente : ServidorDifusion.getListaUsuarios()) {
                if (cliente.getUsername() != null && cliente.getUsername().equals(destino)) {
                    cliente.sendMessage(new SendPrivNotification(username, destino, req.getContenido()));
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                EnvioReciboMensajes.enviar(os, new ErrorRes("Usuario destino no conectado"));
            }
        }
    }
}
