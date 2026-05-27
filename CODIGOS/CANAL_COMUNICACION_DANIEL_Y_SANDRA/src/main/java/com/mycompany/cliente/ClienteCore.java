package com.mycompany.cliente;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import com.mycompany.common.*;

public class ClienteCore {

    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private MensajeListener listener;
    private String username;
    private String pendingUsername;
    private boolean conectado;
    private Timer heartbeatTimer;
    
    public ClienteCore(String host, int puerto) throws Exception {
        socket = new Socket(host, puerto);
        is = socket.getInputStream();
        os = socket.getOutputStream();
        conectado = true;
        iniciarReceptor();
        iniciarHeartbeat();
    }

    public void setListener(MensajeListener listener) {
        this.listener = listener;
    }

    public String getUsername() {
        return username;
    }

    public boolean isConectado() {
        return conectado;
    }

    private synchronized void enviar(Mensaje msg) throws Exception {
        EnvioReciboMensajes.enviar(os, msg);
    }

    private void iniciarReceptor() {
        Thread t = new Thread(() -> {
            try {
                while (conectado) {
                    Mensaje msg = EnvioReciboMensajes.recibir(is);
                    if (msg == null) break;
                    procesarMensaje(msg);
                }
            } catch (Exception e) {
                if (conectado && listener != null) {
                    listener.onDesconexion();
                }
            }
            conectado = false;
            if (heartbeatTimer != null) heartbeatTimer.cancel();
        });
        t.setDaemon(true);
        t.start();
    }

    private void iniciarHeartbeat() {
        heartbeatTimer = new Timer(true);
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    if (conectado && username != null) {
                        enviar(new Heartbeat());
                    }
                } catch (Exception e) {
                    cancel();
                }
            }
        }, 30000, 30000);
    }

    private void procesarMensaje(Mensaje msg) {
        if (listener == null) return;
        switch (msg.getTipo()) {
            case REGISTER_RES: {
                RegisterRes r = (RegisterRes) msg;
                listener.onRegistro(r.getExito(), r.getRespuesta());
                break;
            }
            case LOGIN_RES: {
                LoginRes r = (LoginRes) msg;
                if (r.getExito()) {
                    username = pendingUsername;
                }
                pendingUsername = null;
                listener.onLogin(r.getExito(), r.getRespuesta());
                break;
            }
            case LOGOUT_RES: {
                LogoutRes r = (LogoutRes) msg;
                if ("OK".equals(r.getRespuesta())) {
                    username = null;
                }
                listener.onLogout(r.getRespuesta());
                break;
            }
            case GET_CHANNELS_RES: {
                GetChannelsRes r = (GetChannelsRes) msg;
                listener.onCanales(r.getExito(), r.getRespuesta());
                break;
            }
            case JOIN_CHANNEL_RES: {
                JoinChannelRes r = (JoinChannelRes) msg;
                listener.onUnirCanal(r.getExito(), r.getRespuesta());
                break;
            }
            case LEAVE_CHANNEL_RES: {
                LeaveChannelRes r = (LeaveChannelRes) msg;
                listener.onSalirCanal(r.isOk(), r.getMensaje());
                break;
            }
            case SEND_CHANNEL_NOTIFICATION: {
                SendChannelNotification r = (SendChannelNotification) msg;
                listener.onMensajeCanal(r.getUsuario(), r.getDestino(), r.getContenido());
                break;
            }
            case SEND_PRIV_NOTIFICATION: {
                SendPrivNotification r = (SendPrivNotification) msg;
                listener.onMensajePrivado(r.getOrigen(), r.getDestino(), r.getContenido());
                break;
            }
            case HISTORY_RES: {
                HistoryRes r = (HistoryRes) msg;
                listener.onHistorial(r.getSalon(), r.getHistorial());
                break;
            }
            case NOTIFY_JOIN: {
                NotifyJoin r = (NotifyJoin) msg;
                listener.onNotifyJoin(r.getUsuarioUnido(), r.getNombreSalon());
                break;
            }
            case HEARTBEAT_ACK: {
                listener.onHeartbeatAck();
                break;
            }
            case ERROR_RES: {
                ErrorRes r = (ErrorRes) msg;
                listener.onError(r.getMensaje());
                break;
            }
            default:
                break;
        }
    }

    public void registrar(String nombre) throws Exception {
        enviar(new RegisterReq(nombre));
    }

    public void login(String nombre, String contrasena) throws Exception {
        pendingUsername = nombre;
        enviar(new LoginReq(nombre, contrasena));
    }

    public void logout() throws Exception {
        enviar(new LogoutReq(username != null ? username : ""));
    }

    public void getCanales() throws Exception {
        enviar(new GetChannelsReq());
    }

    public void unirCanal(String salon) throws Exception {
        enviar(new JoinChannelReq(salon));
    }

    public void salirCanal(String salon) throws Exception {
        enviar(new LeaveChannelReq(username != null ? username : "", salon));
    }

    public void enviarMensajeCanal(String salon, String texto) throws Exception {
        enviar(new SendChannelMsgReq(salon, texto, true));
    }

    public void enviarMensajePrivado(String destino, String texto) throws Exception {
        enviar(new SendChannelMsgReq(destino, texto, false));
    }

    public void pedirHistorial(String salon, long desdeTimestamp) throws Exception {
        enviar(new HistoryReq(username != null ? username : "", salon, desdeTimestamp));
    }

    public void desconectar() {
        conectado = false;
        if (heartbeatTimer != null) heartbeatTimer.cancel();
        try {
            socket.close();
        } catch (Exception ignored) {
        }
    }
}
