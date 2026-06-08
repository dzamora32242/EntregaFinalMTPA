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

    /**
     * Envía un mensaje al servidor de forma sincronizada a través del socket.
     * 
     * @param msg El mensaje que se va a enviar.
     * @throws Exception Si ocurre un error durante el envío por la red.
     */
    private synchronized void enviar(Mensaje msg) throws Exception {
        EnvioReciboMensajes.enviar(os, msg);
    }

    /**
     * Inicia un hilo en segundo plano (daemon) encargado de escuchar continuamente
     * los mensajes entrantes del servidor mientras la conexión esté activa.
     * Si se pierde la conexión, notifica al listener y detiene el heartbeat.
     */
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

    /**
     * Inicia un temporizador que envía periódicamente un mensaje de latido (Heartbeat)
     * al servidor para mantener la conexión viva y evitar desconexiones por inactividad.
     */
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

    /**
     * Recibe un mensaje emitido por el servidor y determina qué evento del 
     * listener debe ser disparado según el tipo de primitiva del mensaje.
     * 
     * @param msg El mensaje recibido desde el servidor a procesar.
     */
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

    /**
     * Envía al servidor una solicitud para registrar un nuevo nombre de usuario.
     * 
     * @param nombre El nombre del usuario que se desea registrar.
     * @throws Exception Si ocurre un error de red al enviar la solicitud.
     */
    public void registrar(String nombre) throws Exception {
        enviar(new RegisterReq(nombre));
    }

    /**
     * Envía una petición al servidor para iniciar sesión con las credenciales proporcionadas.
     * Guarda el nombre de usuario de forma temporal hasta confirmar el éxito del login.
     * 
     * @param nombre El nombre de usuario.
     * @param contrasena La contraseña asociada a la cuenta.
     * @throws Exception Si hay algún problema enviando la petición.
     */
    public void login(String nombre, String contrasena) throws Exception {
        pendingUsername = nombre;
        enviar(new LoginReq(nombre, contrasena));
    }

    /**
     * Envía al servidor una petición para cerrar la sesión activa del usuario actual.
     * 
     * @throws Exception Si hay algún fallo durante la comunicación con el servidor.
     */
    public void logout() throws Exception {
        enviar(new LogoutReq(username != null ? username : ""));
    }

    /**
     * Solicita al servidor la lista de todos los salones de chat disponibles actualmente.
     * 
     * @throws Exception Si ocurre un error en la transmisión de la solicitud.
     */
    public void getCanales() throws Exception {
        enviar(new GetChannelsReq());
    }

    /**
     * Envía una petición para unirse a un salón de chat específico.
     * 
     * @param salon El nombre del canal al que el usuario quiere entrar.
     * @throws Exception Si hay algún error en el envío.
     */
    public void unirCanal(String salon) throws Exception {
        enviar(new JoinChannelReq(salon));
    }

    /**
     * Notifica al servidor la intención de abandonar un salón de chat en el que se encuentra.
     * 
     * @param salon El nombre del salón que se va a dejar.
     * @throws Exception Si ocurre un error al enviar la petición.
     */
    public void salirCanal(String salon) throws Exception {
        enviar(new LeaveChannelReq(username != null ? username : "", salon));
    }

    /**
     * Envía un mensaje de texto público dirigido a todos los miembros de un salón de chat.
     * 
     * @param salon El nombre del salón de chat destino.
     * @param texto El contenido del mensaje a enviar.
     * @throws Exception Si se produce un error durante el envío.
     */
    public void enviarMensajeCanal(String salon, String texto) throws Exception {
        enviar(new SendChannelMsgReq(salon, texto, true));
    }

    /**
     * Envía un mensaje de texto privado dirigido exclusivamente a otro usuario conectado.
     * 
     * @param destino El nombre del usuario que recibirá el mensaje privado.
     * @param texto El contenido del mensaje.
     * @throws Exception Si ocurre un problema en la transmisión de datos.
     */
    public void enviarMensajePrivado(String destino, String texto) throws Exception {
        enviar(new SendChannelMsgReq(destino, texto, false));
    }

    /**
     * Solicita al servidor el historial de mensajes de un salón determinado, a partir de una fecha concreta.
     * 
     * @param salon El nombre del salón del cual se requiere el historial.
     * @param desdeTimestamp El momento de tiempo (en milisegundos) desde el que se solicitan los mensajes.
     * @throws Exception Si falla la comunicación de red.
     */
    public void pedirHistorial(String salon, long desdeTimestamp) throws Exception {
        enviar(new HistoryReq(username != null ? username : "", salon, desdeTimestamp));
    }

    /**
     * Cierra de forma controlada la conexión de red con el servidor, 
     * detiene el envío periódico de latidos (heartbeat) y actualiza el estado local del cliente.
     */
    public void desconectar() {
        conectado = false;
        if (heartbeatTimer != null) heartbeatTimer.cancel();
        try {
            socket.close();
        } catch (Exception ignored) {
        }
    }
}
