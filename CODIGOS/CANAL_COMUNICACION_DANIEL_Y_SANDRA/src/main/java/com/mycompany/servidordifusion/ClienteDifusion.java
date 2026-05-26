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
                if (msg == null) break;
                procesarMensaje(msg);
            }
        } catch (Exception ex) {
            System.out.println("Cliente desconectado: " + username);
        }
        limpiarSalones();
    }

    private void limpiarSalones() {
        for (Salon salon : salones) {
            salon.sacarCliente(this);
        }
    }

    public synchronized void sendMessage(Mensaje mensaje) throws Exception {
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
        EnvioReciboMensajes.enviar(os, new RegisterRes(true, contrasenaGenerada));
    }

    private void procesarLogin(LoginReq req) throws Exception {
        System.out.println("Login de: " + req.getNombreUsuario());
        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(req.getNombreUsuario()) &&
                    u.getContrasena().equals(req.getContrasena())) {
                this.username = req.getNombreUsuario();
                EnvioReciboMensajes.enviar(os, new LoginRes(true, "OK"));
                return;
            }
        }
        EnvioReciboMensajes.enviar(os, new LoginRes(false, "Credenciales incorrectas"));
    }

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

    private void procesarHeartbeat(Heartbeat msg) throws Exception {
        System.out.println("Heartbeat de: " + username);
        EnvioReciboMensajes.enviar(os, new HeartbeatACK());
    }

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
    }

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

    private void procesarEnvioMensaje(SendChannelMsgReq req) throws Exception {
        if (username == null) {
            EnvioReciboMensajes.enviar(os, new ErrorRes("Debe iniciar sesión para enviar mensajes"));
            return;
        }
        if (req.getEsUnSalon()) {
            for (Salon salon : salones) {
                if (salon.getNombre().equals(req.getDestino())) {
                    if (!salon.esMiembro(this)) {
                        EnvioReciboMensajes.enviar(os, new ErrorRes("No perteneces al salón " + req.getDestino()));
                        return;
                    }
                    salon.addMensaje(new MensajeUsuario(username, req.getContenido()));
                    salon.difundir(new SendChannelNotification(username, req.getDestino(), req.getContenido()));
                    return;
                }
            }
            EnvioReciboMensajes.enviar(os, new ErrorRes("Salón no encontrado"));
        } else {
            String destino = req.getDestino();
            for (ClienteDifusion c : ServidorDifusion.getListaUsuarios()) {
                if (c.getUsername() != null && c.getUsername().equals(destino)) {
                    c.sendMessage(new SendPrivNotification(username, destino, req.getContenido()));
                    return;
                }
            }
            EnvioReciboMensajes.enviar(os, new ErrorRes("Usuario destino no conectado"));
        }
    }

    private void procesarHistory(HistoryReq msg) throws Exception {
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
